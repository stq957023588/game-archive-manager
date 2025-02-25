package com.fool.gamearchivemanager.module.file;

import com.fool.gamearchivemanager.entity.dto.AccountDTO;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

public interface FileTransferProtocolAction {

    void doAction(ChannelHandlerContext ctx, Object msg, Map<String, String> header, AccountDTO user);

}
