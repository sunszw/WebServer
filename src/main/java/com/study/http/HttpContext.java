package com.study.http;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpContext {
    private static Map<String, String> mimeMapping = new HashMap<>();

    static {
        initMimeMapping();
    }

    private static void initMimeMapping() {
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new File("conf/web.xml"));
            Element rootElement = document.getRootElement();
            List<Element> elements = rootElement.elements("mime-mapping");
            for (Element e : elements) {
                String key = e.elementTextTrim("extension");
                String value = e.elementTextTrim("mime-type");
                mimeMapping.put(key, value);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static String getMimeType(String key) {
        return mimeMapping.get(key);
    }

}
