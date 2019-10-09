package com.study.core;

import com.study.http.EmptyRequestException;
import com.study.http.HttpRequest;
import com.study.http.HttpResponse;
import com.study.http.ServerContext;
import com.study.servlet.HttpServlet;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            //1.解析请求
            HttpRequest request=new HttpRequest(socket);
            HttpResponse response=new HttpResponse(socket);
            //2.处理请求
            String path=request.getRequestURI();
            //判断是否为请求业务
            HttpServlet servlet= ServerContext.getServlet(path);
            if (servlet!=null){
                servlet.service(request,response);
            }else {
                File file=new File("src/main/webapp"+path);
                if (file.exists()){

                    response.setEntity(file);
                }else {
                    File notFound=new File("src/main/webapp/root/404.html");
                    response.setStatusCode(404);
                    response.setStatusReason("NOT FOUND");
                    response.setEntity(notFound);
                }
            }
            //3.响应客户端
            response.flush();

        } catch (EmptyRequestException e) {
            System.out.println("空请求...");
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
