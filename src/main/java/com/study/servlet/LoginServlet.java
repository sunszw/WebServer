package com.study.servlet;

import com.study.http.HttpRequest;
import com.study.http.HttpResponse;

import java.io.IOException;
import java.io.RandomAccessFile;

public class LoginServlet extends HttpServlet {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        String username = request.getParameters("username");
        String password = request.getParameters("password");
        //如果用户名和密码都为空跳转回登录页面
        if (username == null && password == null) {
            forward("/myweb/login.html", request, response);
            return;
        }

        try (RandomAccessFile randomAccessFile = new RandomAccessFile("user.dat", "r")) {
            for (int i = 0; i < randomAccessFile.length() / 100; i++) {
                randomAccessFile.seek(i * 100);
                byte[] data = new byte[32];
                randomAccessFile.read(data);
                String name = new String(data, "utf-8").trim();
                if (name.equals(username)) {
                    randomAccessFile.read(data);
                    String pwd = new String(data, "utf-8").trim();
                    if (pwd.equals(password)) {
                        forward("/myweb/login_success.html", request, response);
                        return;
                    }
                }
            }
            forward("/myweb/login_failed.html", request, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
