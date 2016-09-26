package org.openmf.mifos.dataimport.http;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SimpleHttpRequest {

    private static final int HTTP_TIMEOUT = 100 * 1000; // 100 secs
	
    private final HttpURLConnection connection;

    public static enum Method {
        GET, POST;
    }

    public SimpleHttpRequest(String url, Method method, Map<String, String> headers, String content) throws IOException {
        URL obj = new URL(url);
        connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod(method.name());
        connection.setReadTimeout(HTTP_TIMEOUT);
        connection.setUseCaches(false);
        for (Entry<String, String> header : headers.entrySet()) {
            connection.addRequestProperty(header.getKey(), header.getValue());
        }
        if (content != null) {
            connection.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(),"UTF-8");
            out.write(content);
            close(out);
        }
    }

    private void close(OutputStreamWriter out) throws IOException {
        
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
                out.close();
        }
    }
    
    public SimpleHttpRequest(String url, Map<String, String> headers, String content) throws IOException {
        this(url, Method.GET, headers, content);
    }
    
    public SimpleHttpRequest(String url, String content) throws IOException {
        this(url, Method.GET, new HashMap<String, String>(), content);
    }
    
    public SimpleHttpRequest(String url) throws IOException {
        this(url, Method.GET, new HashMap<String, String>(), null);
    }

    public int status() throws IOException {
        return connection.getResponseCode();
    }

    public HttpURLConnection getConnection() throws IOException {
        return connection;
        
    }

}