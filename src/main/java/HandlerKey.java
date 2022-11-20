public class HandlerKey {

    public static String build(String method, String path) {
        return method + ":/" + path;
    }
}
