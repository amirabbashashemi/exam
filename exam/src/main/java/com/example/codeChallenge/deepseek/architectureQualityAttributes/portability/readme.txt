بخش اول: چک‌لیست ۵ ثانیه‌ای Portability (قبل از تحویل کد)
وقتی کد را نوشتید، این ۵ سوال را از خودتان بپرسید. اگر جواب هرکدام "نه" بود، نمره‌تان کم می‌شود:

آیا برای ساخت مسیرها از Path.of() یا Paths.get() و resolve() استفاده کرده‌ام و از + و / یا \ هاردکد پرهیز کرده‌ام؟
(اگر در جایی "C:\\folder\\file.txt" یا "/var/log/file.txt" نوشته‌اید → غلط است).

آیا برای ایجاد پوشه از Files.createDirectories() استفاده کرده‌ام، نه mkdir یا Runtime.exec()؟
(اگر از Runtime.exec("mkdir -p") استفاده کرده‌اید → در ویندوز کار نمی‌کند).

آیا هنگام خواندن/نوشتن فایل، StandardCharsets.UTF_8 را به‌صورت صریح مشخص کرده‌ام؟
(اگر از new FileReader(file) بدون مشخص کردن encoding استفاده کرده‌اید → به encoding پیش‌فرض سیستم وابسته است).

آیا از System.lineSeparator() به‌جای \n برای خط جدید استفاده کرده‌ام؟
(اگر \n هاردکد کرده‌اید → در ویندوز (\r\n) مشکل ایجاد می‌کند).

آیا برای تاریخ و زمان از java.time با Clock تزریقی استفاده کرده‌ام و از new Date() یا Calendar پرهیز کرده‌ام؟
(اگر از new Date() استفاده کرده‌اید → به Locale و Timezone سیستم وابسته است).

🥈 بخش دوم: کلمات کلیدی جادویی برای پاسخ‌های تشریحی (تئوری محض)
اگر سوال تشریحی در مورد Portability آمد، حتماً این ۴ عبارت کلیدی را در پاسخ خود قالب کنید:

Abstraction over OS (انتزاع از سیستمعامل):
"سیستم باید از APIهای انتزاعی جاوا (مثل java.nio.file) استفاده کند تا از جزئیات سیستمعامل‌های مختلف (ویندوز، لینوکس، مک) جدا شود."

Encoding Independence (استقلال از کدگذاری):
"همیشه هنگام کار با فایل‌ها، کدگذاری را به‌صورت صریح با StandardCharsets.UTF_8 مشخص کنید تا به کدگذاری پیش‌فرض سیستم وابسته نباشید."

Path Abstraction (انتزاع مسیر):
"به‌جای هاردکد کردن جداکننده‌های مسیر (/ یا \)، از Path.resolve() یا File.separator استفاده کنید تا کد در همه‌ی سیستمعامل‌ها کار کند."

Configuration Externalization (برون‌سپاری پیکربندی):
"مسیرها و تنظیمات وابسته به محیط را از کد جدا کنید و از طریق متغیرهای محیطی، Properties یا تزریق وابستگی به کلاس ارائه دهید."

🥉 بخش سوم: تله‌های مرگبار (نمره‌ی زیر ۵۰)
این اشتباهات را هرگز در آزمون Portability مرتکب نشوید:

استفاده از File به‌جای Path و Files API:

java
File file = new File("/var/log/app.log"); // ❌ وابسته به سیستم‌عامل
همیشه از Path و Files استفاده کنید.

استفاده از Runtime.exec برای دستورات سیستمی:

java
Runtime.getRuntime().exec("mkdir -p /var/log"); // ❌ در ویندوز کار نمی‌کند
به‌جای آن از Files.createDirectories() استفاده کنید.

هاردکد کردن جداکننده‌ی مسیر:

java
String path = "/var/" + "log/" + "file.txt"; // ❌ در ویندوز شکست می‌خورد
استفاده از new FileReader(file) یا new FileWriter(file) بدون مشخص کردن encoding:

java
BufferedReader reader = new BufferedReader(new FileReader("file.txt")); // ❌
از Files.newBufferedReader(path, StandardCharsets.UTF_8) استفاده کنید.

استفاده از new Date() یا Calendar:

java
String date = new Date().toString(); // ❌ وابسته به Locale و Timezone
از java.time و Clock تزریقی استفاده کنید.

استفاده از کلاس‌های خاص JVM (مثل sun.* یا com.sun.*):
اگر از کلاس‌های غیراستاندارد استفاده کنید، کد شما روی JVMهای دیگر (مثل OpenJ9 یا GraalVM) اجرا نمی‌شود.

📜 خلاصه‌ی یک پاراگرافی برای شب امتحان (قابل حفظ کردن)
Portability یعنی "اجرا در هر محیطی بدون تغییر کد". برای رسیدن به آن، از java.nio.file به‌جای java.io.File استفاده کن و مسیرها را با Path.resolve() بساز، نه با + و جداکننده‌های هاردکد. برای ایجاد پوشه، از Files.createDirectories() استفاده کن، نه Runtime.exec. همیشه هنگام خواندن/نوشتن فایل، StandardCharsets.UTF_8 را مشخص کن و از System.lineSeparator() به‌جای \n استفاده کن. برای تاریخ و زمان، از java.time و Clock تزریقی استفاده کن و از new Date() پرهیز کن. مسیرها و تنظیمات وابسته به محیط را هرگز در کد هاردکد نکن؛ آنها را از بیرون (از طریق Constructor، Properties یا متغیرهای محیطی) تزریق کن.

