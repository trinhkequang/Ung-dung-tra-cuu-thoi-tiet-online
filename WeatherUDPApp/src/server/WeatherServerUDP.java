package server;

import utils.WeatherFetcher;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * UDP Weather Server
 * - "current|<city>"  -> JSON hiện tại + 6 mốc forecast 3h
 * - "forecast5|<city>" -> JSON tổng hợp 5 ngày (min/max/desc)
 */
public class WeatherServerUDP {

    public static final int PORT = 5000;
    public static final int BUFFER_SIZE = 8192;

    public static void main(String[] args) {
        System.out.println("=== UDP Weather Server ===");
        System.out.println("Đang lắng nghe trên cổng: " + PORT);

        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            byte[] buffer = new byte[BUFFER_SIZE];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String request = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                System.out.println("Nhận từ client: " + request);

                String[] parts = request.split("\\|", 2);
                String command = parts[0];
                String city = parts.length > 1 ? parts[1] : "Hà Nội";

                String reply;
                try {
                    if (command.equalsIgnoreCase("current")) {
                        reply = WeatherFetcher.getWeatherSummaryJson(city);
                    } else if (command.equalsIgnoreCase("forecast5")) {
                        reply = WeatherFetcher.getForecast5DaysJson(city);
                    } else {
                        reply = "{\"error\":\"Lệnh không hợp lệ\"}";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    reply = "{\"error\":\"" + e.getMessage() + "\"}";
                }

                byte[] sendData = reply.getBytes(StandardCharsets.UTF_8);
                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();
                DatagramPacket response =
                        new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                socket.send(response);

                System.out.println("Đã gửi JSON cho client: " + city + " (" + command + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
