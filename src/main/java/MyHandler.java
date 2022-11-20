import java.io.BufferedOutputStream;
import java.io.IOException;

public interface MyHandler {
     void handle(Request request, BufferedOutputStream responseStream)  throws IOException;
}
