package utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Gọi OpenWeatherMap và trả JSON cho server UDP.
 * - getWeatherSummaryJson(city): hiện tại + 6 mốc forecast (3h/1 mốc)
 * - getForecast5DaysJson(city): tổng hợp 5 ngày (min/max/desc tiêu biểu)
 */
public class WeatherFetcher {

    // API key OWM của bạn
    private static final String API_KEY = "82d3534f31ed908a5b469dedc5e719ed";

    private static String fetch(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    /** Thời tiết hiện tại + 6 mốc forecast 3h */
    public static String getWeatherSummaryJson(String city) throws Exception {
        String q = URLEncoder.encode(city, StandardCharsets.UTF_8);

        // Hiện tại
        String curUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + q +
                "&appid=" + API_KEY + "&units=metric&lang=vi";
        JSONObject curJson = new JSONObject(fetch(curUrl));

        JSONObject result = new JSONObject();
        result.put("city", city);
        result.put("temperature", Math.round(curJson.getJSONObject("main").getDouble("temp")));
        result.put("feels_like", Math.round(curJson.getJSONObject("main").getDouble("feels_like")));
        result.put("humidity", curJson.getJSONObject("main").getInt("humidity") + "%");
        result.put("pressure", curJson.getJSONObject("main").getInt("pressure") + " hPa");
        result.put("wind", curJson.getJSONObject("wind").getDouble("speed") + " m/s");
        result.put("description", curJson.getJSONArray("weather").getJSONObject(0).getString("description"));

        // Forecast 5 ngày (3h/mốc) – lấy 6 mốc đầu để hiển thị nhanh
        String foreUrl = "https://api.openweathermap.org/data/2.5/forecast?q=" + q +
                "&appid=" + API_KEY + "&units=metric&lang=vi";
        JSONObject foreJson = new JSONObject(fetch(foreUrl));
        JSONArray list = foreJson.getJSONArray("list");

        JSONArray forecasts = new JSONArray();
        for (int i = 0; i < Math.min(6, list.length()); i++) {
            JSONObject f = list.getJSONObject(i);
            JSONObject obj = new JSONObject();
            String dtTxt = f.getString("dt_txt"); // "2025-09-29 12:00:00"
            String hour = dtTxt.split(" ")[1].substring(0, 5);
            obj.put("hour", hour);
            obj.put("temp", Math.round(f.getJSONObject("main").getDouble("temp")));
            obj.put("feels_like", Math.round(f.getJSONObject("main").getDouble("feels_like")));
            obj.put("desc", f.getJSONArray("weather").getJSONObject(0).getString("description"));
            forecasts.put(obj);
        }
        result.put("forecast", forecasts);

        return result.toString();
    }

    /** Dự báo 5 ngày: tổng hợp min/max và mô tả tiêu biểu theo từng ngày */
    public static String getForecast5DaysJson(String city) throws Exception {
        String q = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String foreUrl = "https://api.openweathermap.org/data/2.5/forecast?q=" + q +
                "&appid=" + API_KEY + "&units=metric&lang=vi";
        JSONObject foreJson = new JSONObject(fetch(foreUrl));
        JSONArray list = foreJson.getJSONArray("list");

        // Map yyyy-MM-dd -> thống kê (min, max, đếm mô tả)
        Map<String, DayAgg> agg = new LinkedHashMap<>();
        for (int i = 0; i < list.length(); i++) {
            JSONObject f = list.getJSONObject(i);
            String[] parts = f.getString("dt_txt").split(" "); // 0: date, 1: time
            String day = parts[0];
            double t = f.getJSONObject("main").getDouble("temp");
            String desc = f.getJSONArray("weather").getJSONObject(0).getString("description").toLowerCase();

            agg.computeIfAbsent(day, k -> new DayAgg()).add(t, desc);
        }

        JSONArray days = new JSONArray();
        for (Map.Entry<String, DayAgg> e : agg.entrySet()) {
            JSONObject d = new JSONObject();
            d.put("date", e.getKey());
            d.put("min", Math.round(e.getValue().min));
            d.put("max", Math.round(e.getValue().max));
            d.put("desc", e.getValue().topDesc());
            days.put(d);
            if (days.length() == 5) break; // chỉ lấy 5 ngày
        }

        JSONObject result = new JSONObject();
        result.put("city", city);
        result.put("days", days);
        return result.toString();
    }

    // Helper tích lũy min/max và mô tả phổ biến nhất
    private static class DayAgg {
        double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
        Map<String, Integer> counts = new HashMap<>();
        void add(double temp, String desc) {
            min = Math.min(min, temp);
            max = Math.max(max, temp);
            counts.put(desc, counts.getOrDefault(desc, 0) + 1);
        }
        String topDesc() {
            return counts.entrySet().stream()
                    .max(Comparator.comparingInt(Map.Entry::getValue))
                    .map(Map.Entry::getKey).orElse("");
        }
    }
}
