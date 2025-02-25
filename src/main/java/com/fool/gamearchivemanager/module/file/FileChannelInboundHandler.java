package com.fool.gamearchivemanager.module.file;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.fool.gamearchivemanager.config.security.JwtProperties;
import com.fool.gamearchivemanager.config.security.TokenClaimKey;
import com.fool.gamearchivemanager.entity.dto.AccountDTO;
import com.fool.gamearchivemanager.module.account.service.AccountService;
import com.fool.gamearchivemanager.util.JWTUtils;
import com.fool.gamearchivemanager.util.SpringContextUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.fool.gamearchivemanager.module.file.FileTransferProtocolState.*;
import static com.fool.gamearchivemanager.module.file.FileTransferProtocolActionConstant.*;

@Slf4j
public class FileChannelInboundHandler extends ChannelInboundHandlerAdapter {


    private final Map<String, FileTransferProtocolAction> actions;

    public FileChannelInboundHandler() {
        actions = new HashMap<>();
        actions.put(GET, new GetFileAction());
        actions.put(PUSH, new PushFileAction());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //获取客户端发送过来的消息
        ByteBuf byteBuf = (ByteBuf) msg;
        StringBuilder stringBuilder = new StringBuilder();
        Map<String, String> header = new HashMap<>();

        while (byteBuf.isReadable()) {
            byte b = byteBuf.readByte();
            if (b == '\r') {
                continue;
            }
            if (b == '\n' && stringBuilder.isEmpty()) {
                break;
            }
            if (b == '\n') {
                String string = stringBuilder.toString();
                stringBuilder.delete(0, stringBuilder.length());
                String[] split = string.split(":", 2);
                header.put(split[0].toLowerCase(Locale.ROOT), split.length > 1 ? split[1] : null);
                continue;
            }
            stringBuilder.append((char) b);
        }
        String token = header.get(FileTransferProtocolHeader.AUTHORIZATION);
        if (!StringUtils.hasText(token)) {
            log.error("Missing token!");
            ctx.writeAndFlush(Unpooled.copiedBuffer(NON_AUTH, CharsetUtil.UTF_8));
            return;
        }
        String username;
        try {
            Map<String, Claim> claimMap = checkAuth(token);
            username = claimMap.get(TokenClaimKey.USER_NAME).as(String.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.writeAndFlush(Unpooled.copiedBuffer(AUTH_ERROR, CharsetUtil.UTF_8));
            return;
        }

        AccountService accountService = SpringContextUtils.getBean(AccountService.class);
        AccountDTO user = accountService.getUser(username);
        if (user == null) {
            log.error("user info error!");
            ctx.writeAndFlush(Unpooled.copiedBuffer(AUTH_ERROR, CharsetUtil.UTF_8));
            return;
        }

        String action = header.get(FileTransferProtocolHeader.ACTION);
        if (!StringUtils.hasText(action)) {
            log.error("Unsupported action!");
            ctx.writeAndFlush(Unpooled.copiedBuffer(NON_ACTION, CharsetUtil.UTF_8));
            return;
        }

        action = action.toLowerCase(Locale.ROOT);
        FileTransferProtocolAction fileTransferProtocolAction = actions.get(action);
        if (fileTransferProtocolAction == null) {
            ctx.writeAndFlush(UNSUPPORTED_ACTION);
            return;
        }
        try {
            fileTransferProtocolAction.doAction(ctx, byteBuf, header, user);
        } catch (Exception e) {
            ctx.write(Unpooled.copiedBuffer(ERROR, CharsetUtil.UTF_8));
            ctx.write(Unpooled.copiedBuffer("\n", CharsetUtil.UTF_8));
            ctx.write(Unpooled.copiedBuffer(e.getMessage(), CharsetUtil.UTF_8));
            ctx.flush();
        }
    }


    public Map<String, Claim> checkAuth(String authorization) {
        JwtProperties jwtProperties = SpringContextUtils.getBean(JwtProperties.class);
        try {
            return JWTUtils.parsingToken(jwtProperties.getSecret(), jwtProperties.getIssuer(), authorization);
        } catch (JWTVerificationException e) {
            throw new RuntimeException("netty server error! token verify failed", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("netty server error! unsupporte encoding", e);
        } catch (Exception e) {
            throw new RuntimeException("netty server error!", e);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel inactive!");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("File channel error!", cause);
    }
}
