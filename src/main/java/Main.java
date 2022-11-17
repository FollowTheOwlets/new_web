
import java.io.BufferedOutputStream;

import java.util.logging.Handler;

public class Main {
    public static void main(String[] args) {
        final var server = new Server();
        // код инициализации сервера (из вашего предыдущего ДЗ)

        // добавление handler'ов (обработчиков)
        // Я так понимаю ошибки задуманы, так как в задании указано, что это функтор
        server.addHandler("GET", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                // TODO: handlers code
            }
        });
        server.addHandler("POST", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                // TODO: handlers code
            }
        });

        server.listen(9999);
    }
}
