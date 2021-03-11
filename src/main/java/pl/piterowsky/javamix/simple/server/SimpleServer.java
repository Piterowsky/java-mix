package pl.piterowsky.javamix.simple.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Log4j2
public class SimpleServer {

    private static final Integer PORT = 8080;

    private final HttpServer server;

    public SimpleServer() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        this.server.setExecutor(Executors.newFixedThreadPool(5));
    }

    public static void main(String[] args) throws IOException {
        var server = new SimpleServer();
        server.get("/hello", (httpExchange, params) -> {
            var simplePojo = new SimplePojo();
            simplePojo.setHello("Hello " + params.getOrDefault("name", "World"));
            simplePojo.setAge(12);
            return simplePojo;
        });
        server.start();
    }

    private <T> void handleResponse(HttpExchange httpExchange, T responseObject) throws IllegalAccessException, IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        var json = JsonParser.convertToJson(responseObject);
        httpExchange.getResponseHeaders().add("Content-type", "application/json");
        httpExchange.sendResponseHeaders(200, json.length());
        outputStream.write(json.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }

    public void start() {
        this.server.start();
        log.info("Server started at port: {}", PORT);
    }

    public <T> void get(String path, BiFunction<HttpExchange, Map<String, String>, T> function) {
        server.createContext(path, httpExchange -> {
            if (HttpMethod.GET.matches(httpExchange.getRequestMethod())) {
                var uri = httpExchange.getRequestURI().toString();
                var responseObject = function.apply(httpExchange, getParamsMap(uri));
                try {
                    handleResponse(httpExchange, responseObject);
                    return;
                } catch (Exception e) {
                    log.error("Exception occurred while preparing response");
                }
            }
            log.error("Only GET method can be handled");
        });
    }

    private Map<String, String> getParamsMap(String uri) {
        if (uri.contains("?")) {
            var paramsStr = uri.split("\\?")[1];
            if (paramsStr.contains("=")) {
                var paramsArray = paramsStr.split("=");
                return paramsArray.length >= 2
                        ? Collections.singletonMap(paramsArray[0], paramsArray[1])
                        : Collections.emptyMap();
            }
        }
        return Collections.emptyMap();
    }

    public void post(String path, Consumer<HttpExchange> consumer) {
        server.createContext(path, httpExchange -> {
            if (HttpMethod.GET.matches(httpExchange.getRequestMethod())) {
                consumer.accept(httpExchange);
            }
        });
    }

}

@Data
class SimplePojo {

    private String hello;
    private Integer age;

}

enum HttpMethod {
    GET, POST, UPDATE, DELETE;

    public boolean matches(String method) {
        return this.toString().equals(method);
    }
}

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class JsonParser {

    public static String convertToJson(Object object) throws IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        convertObjectToJson(object, sb);

        // Removes last comma
        sb.deleteCharAt(sb.toString().length() - 1);
        sb.append("}");
        return sb.toString();
    }

    private static String convertObjectToJson(Object object, StringBuilder sb) throws IllegalAccessException {
        Field[] allFields = object.getClass().getDeclaredFields();
        for (Field field : allFields) {
            field.setAccessible(true);
            if (String.class.isAssignableFrom(field.getType())) {
                sb.append(String.format("\"%s\": \"%s\"", field.getName(), field.get(object)));
            } else if (Number.class.isAssignableFrom(field.getType())) {
                sb.append(String.format("\"%s\": %s", field.getName(), field.get(object)));
            } else if (Collection.class.isAssignableFrom(field.getType())) {
                // handle collections
            } else {
                convertObjectToJson(object, sb);
            }
            sb.append(",");
        }
        return sb.toString();
    }

}
