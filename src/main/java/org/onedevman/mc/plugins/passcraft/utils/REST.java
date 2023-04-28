package org.onedevman.mc.plugins.passcraft.utils;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class REST {

    public static Gson GSON = new Gson();

    public enum METHOD {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE"),
        HEAD("HEAD"),
        OPTIONS("OPTIONS"),
        PATCH("PATCH");

        //

        private final String _method;

        //

        private METHOD(String method) {
            this._method = method;
        }

        //

        public String get() { return this._method; }

    }

    //

    public record Response(int code, String message, Map<String, String> headers, String content) {

        public Response(
            int code,
            String message,
            Map<String, String> headers,
            String content
        ) {
            this.code = code;
            this.message = message;
            this.headers = headers;
            this.content = content;
        }

    }

    //

    public static Response request(String endpoint, METHOD method, Map<String, String> headers) throws IOException {
        URL request_url = new URL(endpoint.toString());

        HttpURLConnection conn = (HttpURLConnection) request_url.openConnection();
        conn.setInstanceFollowRedirects(true);

        //

        conn.setRequestMethod(method.get());

        //

        List<String> header_name_list = new ArrayList<>(headers.keySet());
        int header_name_list_length = header_name_list.size();

        String header_name;
        for(int i = 0; i < header_name_list_length; ++i) {
            header_name = header_name_list.get(i);
            conn.setRequestProperty(header_name, headers.get(header_name));
        }

        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        //

        StringBuffer body_content_constructor = null;

        if(method != METHOD.HEAD) {
            InputStream conn_in = conn.getResponseCode() > 299 ? conn.getErrorStream() : conn.getInputStream();
            BufferedReader response_body_reader = new BufferedReader(new InputStreamReader(conn_in));

            body_content_constructor = new StringBuffer();
            String line;
            while ((line = response_body_reader.readLine()) != null) {
                body_content_constructor.append(line);
            }

            conn_in.close();
        }

        Map<String, String> response_headers = new HashMap<>();
        for (Map.Entry<String, List<String>> entries : conn.getHeaderFields().entrySet()) {
            StringBuilder value = new StringBuilder();

            List<String> value_parts = entries.getValue();
            int value_part_count = value_parts.size();

            for (int i = 0; i < value_part_count; ++i)
                value.append(value_parts.get(i)).append(i < value_part_count - 1 ? "," : "");

            response_headers.put(entries.getKey(), value.toString());
        }

        conn.disconnect();

        return new Response(
            conn.getResponseCode(),
            conn.getResponseMessage(),
            response_headers,
            body_content_constructor == null ? null : body_content_constructor.toString()
        );
    }

}
