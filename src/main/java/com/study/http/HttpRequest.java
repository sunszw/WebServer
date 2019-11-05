package com.study.http;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    //与客户端连接相关的属性信息
    private Socket socket;
    //通过socket获取输入流用于读写客户端消息
    private InputStream inputStream;

    //请求行相关信息
    private String method;//请求方式
    private String url;//请求资源抽象路径
    private String protocol;//请求协议版本

    //抽象路径中的请求部分
    private String requestURI;
    //抽象路径中的参数部分
    private String queryString;
    //参数键值对
    private Map<String, String> parameters = new HashMap<>();
    //请求头相关信息
    private Map<String, String> requestHeaders = new HashMap<>();
    //请求体相关信息(本项目暂无)

    //初始化请求对象
    public HttpRequest(Socket socket) throws EmptyRequestException {
//        System.out.println("开始解析请求...");
        try {
            this.socket = socket;
            this.inputStream = socket.getInputStream();

            //1.解析请求行
            parseRequestLine();
            //2.解析请求头
            parseRequestHeaders();
            //3.解析请求体
            parseRequestBody();

        } catch (EmptyRequestException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("请求解析完毕.");
    }

    //解析请求行
    private void parseRequestLine() {
//        System.out.println("开始解析请求行...");
        //读取请求行的内容
        try {
            String line = readLine();
            //判断请求是否为空请求
            if ("".equals(line)) {
                throw new EmptyRequestException();
            }
            //拆分请求行
            String[] data = line.split("\\s");
            method = data[0];
            url = data[1];
            protocol = data[2];
            //进一步解析抽象路径
            parseURL(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        System.out.println("请求行解析完毕.");
    }

    //进一步解析抽象路径
    private void parseURL(String url) {
        //判断url中是否含有"?"，来确定是否含有参数
        if (url.contains("?")) {
            // 如果有参数按照"?"拆分为两部分，赋值给requestURI和queryString
            String[] data = url.split("\\?");
            requestURI = data[0];
            if (data.length > 1) {
                queryString = data[1];
                try {
                    //处理如果参数含有中文乱码问题
                    queryString = URLDecoder.decode(queryString, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //按照"&"拆分出每个参数，在按照"="拆分为参数名和参数值，保存到parameters中
                data = queryString.split("&");
                for (String parameter : data) {
                    String[] str = parameter.split("=");
                    if (str.length > 1) {
                        parameters.put(str[0], str[1]);
                    } else {
                        parameters.put(str[0], null);
                    }
                }
            }

        } else {
            //如果不含参数则直接将url的值赋给requestURI
            requestURI = url;
        }
    }

    //解析请求头
    private void parseRequestHeaders() {
//        System.out.println("开始解析请求头...");
        try {
            //循环调用readLine方法读取每一个请求头，如果readLine方法返回的是一个空字符串时，说明这次读取到了CRLF，那么循环就停止
            while (true) {
                String line = readLine();
                if ("".equals(line)) {
                    break;
                }
                //每当读取到一个请求头后就可以按照“冒号空格”拆分，拆出的内容就是请求头的名字和对应的值
                String[] data = line.split(": ");
                //将请求头以键值对形式保存
                requestHeaders.put(data[0], data[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("请求头解析完毕.");
    }

    private void parseRequestBody() {
//        System.out.println("开始解析请求体...");

//        System.out.println("本项目暂无请求体.");

//        System.out.println("请求体解析完毕.");
    }

    //读取传输的内容
    private String readLine() throws Exception {
        try {
            inputStream = socket.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();
            int d = 0;
            //c1表示上次读到的字符，c2表示本次读到的字符
            char c1 = 'a';
            char c2 = 'a';
            while ((d = inputStream.read()) != -1) {
                c2 = (char) d;
                if (c1 == 13 && c2 == 10) {
                    break;
                }
                stringBuilder.append(c2);
                c1 = c2;
            }
            return stringBuilder.toString().trim();
        } catch (Exception e) {
            throw e;
        }
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getRequestHeaders(String key) {
        return requestHeaders.get(key);
    }

    public String getParameters(String key) {
        return parameters.get(key);
    }


}
