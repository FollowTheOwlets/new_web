
public class Main {
    public static void main(String[] args) {
        final var server = new Server();
        var port = 9999;
        Request.host = "http://localhost:" + port;

        // добавление handler'ов (обработчиков)
        // Handler просил переопределение 4-х методов поэтому свой интерфейс сделал
        server
                .addHandler("GET", "/messages", (request, responseStream) -> {
                    responseStream.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Length: 0\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    responseStream.flush();
                })
                .addHandler("POST", "/messages", (request, responseStream) -> {
                    responseStream.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Length: 0\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    responseStream.flush();
                });

        server.listen(port);
    }
}
