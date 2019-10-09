package com.study.servlet;

import com.study.http.HttpRequest;
import com.study.http.HttpResponse;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class RegServlet extends HttpServlet {
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        //获取用户提交的注册信息
        String username = request.getParameters("username");
        String password = request.getParameters("password");
        String phone = request.getParameters("phone");
        int level = Integer.parseInt(request.getParameters("level"));

        //如果用户名和密码都为空跳转回注册页面
        if (username == null && password == null) {
            forward("/myweb/reg.html", request, response);
            return;
        }

        try (RandomAccessFile randomAccessFile = new RandomAccessFile("user.dat", "rw")) {
            //读取user.dat文件，如果已有用户名，提示用户注册的用户名已存在
            for (int i = 0; i < randomAccessFile.length() / 100; i++) {
                randomAccessFile.seek(i * 100);
                byte[] data = new byte[32];
                randomAccessFile.read(data);
                String name = new String(data, "utf-8").trim();
                if (name.equals(username)) {
                    forward("/myweb/reg_repeat.html", request, response);
                    return;
                }
            }

            //将注册信息写入文件保存
            randomAccessFile.seek(randomAccessFile.length());
            //写入用户名
            byte[] data = username.getBytes("utf-8");
            data = Arrays.copyOf(data, 32);
            randomAccessFile.write(data);
            //写入密码
            data = password.getBytes("utf-8");
            data = Arrays.copyOf(data, 32);
            randomAccessFile.write(data);
            //写入手机号码
            data = phone.getBytes("utf-8");
            data = Arrays.copyOf(data, 32);
            randomAccessFile.write(data);
            //写入等级
            randomAccessFile.writeInt(level);

            //响应客户端注册结果
            forward("/myweb/reg_success.html", request, response);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
