package com.study.servlet;

import com.study.http.HttpRequest;
import com.study.http.HttpResponse;

import java.io.File;

public abstract class HttpServlet {
    public abstract void service(HttpRequest request, HttpResponse response);

    public void forward(String path, HttpRequest request, HttpResponse response) {
        File file = new File("src/main/webapp" + path);
        response.setEntity(file);
    }

}
