import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Request {
    public static String host;
    final String method;
    final String path;
    final List<String> headers;
    final String body;

    public Request(String method, String path, List<String> headers) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = "";
        System.out.println(getQueryParams());
    }

    public Request(String method, String path, List<String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }

    public List<NameValuePair> getQueryParams() {
        try {
            return URLEncodedUtils.parse(new URI(host + path), Charset.defaultCharset());
        } catch (URISyntaxException e) {
            throw new RuntimeException(host + path + " ошибка декодирования");
        }
    }

    public List<String> getQueryParam(String name) {
        List<String> list = new ArrayList<>();
        for (NameValuePair pair : getQueryParams()) {
            if (pair.getName().equals(name)) {
                list.add(pair.getValue());
            }
        }
        return list;
    }

    public List<NameValuePair> getPostParams() {
        String encodeBody = URLDecoder.decode(body, StandardCharsets.UTF_8);

        String[] pairs = encodeBody.split("&");
        NameValuePair[] nameValuePairs = new NameValuePair[pairs.length];
        for (int i = 0; i < pairs.length; i++) {
            String pair = pairs[i];
            nameValuePairs[i] = new NameValuePair() {
                @Override
                public String getName() {
                    return pair.substring(0, pair.indexOf("="));
                }

                @Override
                public String getValue() {
                    return pair.substring(pair.indexOf("=") + 1);
                }
            };
        }
        return List.of(nameValuePairs);
    }

    public List<String> getPostParam(String name) {
        List<String> list = new ArrayList<>();
        for (NameValuePair pair : getPostParams()) {
            if (pair.getName().equals(name)) {
                list.add(pair.getValue());
            }
        }
        return list;
    }
}
