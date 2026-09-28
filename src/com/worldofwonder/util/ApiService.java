package com.worldofwonder.util;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ApiService {

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(4))
            .build();

    private static File cacheDir() {
        File dir = new File("data/cache/avatars");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static <T> void executeAsync(HttpRequest request, AsyncHandler<T> handler, Consumer<T> onSuccess, Consumer<Exception> onError) {
        SwingWorker<T, Void> worker = new SwingWorker<>() {
            @Override
            protected T doInBackground() throws Exception {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    return handler.handle(response.body());
                } else {
                    throw new RuntimeException("HTTP Error: " + response.statusCode());
                }
            }

            @Override
            protected void done() {
                try {
                    T result = get();
                    if (onSuccess != null) {
                        onSuccess.accept(result);
                    }
                } catch (Exception e) {
                    if (onError != null) {
                        onError.accept(e instanceof java.util.concurrent.ExecutionException ? (Exception) e.getCause() : e);
                    }
                }
            }
        };
        worker.execute();
    }

    private interface AsyncHandler<T> {
        T handle(String responseBody) throws Exception;
    }

    // A. OpenTDB Trivia API
    @SuppressWarnings("unchecked")
    // B. Datamuse Word API
    // C. Fun Facts (Numbers API)
    // D. Random Fun Fact (Useless Facts API)
    public static void fetchRandomFact(Consumer<String> onSuccess, Consumer<Exception> onError) {
        String url = "https://uselessfacts.jsph.pl/api/v2/facts/random?language=en";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(6))
                .GET()
                .build();
        executeAsync(request, body -> {
            Map<String, Object> map = JsonUtil.parseObject(body);
            if (map.containsKey("text")) return (String) map.get("text");
            throw new Exception("Invalid Useless Facts response");
        }, onSuccess, onError);
    }

    // E. DiceBear Avatar
    public static void fetchAvatar(String seed, int size, Consumer<BufferedImage> onSuccess, Consumer<Exception> onError) {
        SwingWorker<BufferedImage, Void> worker = new SwingWorker<>() {
            @Override
            protected BufferedImage doInBackground() throws Exception {
                File dir = cacheDir();
                File cacheFile = new File(dir, seed + ".png");
                if (cacheFile.exists()) {
                    try {
                        return ImageIO.read(cacheFile);
                    } catch (Exception ignored) { }
                }
                
                String encodedSeed = URLEncoder.encode(seed, StandardCharsets.UTF_8);
                String urlStr = String.format("https://api.dicebear.com/9.x/adventurer/png?seed=%s&size=%d", encodedSeed, size);
                
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlStr))
                        .timeout(Duration.ofSeconds(6))
                        .GET()
                        .build();
                        
                HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
                if (response.statusCode() == 200) {
                    Files.write(cacheFile.toPath(), response.body());
                    return ImageIO.read(cacheFile);
                } else {
                    throw new RuntimeException("HTTP Error fetching avatar: " + response.statusCode());
                }
            }

            @Override
            protected void done() {
                try {
                    BufferedImage img = get();
                    if (onSuccess != null) onSuccess.accept(img);
                } catch (Exception e) {
                    if (onError != null) onError.accept(e instanceof java.util.concurrent.ExecutionException ? (Exception) e.getCause() : e);
                }
            }
        };
        worker.execute();
    }

    // F. Wikipedia Summary
    @SuppressWarnings("unchecked")
    public static void fetchWikiSummary(String title, Consumer<Map<String, String>> onSuccess, Consumer<Exception> onError) {
        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
        String url = String.format("https://en.wikipedia.org/api/rest_v1/page/summary/%s", encodedTitle);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "WorldOfWonderApp/1.0")
                .timeout(Duration.ofSeconds(6))
                .GET()
                .build();
        executeAsync(request, body -> {
            Map<String, Object> map = JsonUtil.parseObject(body);
            Map<String, String> res = new HashMap<>();
            res.put("title", (String) map.get("title"));
            res.put("extract", (String) map.get("extract"));
            res.put("description", (String) map.get("description"));
            
            if (map.containsKey("thumbnail")) {
                Map<String, Object> thumb = (Map<String, Object>) map.get("thumbnail");
                res.put("thumbnail", (String) thumb.get("source"));
            } else {
                res.put("thumbnail", null);
            }
            return res;
        }, onSuccess, onError);
    }

    // G. Dreamlo Leaderboard
    @SuppressWarnings("unchecked")
    public static void fetchLeaderboard(String publicCode, int limit, Consumer<List<Map<String, Object>>> onSuccess, Consumer<Exception> onError) {
        String url = String.format("http://dreamlo.com/lb/%s/json/%d", publicCode, limit);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(6))
                .GET()
                .build();
        executeAsync(request, body -> {
            Map<String, Object> map = JsonUtil.parseObject(body);
            if (map.containsKey("dreamlo")) {
                Map<String, Object> dl = (Map<String, Object>) map.get("dreamlo");
                if (dl.containsKey("leaderboard")) {
                    Object lbObj = dl.get("leaderboard");
                    if (lbObj instanceof Map) {
                        Map<String, Object> lb = (Map<String, Object>) lbObj;
                        if (lb.containsKey("entry")) {
                            Object entry = lb.get("entry");
                            if (entry instanceof List) {
                                return (List<Map<String, Object>>) entry;
                            } else if (entry instanceof Map) {
                                List<Map<String, Object>> list = new ArrayList<>();
                                list.add((Map<String, Object>) entry);
                                return list;
                            }
                        }
                    }
                }
            }
            return new ArrayList<>();
        }, onSuccess, onError);
    }
}
