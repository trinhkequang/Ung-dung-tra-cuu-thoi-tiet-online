<h2 align="center">
    <a href="https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin">
    🎓 Faculty of Information Technology (DaiNam University)
    </a>
</h2>
<h2 align="center">
   ỨNG DỤNG TRA CỨU THỜI TIẾT ONLINE
</h2>
<div align="center">
    <p align="center">
        <img alt="AIoTLab Logo" width="170" src="https://github.com/user-attachments/assets/711a2cd8-7eb4-4dae-9d90-12c0a0a208a2" />
        <img alt="AIoTLab Logo" width="180" src="https://github.com/user-attachments/assets/dc2ef2b8-9a70-4cfa-9b4b-f6c2f25f1660" />
        <img alt="DaiNam University Logo" width="200" src="https://github.com/user-attachments/assets/77fe0fd1-2e55-4032-be3c-b1a705a1b574" />
    </p>

[![AIoTLab](https://img.shields.io/badge/AIoTLab-green?style=for-the-badge)](https://www.facebook.com/DNUAIoTLab)
[![Faculty of Information Technology](https://img.shields.io/badge/Faculty%20of%20Information%20Technology-blue?style=for-the-badge)](https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin)
[![DaiNam University](https://img.shields.io/badge/DaiNam%20University-orange?style=for-the-badge)](https://dainam.edu.vn)

</div>



# 🌦️ Ứng dụng Dự báo Thời tiết Online (UDP Client–Server)

## 📖 1. Giới thiệu hệ thống  

### 🎯 Mục tiêu dự án
- Xây dựng ứng dụng **dự báo thời tiết online** theo mô hình **Client–Server** sử dụng **UDP Socket**.  
- Giúp sinh viên nắm vững kiến thức về **lập trình mạng** (Sockets, DatagramPacket, DatagramSocket).  
- Cung cấp giao diện đồ họa trực quan bằng **Java Swing GUI**.  
- Hiển thị dữ liệu thời tiết sinh động, kèm theo **biểu đồ (JFreeChart)** và **cảnh báo thông minh**.  

### ⚙️ Cách hoạt động  
- **Server**: nhận yêu cầu từ Client, gọi API **OpenWeatherMap**, xử lý dữ liệu và trả về JSON.  
- **Client**: gửi yêu cầu thời tiết/dự báo, nhận dữ liệu JSON, hiển thị trực quan qua giao diện và biểu đồ.  

---

## 🏗️ 2. Kiến trúc hệ thống  

Ứng dụng được thiết kế theo mô hình **Client–Server** với giao thức **UDP**.  

- **Client**:  
  - Giao diện người dùng (Java Swing).  
  - Cho phép nhập thành phố, gửi yêu cầu dự báo thời tiết.  
  - Hiển thị dữ liệu trả về dưới dạng văn bản, biểu đồ, cảnh báo.  

- **Server**:  
  - Lắng nghe yêu cầu từ Client qua UDP.  
  - Gọi API OpenWeatherMap (HTTP/JSON).  
  - Xử lý dữ liệu, đóng gói JSON, gửi trả kết quả về Client.  

### 🔗 Sơ đồ hệ thống  

```text
+------------------+           UDP            +------------------+          HTTP/API           +---------------------+
|     CLIENT       |  <-------------------->  |     SERVER       |  <----------------------->  | OpenWeatherMap API  |
| (Java Swing GUI) |                          | (UDP + JSON)     |                            |   (Weather Data)    |
+------------------+                          +------------------+                            +---------------------+
| - Nhập thành phố                            | - Xử lý yêu cầu                               | - Trả về dữ liệu    |
| - Hiển thị biểu đồ & cảnh báo               | - Gọi API OWM                                 |   thời tiết & dự báo |
+---------------------------------------------------------------------------------------------------------------+
```


👉 Với kiến trúc này, hệ thống vừa **nhẹ, nhanh, dễ triển khai**, vừa đảm bảo tính **thực tế và trực quan**.  

---

## 🔧 3. Ngôn ngữ lập trình & Công nghệ sử dụng  

- **Ngôn ngữ:** Java 8+  
- **Giao diện:** Java Swing (JFrame, JPanel, JComboBox, JTabbedPane, …)  
- **Giao thức mạng:** UDP (DatagramSocket, DatagramPacket)  
- **Biểu đồ:** JFreeChart (LineChart hiển thị dự báo nhiệt độ)  
- **API:** OpenWeatherMap (RESTful API trả về JSON)  
- **IDE phát triển:** Eclipse IDE / IntelliJ IDEA  

---

## 🚀 4. Hướng dẫn cài đặt & chạy dự án trên Eclipse  

### 1️⃣ Chuẩn bị  
- Cài đặt **Java JDK 8+**  
- Cài đặt **Eclipse IDE** hoặc **IntelliJ IDEA**  
- Tải source code:  

git clone https://github.com/ApheliosQ/WeatherUDP.git  

---

### 2️⃣ Chạy Server  
- Mở Eclipse → Import > Existing Projects into Workspace  
- Mở file **WeatherServerUDP.java** trong package `server`  
- Kiểm tra `SERVER_PORT` (mặc định: `5000`)  
- Chạy chương trình (**Run As > Java Application**)  

➡ Server bắt đầu lắng nghe các Client.  

---

### 3️⃣ Chạy Client  
- Mở file **WeatherClientUDP.java** trong package `client`  
- Kiểm tra `SERVER_ADDRESS` và `SERVER_PORT` trùng với Server  
- Chạy chương trình (**Run As > Java Application**)  
- Nhập tên thành phố (VD: Hà Nội, Đà Nẵng, Hồ Chí Minh)  

➡ Client hiển thị dữ liệu **thời tiết hiện tại** và **dự báo 5 ngày** dưới dạng **biểu đồ + cảnh báo thông minh**.  

---

## ✨ 5. Các tính năng nổi bật  

- 🌍 **Tìm kiếm thành phố**: nhập tên bất kỳ để tra cứu thời tiết.  
- 🌡️ **Thời tiết hiện tại**: nhiệt độ, độ ẩm, gió, áp suất, feels-like.  
- 📊 **Dự báo ngắn hạn**: hiển thị biểu đồ nhiệt độ – feels-like trong 6 mốc giờ.  
- 📅 **Dự báo 5 ngày**: hiển thị nhiệt độ Min/Max, tình trạng mưa/nắng/mây.  
- ⚠️ **Cảnh báo thông minh**: gợi ý khi trời nắng gắt, mưa lớn hoặc rét.  
- 🌙 **Chế độ Dark/Light Mode**: thay đổi giao diện ngay trên Client.  
- 🗺️ **Tích hợp Google Maps**: mở bản đồ tại thành phố đang tra cứu.  

---

## 🖼️ 6. Giao diện minh họa  

### 🖼️ Giao diện dự án

Ứng dụng **Dự báo thời tiết online (UDP Client–Server)** được thiết kế trực quan với **3 tab chính**:

- **Thời tiết hiện tại**: hiển thị nhiệt độ, độ ẩm, áp suất, tốc độ gió, tình trạng mây, cùng cảnh báo thông minh.  
- **Dự báo 5 ngày**: biểu đồ trực quan (Min/Max) và danh sách dự báo chi tiết theo từng ngày.  
- **Bản đồ**: mở Google Maps tại thành phố mà người dùng nhập, giúp định vị dễ dàng.  

Ngoài ra, ứng dụng có tích hợp **biểu đồ JFreeChart** để trực quan hóa xu hướng nhiệt độ theo nhiều mốc giờ.  

<div align="center">

  <img width="1916" height="1017" alt="màn hình chính" src="https://github.com/user-attachments/assets/b6b60990-e977-48b6-be0a-5fd78e48d0b1" />
 
  <p><b>Hình 1:</b> Tab "Thời tiết hiện tại" – hiển thị thông tin chi tiết và cảnh báo.</p>

 <img width="1918" height="1015" alt="dự báo 5 ngày" src="https://github.com/user-attachments/assets/1f479f32-1880-46b1-b340-7d34d24f34bd" />

  <p><b>Hình 2:</b> Tab "Dự báo 5 ngày" – biểu đồ và dự báo chi tiết cho từng ngày.</p>

<img width="1918" height="1018" alt="bản đồ" src="https://github.com/user-attachments/assets/6ca89db5-e14f-4742-b674-c5fc75a302be" />

  <p><b>Hình 3:</b> Tab "Bản đồ" – hiển thị vị trí thành phố trên Google Maps.</p>

<img width="1918" height="1012" alt="bản đồ 2" src="https://github.com/user-attachments/assets/f03e8d41-fa5c-4164-b5aa-ab4f07755769" />

  <p><b>Hình 4:</b> Khi ấn tìm bản đồ và ra nơi mà bạn muốn tìm</p>

</div>


---

## 🔮 7. Hướng phát triển trong tương lai  

- ⛈️ Tích hợp dự báo **7 ngày / 14 ngày**.  
- 📱 Phát triển ứng dụng di động (Android).  
- 🌍 Hỗ trợ đa ngôn ngữ (Tiếng Việt, Tiếng Anh, …).  
- 🔔 Thêm tính năng **gửi thông báo cảnh báo thời tiết**.  
- 💾 Lưu lịch sử tìm kiếm và tùy chỉnh giao diện.  

---

## 📬 8. Liên hệ  

👨‍💻 **Người thực hiện:** *Trịnh Kế Quang*  
✉️ **Email:** *trinhquang01032004@gmail.com*  
📞 **Số điện thoại:** *0966678165*  

© 2025 Faculty of Information Technology, Đại Nam University. All rights reserved.
```
