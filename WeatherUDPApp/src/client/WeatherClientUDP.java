package client;

import org.json.JSONArray;
import org.json.JSONObject;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherClientUDP {

    private static final int SERVER_PORT = 5000;
    private static final int BUFFER_SIZE = 8192;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean darkMode = false;

    private final GradientPanel rootPanel = new GradientPanel();

    private JComboBox<String> cbCity;
    private JButton btnSearch, btnTheme;

    private JLabel lblTemp, lblIcon, lblStatus, lblAlert;
    private JPanel humPanel, windPanel, pressPanel, feelsPanel;
    private JPanel forecastPanelSmall, widgetChartSmall;

    private JPanel daysPanel;
    private JPanel widgetChart5d;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WeatherClientUDP::new);
    }

    public WeatherClientUDP() {
        JFrame frame = new JFrame("🌍 Weather Dashboard (UDP Client)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 760);
        frame.setLocationRelativeTo(null);

        rootPanel.setLayout(new BorderLayout(10, 10));
        rootPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.setContentPane(rootPanel);

        // ==== Header ====
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        header.setOpaque(false);

        JLabel title = new JLabel("🌍 Weather Dashboard");
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        cbCity = new JComboBox<>(new String[]{"Hà Nội", "Ho Chi Minh", "Đà Nẵng"});
        cbCity.setEditable(true);

        btnSearch = new JButton("🔍");
        btnTheme  = new JButton("🌙");

        header.add(title);
        header.add(cbCity);
        header.add(btnSearch);
        header.add(btnTheme);
        rootPanel.add(header, BorderLayout.NORTH);

        // ==== Tabs ====
        JTabbedPane tabs = new JTabbedPane();
        tabs.setOpaque(false);
        tabs.addTab("🌡️ Thời tiết hiện tại", buildCurrentTab());
        tabs.addTab("📅 Dự báo 5 ngày", build5DaysTab());
        tabs.addTab("🗺️ Bản đồ", buildMapTab(frame));
        rootPanel.add(tabs, BorderLayout.CENTER);

        // ==== Actions ====
        btnTheme.addActionListener(e -> {
            darkMode = !darkMode;
            rootPanel.setDarkMode(darkMode);
            rootPanel.repaint();
            btnTheme.setText(darkMode ? "☀️" : "🌙");
        });

        ActionListener doSearch = e -> {
            String city = cbCity.getSelectedItem().toString().trim();
            if (city.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Vui lòng nhập tên thành phố");
                return;
            }
            requestCurrent("localhost", city);
            requestForecast5("localhost", city);
        };
        btnSearch.addActionListener(doSearch);

        frame.setVisible(true);
    }

    // ============= Tab hiện tại =============
    private JPanel buildCurrentTab() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setOpaque(false);

        JPanel grid = new JPanel(new GridLayout(2, 2, 15, 15));
        grid.setOpaque(false);

        // Widget Current
        JPanel wCurrent = widget();
        wCurrent.setLayout(new BoxLayout(wCurrent, BoxLayout.Y_AXIS));
        lblTemp = new JLabel("--°", SwingConstants.CENTER);
        lblTemp.setFont(new Font("Arial", Font.BOLD, 84));
        lblTemp.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblIcon = new JLabel("❓");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 110));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblStatus = new JLabel("Chưa có dữ liệu");
        lblStatus.setFont(new Font("Arial", Font.BOLD, 28));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblAlert = new JLabel("⚠️ Không có cảnh báo", SwingConstants.CENTER);
        lblAlert.setFont(new Font("Segoe UI", Font.BOLD, 20)); // chữ rõ, emoji sẽ được nhúng qua HTML
        lblAlert.setForeground(Color.ORANGE);
        lblAlert.setAlignmentX(Component.CENTER_ALIGNMENT);

        wCurrent.add(Box.createVerticalGlue());
        wCurrent.add(lblTemp);
        wCurrent.add(lblIcon);
        wCurrent.add(lblStatus);
        wCurrent.add(lblAlert);
        wCurrent.add(Box.createVerticalGlue());

        // Widget Info
        JPanel wInfo = widget();
        wInfo.setLayout(new GridLayout(2, 2, 10, 10));
        humPanel   = infoItem("💧", "Độ ẩm: --");
        windPanel  = infoItem("🌬️", "Gió: --");
        pressPanel = infoItem("📊", "Áp suất: --");
        feelsPanel = infoItem("🌡️", "Feels like: --");
        wInfo.add(humPanel);
        wInfo.add(windPanel);
        wInfo.add(pressPanel);
        wInfo.add(feelsPanel);

        // Forecast ngắn
        forecastPanelSmall = widget();
        forecastPanelSmall.setLayout(new GridLayout(1, 6, 8, 8));

        // Chart nhỏ
        widgetChartSmall = widget();
        widgetChartSmall.setLayout(new BorderLayout());
        widgetChartSmall.setPreferredSize(new Dimension(0, 260));

        grid.add(wCurrent);
        grid.add(wInfo);
        grid.add(forecastPanelSmall);
        grid.add(widgetChartSmall);

        main.add(grid, BorderLayout.CENTER);
        return main;
    }

    // ============= Tab 5 ngày =============
    private JPanel build5DaysTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        daysPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        daysPanel.setOpaque(false);
        panel.add(daysPanel, BorderLayout.CENTER);

        widgetChart5d = widget();
        widgetChart5d.setLayout(new BorderLayout());
        widgetChart5d.setPreferredSize(new Dimension(0, 320));
        panel.add(widgetChart5d, BorderLayout.SOUTH);
        return panel;
    }

    // ============= Tab bản đồ =============
    private JPanel buildMapTab(JFrame frame) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        JButton openMap = new JButton("🗺️ Mở Google Maps cho thành phố đang chọn");
        openMap.addActionListener(e -> {
            try {
                String city = cbCity.getSelectedItem().toString().trim();
                if (!city.isEmpty()) {
                    String url = "https://www.google.com/maps/search/?api=1&query=" +
                            URLEncoder.encode(city, StandardCharsets.UTF_8);
                    Desktop.getDesktop().browse(URI.create(url));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Không mở được Maps: " + ex.getMessage());
            }
        });
        panel.add(openMap);
        return panel;
    }

    // ============= Helpers =============
    private JPanel widget() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 0));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 25, 25);
                g2.setColor(new Color(0, 0, 0, 50));
                g2.drawRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 25, 25);
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));
        return p;
    }

    private JPanel infoItem(String emoji, String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel icon = new JLabel(emoji, SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(Color.WHITE);
        panel.add(icon, BorderLayout.CENTER);
        panel.add(lbl, BorderLayout.SOUTH);
        return panel;
    }

    private void updateInfo(JPanel panel, String emoji, String text) {
        panel.removeAll();
        panel.setLayout(new BorderLayout());
        JLabel icon = new JLabel(emoji, SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(Color.WHITE);
        panel.add(icon, BorderLayout.CENTER);
        panel.add(lbl, BorderLayout.SOUTH);
        panel.revalidate();
        panel.repaint();
    }

    private String emojiFromDesc(String desc) {
        String d = desc == null ? "" : desc.toLowerCase();
        if (d.contains("mưa") || d.contains("rain") || d.contains("drizzle") || d.contains("shower") || d.contains("storm")) return "🌧️";
        if (d.contains("mây") || d.contains("cloud") || d.contains("overcast")) return "☁️";
        if (d.contains("nắng") || d.contains("clear") || d.contains("sun")) return "☀️";
        if (d.contains("tuyết") || d.contains("snow")) return "❄️";
        return "🌤️";
    }

    // ============= Network =============
    private interface JsonHandler { void accept(JSONObject json); }

    private void requestCurrent(String host, String city) {
        executor.submit(() -> {
            try (DatagramSocket socket = new DatagramSocket()) {
                sendAndHandle(socket, host, "current|" + city, this::handleCurrentResponse);
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null, "❌ Lỗi current: " + ex.getMessage()));
            }
        });
    }

    private void requestForecast5(String host, String city) {
        executor.submit(() -> {
            try (DatagramSocket socket = new DatagramSocket()) {
                sendAndHandle(socket, host, "forecast5|" + city, this::handleForecast5Response);
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null, "❌ Lỗi forecast5: " + ex.getMessage()));
            }
        });
    }

    private void sendAndHandle(DatagramSocket socket, String host, String message, JsonHandler handler) throws Exception {
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(host), SERVER_PORT);
        socket.send(packet);

        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
        socket.receive(response);
        String reply = new String(response.getData(), 0, response.getLength(), StandardCharsets.UTF_8);

        JSONObject json = new JSONObject(reply);
        SwingUtilities.invokeLater(() -> handler.accept(json));
    }

    // ============= Handle current weather =============
    private void handleCurrentResponse(JSONObject json) {
        String tempStr = json.optString("temperature", "--");
        String desc    = json.optString("description", "--");
        String humidity= json.optString("humidity", "--");
        String wind    = json.optString("wind", "--");
        String pressure= json.optString("pressure", "--");
        String feels   = json.optString("feels_like", "--");

        lblTemp.setText(tempStr + "°");
        lblIcon.setText(emojiFromDesc(desc));
        lblStatus.setText(desc);

        updateInfo(humPanel,  "💧", "Độ ẩm: " + humidity);
        updateInfo(windPanel, "🌬️", "Gió: " + wind);
        updateInfo(pressPanel,"📊", "Áp suất: " + pressure);
        updateInfo(feelsPanel,"🌡️", "Feels like: " + feels + "°");

        // ===== Cảnh báo (giữ emoji + chữ đẹp) =====
        String emoji = "✅";
        String text  = " Thời tiết ổn định";
        Color alertColor = new Color(0, 120, 0);

        try {
            int t = Integer.parseInt(tempStr);
            String dlow = desc.toLowerCase();

            if (dlow.contains("mưa") || dlow.contains("rain")) {
                emoji = "🌧️";
                text  = " Mang theo áo mưa!";
                alertColor = Color.BLUE;
            } else if (dlow.contains("nắng") || dlow.contains("clear")) {
                emoji = "☀️";
                text  = " Trời nắng, nhớ chống nắng!";
                alertColor = Color.ORANGE;
            }
            if (t > 35) { emoji = "🔥"; text = " Nóng gắt!"; alertColor = Color.RED; }
            if (t < 15) { emoji = "❄️"; text = " Trời lạnh, nhớ mặc ấm!"; alertColor = Color.CYAN.darker(); }
        } catch (Exception ignored) {}

        String alertHtml =
                "<html><div style='text-align:center;'>"
                        + "<span style='font-family:\"Segoe UI Emoji\";'>" + emoji + "</span>"
                        + "<span style='font-family:\"Segoe UI\"; font-weight:bold;'>" + text + "</span>"
                        + "</div></html>";

        lblAlert.setText(alertHtml);
        lblAlert.setForeground(alertColor);

        // ===== Forecast ngắn 6 mốc =====
        forecastPanelSmall.removeAll();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        JSONArray forecast = json.optJSONArray("forecast");
        if (forecast != null) {
            for (int i = 0; i < Math.min(6, forecast.length()); i++) {
                JSONObject f = forecast.getJSONObject(i);
                String hour  = f.optString("hour", "");
                String fTemp = f.optString("temp", "");
                String fFeel = f.optString("feels_like", "");
                String fDesc = f.optString("desc", "");

                try {
                    dataset.addValue(Double.parseDouble(fTemp), "Nhiệt độ", hour);
                    dataset.addValue(Double.parseDouble(fFeel), "Feels like", hour);
                } catch (Exception ignored) {}

                JPanel card = widget();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

                JLabel h  = new JLabel(hour, SwingConstants.CENTER);
                JLabel ic = new JLabel(emojiFromDesc(fDesc), SwingConstants.CENTER);
                ic.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 55));
                JLabel t  = new JLabel(fTemp + "°", SwingConstants.CENTER);
                t.setFont(new Font("Segoe UI", Font.BOLD, 22));
                JLabel fl = new JLabel("(Feels " + fFeel + "°)", SwingConstants.CENTER);

                h.setForeground(new Color(0,120,255));
                t.setForeground(Color.WHITE);
                fl.setForeground(new Color(0,120,255));

                h.setAlignmentX(Component.CENTER_ALIGNMENT);
                ic.setAlignmentX(Component.CENTER_ALIGNMENT);
                t.setAlignmentX(Component.CENTER_ALIGNMENT);
                fl.setAlignmentX(Component.CENTER_ALIGNMENT);

                card.add(Box.createVerticalGlue());
                card.add(h);
                card.add(ic);
                card.add(t);
                card.add(fl);
                card.add(Box.createVerticalGlue());

                forecastPanelSmall.add(card);
            }
        }
        forecastPanelSmall.revalidate();
        forecastPanelSmall.repaint();

        JFreeChart chart = ChartFactory.createLineChart("Dự báo nhiệt độ (6 mốc)", "Giờ", "°C", dataset);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(0,0,0,0));
        plot.getRangeAxis().setRange(0, 40);
        ChartPanel cp = new ChartPanel(chart);
        cp.setOpaque(false);

        widgetChartSmall.removeAll();
        widgetChartSmall.add(cp, BorderLayout.CENTER);
        widgetChartSmall.revalidate();
        widgetChartSmall.repaint();
    }

    // ============= Handle forecast 5 days =============
    private void handleForecast5Response(JSONObject json) {
        JSONArray days = json.optJSONArray("days");
        if (days == null) return;

        daysPanel.removeAll();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < days.length(); i++) {
            JSONObject d = days.getJSONObject(i);
            String date = d.optString("date", "");
            String desc = d.optString("desc", "");
            String min  = String.valueOf(d.optInt("min"));
            String max  = String.valueOf(d.optInt("max"));

            try {
                dataset.addValue(Double.parseDouble(min), "Min", date);
                dataset.addValue(Double.parseDouble(max), "Max", date);
            } catch (Exception ignored) {}

            JPanel card = widget();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

            JLabel dayLbl = new JLabel(date, SwingConstants.CENTER);
            dayLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
            dayLbl.setForeground(Color.WHITE);

            JLabel ic = new JLabel(emojiFromDesc(desc), SwingConstants.CENTER);
            ic.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));

            JLabel maxLbl = new JLabel(max + "°", SwingConstants.CENTER);
            maxLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
            maxLbl.setForeground(new Color(0, 120, 255)); // xanh

            JLabel minLbl = new JLabel(min + "°", SwingConstants.CENTER);
            minLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
            minLbl.setForeground(Color.RED); // đỏ

            JLabel descLbl = new JLabel(desc, SwingConstants.CENTER);
            descLbl.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            descLbl.setForeground(Color.WHITE);

            String forecastText = "Nhiều mây";
            Color forecastColor = Color.LIGHT_GRAY;
            String dlow = desc.toLowerCase();
            if (dlow.contains("mưa") || dlow.contains("rain")) {
                forecastText  = "Có mưa";
                forecastColor = Color.CYAN.darker();
            } else if (dlow.contains("nắng") || dlow.contains("clear") || dlow.contains("sun")) {
                forecastText  = "Nắng";
                forecastColor = Color.ORANGE;
            }
            JLabel forecastType = new JLabel(forecastText, SwingConstants.CENTER);
            forecastType.setFont(new Font("Segoe UI", Font.BOLD, 16));
            forecastType.setForeground(forecastColor);

            // Căn giữa toàn bộ
            dayLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            ic.setAlignmentX(Component.CENTER_ALIGNMENT);
            maxLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            minLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            descLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            forecastType.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Đưa toàn bộ ra giữa khung
            card.add(Box.createVerticalGlue());
            card.add(dayLbl);
            card.add(ic);
            card.add(maxLbl);
            card.add(minLbl);
            card.add(descLbl);
            card.add(Box.createVerticalStrut(4));
            card.add(forecastType);
            card.add(Box.createVerticalGlue());

            daysPanel.add(card);
        }
        daysPanel.revalidate();
        daysPanel.repaint();

        JFreeChart chart = ChartFactory.createLineChart("Min / Max 5 ngày", "Ngày", "°C", dataset);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(0,0,0,0));
        plot.getRangeAxis().setRange(0, 40);

        ChartPanel cp = new ChartPanel(chart);
        cp.setOpaque(false);
        widgetChart5d.removeAll();
        widgetChart5d.add(cp, BorderLayout.CENTER);
        widgetChart5d.revalidate();
        widgetChart5d.repaint();
    }

    // ============= Gradient nền =============
    static class GradientPanel extends JPanel {
        private boolean dark = false;
        private String theme = "default";

        public void setDarkMode(boolean dark) { this.dark = dark; }
        public void setWeatherTheme(String desc) {
            String d = desc == null ? "" : desc.toLowerCase();
            if (d.contains("nắng") || d.contains("clear")) theme = "sunny";
            else if (d.contains("mưa") || d.contains("rain")) theme = "rainy";
            else if (d.contains("mây") || d.contains("cloud")) theme = "cloudy";
            else theme = "default";
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            GradientPaint gp;
            if (dark) {
                gp = new GradientPaint(0, 0, new Color(30, 30, 60),
                        0, getHeight(), new Color(30, 30, 60)); // đồng nhất
            } else {
                switch (theme) {
                    case "sunny":
                        gp = new GradientPaint(0, 0, new Color(255, 200, 0),
                                0, getHeight(), new Color(255, 200, 0));
                        break;
                    case "rainy":
                        gp = new GradientPaint(0, 0, new Color(90, 110, 140),
                                0, getHeight(), new Color(90, 110, 140));
                        break;
                    case "cloudy":
                        gp = new GradientPaint(0, 0, new Color(135, 206, 235),
                                0, getHeight(), new Color(135, 206, 235));
                        break;
                    default:
                        gp = new GradientPaint(0, 0, new Color(70, 130, 180),
                                0, getHeight(), new Color(70, 130, 180));
                }
            }
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
