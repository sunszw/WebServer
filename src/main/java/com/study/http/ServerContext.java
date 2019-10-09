package com.study.http;

import com.study.servlet.HttpServlet;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerContext {
    private static Map<String, HttpServlet> servletMapping = new HashMap<>();

    static {
        initServletMapping();
    }

    private static void initServletMapping() {
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new File("conf/servlets.xml"));
            Element rootElement = document.getRootElement();
            List<Element> elements = rootElement.elements("servlet");
            for (Element e : elements) {
                String key = e.attributeValue("path");
                String name = e.attributeValue("className");
                Class cls = Class.forName(name);
                HttpServlet value = (HttpServlet) cls.newInstance();
                servletMapping.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HttpServlet getServlet(String path) {
        return servletMapping.get(path);
    }

}
