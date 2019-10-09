package com.study.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpResponse {
    //与连接相关信息
    private Socket socket;
    private OutputStream outputStream;

    //状态行相关信息
    private int statusCode = 200;
    private String statusReason = "OK";
    //响应头相关信息
    private Map<String, String> responseHeaders = new HashMap<>();
    //响应体相关信息
    private File entity;

    //初始化响应对象
    public HttpResponse(Socket socket) {
        this.socket = socket;

        try {
            this.outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //将当前响应对象以标准HTTP响应格式发送给客户端
    public void flush() {
        sendStatusLine();
        sendResponseHeaders();
        sendResponseBody();
    }

    //发送状态行
    private void sendStatusLine() {
//        System.out.println("开始发送状态行...");
        try {
            String line = "HTTP/1.1" + " " + statusCode + " " + statusReason;
            outputStream.write(line.getBytes("ISO8859-1"));
            outputStream.write(13);
            outputStream.write(10);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("状态行发送完毕.");
    }

    //发送响应头
    private void sendResponseHeaders() {
//        System.out.println("开始发送响应头...");
        try {
            Set<Entry<String, String>> entrySet = responseHeaders.entrySet();
            for (Entry<String, String> entry : entrySet) {
                String key = entry.getKey();
                String value = entry.getValue();
                String line = key + ": " + value;
                outputStream.write(line.getBytes("ISO8859-1"));
                outputStream.write(13);
                outputStream.write(10);
            }
            //单独发送CRLF表示响应头发送完毕
            outputStream.write(13);
            outputStream.write(10);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("响应头发送完毕.");
    }

    public void putResponseHeader(String key, String value) {
        this.responseHeaders.put(key, value);
    }

    //发送响应体
    private void sendResponseBody() {
//        System.out.println("开始发送响应体...");
        if (entity != null) {
            try (FileInputStream fileInputStream = new FileInputStream(entity)) {
                int len = 0;
                byte[] data = new byte[1024 * 10];
                while ((len = fileInputStream.read(data)) != -1) {
                    outputStream.write(data, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        System.out.println("响应体发送完毕.");
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public File getEntity() {
        return entity;
    }

    public void setEntity(File entity) {
        this.entity = entity;
        //根据文件后缀名，获取该资源对应的文件类型
        String fileName = entity.getName();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        String type = HttpContext.getMimeType(suffix);
        putResponseHeader("Content-Type", type);
        putResponseHeader("Content-Length", entity.length() + "");

    }
}
