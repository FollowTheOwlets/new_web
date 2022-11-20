public class HandlerKey {
    protected final String method;
    protected final String path;

    public HandlerKey(String method, String path) {
        this.method = method;
        this.path = path;
    }

    @Override
    public String toString() {
        return this.method + ":/" + path;
    }
}
