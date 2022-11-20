import java.util.Map;

public class Request {
    final String method;
    final Map<String,String> headers;
    final String body;

    public Request(String method, Map<String,String> headers, String body) {
        this.method = method;
        this.headers = headers;
        this.body = body;
    }
}
