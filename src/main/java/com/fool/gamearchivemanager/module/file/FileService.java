package com.fool.gamearchivemanager.module.file;

import com.fool.gamearchivemanager.config.netty.NettyProperties;
import com.fool.gamearchivemanager.util.NettyFileServer;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class FileService {

    private NettyFileServer nettyFileServer;

    public FileService(NettyProperties nettyProperties) {
        this.nettyFileServer = new NettyFileServer(nettyProperties);
    }

    // @PostConstruct
    public void startNettyFileServer() {
        Thread thread = new Thread(nettyFileServer);
        thread.start();
    }


    public void saveToLocal(byte[] bytes, String path) {
        File file = new File(path);
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            boolean mkdirs = parentDir.mkdirs();
            if (!mkdirs) {
                throw new RuntimeException();
            }
        }

        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream getFile(String filePath) throws FileNotFoundException {
        return new FileInputStream(filePath);
    }

}
