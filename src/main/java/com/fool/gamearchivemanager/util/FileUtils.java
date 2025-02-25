package com.fool.gamearchivemanager.util;

import jakarta.servlet.ServletOutputStream;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class FileUtils {

    public static String[] list(String dirPath, String filenameRegex) {
        File[] files = listFiles(dirPath, filenameRegex);
        String[] filenames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            filenames[i] = files[i].getAbsolutePath();
        }
        return filenames;
    }


    public static File[] listFiles(String dirPath, String filenameRegex) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            return new File[0];
        }
        FilenameFilter filenameFilter = (d, name) -> filenameRegex == null || Pattern.compile(filenameRegex).matcher(name).find();
        return dir.listFiles(filenameFilter);
    }

    public static void write(InputStream in, OutputStream out) throws IOException {
        int len;
        byte[] bytes = new byte[1024];
        while ((len = in.read(bytes)) != -1) {
            out.write(bytes, 0, len);
            out.flush();
        }
    }

    public static void write(byte[] bytes, String path) {
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


    public static void deleteFile(String filepath) {
        File file = new File(filepath);
        boolean delete = file.delete();
        if (!delete) {
            log.warn("文件删除失败！文件地址：{}", filepath);
        }
    }


    public static void deleteFiles(String dirPath, String filenameRegex) {
        File[] files = listFiles(dirPath, filenameRegex);
        for (File file : files) {
            boolean delete = file.delete();
            if (!delete) {
                System.err.println("删除存档失败!");
            }
        }
    }
}
