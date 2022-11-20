import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Handler;

public class Server {
    protected Map<HandlerKey, Handler> handlers;

    public Server() {
        handlers = new HashMap<>();
    }

    public Server addHandler(String method, String path, Handler handler) {
        handlers.put(new HandlerKey(method, path), handler);
        return this;
    }

    public void listen(int port) {
        try (final var serverSocket = new ServerSocket(port)) {
            ExecutorService threadPool = Executors.newFixedThreadPool(64);
            while (true) {
                try (
                        final var socket = serverSocket.accept();
                        final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        final var out = new BufferedOutputStream(socket.getOutputStream());
                ) {
                    Future<Boolean> task = CompletableFuture.supplyAsync(
                            () -> connect(in, out),
                            threadPool
                    );
                    task.get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean connect(BufferedReader in, BufferedOutputStream out) {
        try {
            final String requestLine = in.readLine();
            //TODO:Что-то ответить при наличи handler'а
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
