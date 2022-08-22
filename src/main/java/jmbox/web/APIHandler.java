package jmbox.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import jmbox.audio.Converter;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APIHandler implements HttpHandler {
    private HttpExchange exchange;
    private static final Pattern REGEX = Pattern.compile("(\\d+)?-(\\d+)?");
    private static final Logger logger = Logger.getLogger("API");

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        this.exchange = exchange;
        new Thread(() -> {
            try {
                logger.info(String.format("%s %s %s", exchange.getRemoteAddress(), exchange.getRequestMethod(), exchange.getRequestURI()));
                if (exchange.getRequestMethod().equals("GET")) {
                    String[] args = URLDecoder.decode(exchange.getRequestURI().toString(), "UTF-8").split("/");
                    switch (args[2]) {
                        case "play":
                            play(new File(buildPath(args)));
                            return;
                        case "list":
                            list(new File(buildPath(args)));
                            return;
                    }
                    send(200, "OK");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    private void list(File file) throws IOException {
        File[] list = file.listFiles(pathname -> pathname.toString().toLowerCase().endsWith(".mid") || pathname.toString().toLowerCase().endsWith(".midi") || pathname.isDirectory());
        if (list == null) {
            send(404, "Not Found");
            return;
        }
        JsonArray arr = new JsonArray();
        for (File file1 : list) {
            JsonObject fo = new JsonObject();
            fo.addProperty("name", file1.getName());
            fo.addProperty("size", file1.length());
            fo.addProperty("isDir", file1.isDirectory());
            arr.add(fo);
        }
        byte[] b = arr.toString().getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json;charset=UTF-8");
        exchange.sendResponseHeaders(200, b.length);
        exchange.getResponseBody().write(b);
        exchange.getResponseBody().close();
    }

    public void play(File file) throws IOException {
        Converter c = new Converter(file);
        try {
            AudioInputStream is = c.convert();
            long length = is.getFrameLength() * is.getFormat().getFrameSize() + 44;
            Headers response = exchange.getResponseHeaders();
            response.set("Content-Type", "audio/x-wav");

            Headers request = exchange.getRequestHeaders();
            if (request.get("Range") != null) {
                String range = request.get("Range").get(0);
                Matcher m = REGEX.matcher(range);
                if (m.find()) {
                    long skiplen = Math.max(Long.parseLong(m.group(1)) - 44, 0);
                    is.skip(skiplen);

                    response.set("Content-Range", String.format("bytes %d-%d/%d", skiplen, length - 1, length));
                    exchange.sendResponseHeaders(206, length - skiplen);
                    System.out.println(length - skiplen);
                    AudioSystem.write(is, AudioFileFormat.Type.WAVE, exchange.getResponseBody());
                    return;
                }
            }

            response.set("Content-Range", String.format("bytes %d-%d/%d", 0, length - 1, length));
            exchange.sendResponseHeaders(206, length);

            AudioSystem.write(is, AudioFileFormat.Type.WAVE, exchange.getResponseBody());
        } catch (UnsupportedAudioFileException e) {
            logger.warning(e.toString());
            e.printStackTrace();
            send(500, "Internal Server Error");
        } catch (FileNotFoundException e) {
            logger.warning(e.toString());
            send(404, "Not Found");
        } catch (IOException e) {
            logger.warning(e.toString());
        } finally {
            exchange.close();
        }
    }

    private void send(int statusCode, String html) throws IOException {
        exchange.sendResponseHeaders(statusCode, html.getBytes().length);
        exchange.getResponseBody().write(html.getBytes());
        exchange.getResponseBody().close();
    }

    private String buildPath(String[] args) {
        StringBuilder builder = new StringBuilder("./");
        for (int i = 3; i < args.length; i++) {
            String path = args[i];
            if (!path.equals("..") && !path.equals("")) {
                builder.append(args[i]).append("/");
            }
        }
        return builder.toString();
    }
}
