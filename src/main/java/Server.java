import java.io.*;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    protected Map<String, MyHandler> handlers;
    public static final String GET = "GET";
    public static final String POST = "POST";
    public final List<String> allowedMethods = List.of(GET, POST);

    public Server() {
        handlers = new HashMap<>();
    }

    public Server addHandler(String method, String path, MyHandler handler) {
        handlers.put(HandlerKey.build(method, path), handler);
        return this;
    }

    public void listen(int port) {
        try (final var serverSocket = new ServerSocket(port)) {
            ExecutorService threadPool = Executors.newFixedThreadPool(64);
            while (true) {
                try (
                        final var socket = serverSocket.accept();
                        final var in = new BufferedInputStream(socket.getInputStream());
                        final var out = new BufferedOutputStream(socket.getOutputStream())
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

    public Boolean connect(BufferedInputStream in, BufferedOutputStream out) {
        try {
            // лимит на request line + заголовки
            final var limit = 4096;

            in.mark(limit);
            final var buffer = new byte[limit];
            final var read = in.read(buffer);

            // ищем request line
            final var requestLineDelimiter = new byte[]{'\r', '\n'};
            final var requestLineEnd = indexOf(buffer, requestLineDelimiter, 0, read);
            if (requestLineEnd == -1) {
                badRequest(out);
                return false;
            }

            // читаем request line
            final var requestLine = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");
            if (requestLine.length != 3) {
                badRequest(out);
                return false;
            }

            final var method = requestLine[0];
            if (!allowedMethods.contains(method)) {
                badRequest(out);
                return false;
            }
            System.out.println(method);

            final var path = requestLine[1];
            if (!path.startsWith("/")) {
                badRequest(out);
                return false;
            }
            System.out.println(path);

            // ищем заголовки
            final var headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'};
            final var headersStart = requestLineEnd + requestLineDelimiter.length;
            final var headersEnd = indexOf(buffer, headersDelimiter, headersStart, read);
            if (headersEnd == -1) {
                badRequest(out);
                return false;
            }

            // отматываем на начало буфера
            in.reset();
            // пропускаем requestLine
            in.skip(headersStart);

            final var headersBytes = in.readNBytes(headersEnd - headersStart);
            final var headers = Arrays.asList(new String(headersBytes).split("\r\n"));

            // для GET тела нет
            String body = "";
            if (!method.equals(GET)) {
                in.skip(headersDelimiter.length);
                // вычитываем Content-Length, чтобы прочитать body
                final var contentLength = extractHeader(headers, "Content-Length");
                if (contentLength.isPresent()) {
                    final var length = Integer.parseInt(contentLength.get());
                    final var bodyBytes = in.readNBytes(length);

                    body = new String(bodyBytes);
                }
            }

            String handlerKey = HandlerKey.build(method, path.substring(0, path.contains("?") ? path.indexOf("?") : path.length()));
            if (handlers.containsKey(handlerKey)) {
                if (method.equals(GET)) {
                    handlers.get(handlerKey).handle(new Request(method, path, headers), out);
                } else {
                    handlers.get(handlerKey).handle(new Request(method, path, headers, body), out);
                }

            } else {
                badRequest(out);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }

    private static void badRequest(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    // from google guava with modifications
    private static int indexOf(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }
}
