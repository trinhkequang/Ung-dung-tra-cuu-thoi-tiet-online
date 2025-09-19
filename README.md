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


🌤 Ứng Dụng Tra Cứu Thời Tiết Online

📖 Giới thiệu

Ứng dụng Tra Cứu Thời Tiết Online là chương trình Java Desktop giúp người dùng tra cứu thông tin thời tiết hiện tại của bất kỳ thành phố nào thông qua giao thức UDP.
Người dùng chỉ cần nhập tên thành phố, chương trình sẽ gửi yêu cầu đến server thời tiết qua cổng UDP, nhận phản hồi dạng JSON và hiển thị kết quả bằng tiếng Việt một cách trực quan, rõ ràng.

Ứng dụng được thiết kế với:

Giao diện hiện đại, màu sắc hài hòa, hỗ trợ biểu tượng cảm xúc (emoji).

Cơ chế truyền thông không kết nối (connectionless): chỉ gửi/nhận gói tin mà không cần thiết lập kết nối phức tạp.

Khả năng hoạt động đa nền tảng (Windows, Linux, macOS) miễn là có Java Runtime.

🛠️ Công nghệ sử dụng

Ngôn ngữ: Java 11 trở lên

Thư viện chuẩn Java:

java.net cho UDP socket (DatagramSocket, DatagramPacket)

javax.swing cho giao diện đồ họa

java.nio.charset.StandardCharsets cho mã hóa UTF-8

Kiến trúc client-server:

Client: ứng dụng Swing hiện tại

Server: dịch vụ UDP trả về dữ liệu thời tiết JSON (có thể do bạn tự triển khai)

JSON Handling: Xử lý chuỗi JSON thuần túy (replace key, format text)

✨ Chức năng nổi bật

Tra cứu thời tiết hiện tại

Người dùng nhập tên thành phố.

Ứng dụng gửi gói tin UDP chứa yêu cầu dạng REQ|<id>|<city>.

Nhận lại gói tin phản hồi từ server với dữ liệu JSON, ví dụ:

{ "temp":28.4, "dt_txt":"2025-09-19 12:00:00", "desc":"mây rải rác" }


Tự động dịch các khóa sang tiếng Việt:

temp → Nhiệt độ

dt_txt → Thời gian

desc → Tình trạng

Giao diện trực quan & thân thiện

Sử dụng Nimbus Look & Feel để tạo phong cách hiện đại.

Màu nền xanh nhạt dịu mắt, hỗ trợ emoji 🌤 ☀ 🌧.

Xử lý mạng ổn định

Tự động thử lại (retry) tối đa 3 lần nếu hết thời gian chờ.

Hiển thị thông báo rõ ràng khi có lỗi hoặc không nhận được phản hồi.

Đa nền tảng

Chạy được trên mọi hệ điều hành hỗ trợ Java: Windows, macOS, Linux.

📡 Giao thức truyền thông

Loại: UDP – User Datagram Protocol

Cổng mặc định: 5000

Định dạng gói tin:

Yêu cầu (Client → Server):

REQ|<id>|<city>


REQ: loại yêu cầu (thời tiết hiện tại)

<id>: số ngẫu nhiên để nhận dạng phiên giao tiếp

<city>: tên thành phố cần tra cứu

Phản hồi (Server → Client):

RES|<id>|<payload JSON>


RES: phản hồi

<id>: khớp với id đã gửi

<payload JSON>: chuỗi JSON chứa thông tin thời tiết

Cơ chế an toàn:

Kiểm tra id để đảm bảo gói tin trả về chính xác với yêu cầu đã gửi.

Sử dụng timeout 5 giây, thử lại tối đa 3 lần.

🧩 Hướng dẫn cài đặt và chạy

1️⃣ Chuẩn bị

Java JDK 11+: tải và cài đặt từ https://adoptium.net
 hoặc trang chính thức của Oracle.

IDE: khuyến nghị sử dụng IntelliJ IDEA, Eclipse hoặc VS Code (cài Java Extension Pack).

Đảm bảo có một Server UDP đang chạy (ví dụ server mẫu do bạn hoặc nhóm bạn cung cấp).

2️⃣ Tải mã nguồn
git clone https://github.com/trinhkequang/Ungdungtracuuthoitietonline.git
cd Ungdungtracuuthoitietonline

3️⃣ Mở dự án

Mở IDE, chọn Import Project → Existing Maven/Gradle/Plain Project (tùy cấu trúc).

Chỉ định JDK đã cài đặt.

4️⃣ Chạy ứng dụng Client

Trong IDE, mở file src/client/WeatherClientUDP.java.

Nhấn Run hoặc Shift + F10 (IntelliJ) để chạy chương trình.

Giao diện ứng dụng sẽ hiển thị.

5️⃣ Sử dụng

Bước 1: Nhập địa chỉ Server host (mặc định localhost nếu server chạy trên cùng máy).

Bước 2: Nhập Tên thành phố (ví dụ: Hanoi, London, Tokyo).

Bước 3: Bấm nút ☀ Hiện tại để gửi yêu cầu.

Bước 4: Xem kết quả hiển thị trong vùng Kết quả.

🧪 Ví dụ dữ liệu phản hồi

Dữ liệu JSON trả về từ server có dạng:

{
  "temp": 29.3,
  "dt_txt": "2025-09-19 12:00:00",
  "desc": "mây rải rác"
}


Sau khi ứng dụng xử lý sẽ hiển thị:

{
  "Nhiệt độ": 29.3,
  "Thời gian": "2025-09-19 12:00:00",
  "Tình trạng": "mây rải rác"
}

🖼️ Giao diện minh họa


<img width="602" height="466" alt="Ảnh1" src="https://github.com/user-attachments/assets/c64e3b6b-f53a-401a-8f9e-7dc729990d7a" />

📬 Liên hệ

Tác giả: Trịnh Kế Quang

Email: trinhkequang01032004@gmail.com
