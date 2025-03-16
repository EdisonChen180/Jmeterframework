package com.example.framework.jmeter.generator;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * URL处理工具类
 */
public class UrlUtils {
    
    /**
     * 从URL中提取域名
     * @param url 完整URL
     * @return 域名
     */
    public static String extractDomain(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain != null ? domain : "";
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }
    }
    
    /**
     * 从URL中提取路径
     * @param url 完整URL
     * @return 路径
     */
    public static String extractPath(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            String query = uri.getQuery();
            
            if (path == null || path.isEmpty()) {
                path = "/";
            }
            
            if (query != null && !query.isEmpty()) {
                path = path + "?" + query;
            }
            
            return path;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }
    }
}