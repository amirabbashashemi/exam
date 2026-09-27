امتیازات کلیدی برای نمره ۱۰
شماره	ویژگی	وضعیت
۱	Write-Ahead Log (WAL) با APPEND	✅
۲	Recovery با Deduplication (Set<String>)	✅
۳	حذف فایل‌های Backup بعد از Recovery	✅
۴	Idempotency با successMap (هرگز پاک نمی‌شود)	✅
۵	Retry خودکار با failedMap و حذف بعد از ارسال به صف	✅
۶	Checkpointing با Log Rotation	✅
۷	مدیریت InterruptedException بدون interrupt() (فقط لاگ)	✅
۸	ConcurrentHashMap و BlockingQueue	✅
۹	Shutdown کامل همه Executorها	✅
۱۰	استفاده از SLF4J برای لاگ	✅


🎯 Trade-offهای در نظر گرفته شده
تصمیم	مزیت	عیب
WAL با Append	حفظ تاریخچه کامل	حجم فایل زیاد (با Checkpoint حل شده)
Deduplication با Set	جلوگیری از پردازش تکراری	مصرف حافظه اضافی
Retry هر ۱۰ ثانیه	کاهش فشار روی سیستم	تاخیر در پردازش مجدد
Checkpoint هر ۱۰ ثانیه	کاهش حجم Log	I/O اضافی


نکات طلایی رو هم بگو برای reliability
📝 نکات طلایی Reliability از تمرین‌ها
شماره	مفهوم	✅ درست	❌ غلط
۱	Write-Ahead Log (WAL)	قبل از هر تغییری، در Log با APPEND بنویس	بعد از پردازش Log بنویس یا TRUNCATE کن
۲	Recovery	Log را بخوان و وضعیت را بازیابی کن + Deduplication	فقط Log را بخوان بدون جلوگیری از تکراری
۳	فایل‌های Backup	بعد از Recovery، فایل‌های Log را پاک کن	فایل‌ها را نگه دار (باعث Replay مجدد می‌شود)
۴	Idempotency	successfulMap (دائمی) برای جلوگیری از پردازش تکراری	فقط در حافظه نگه دار (در Crash از بین می‌رود)
۵	Retry خودکار	failedMap را به صف برگردان و بعد از Retry پاک کن	Retry کن ولی از Map پاک نکن (تکثیر بی‌نهایت)
۶	Checkpointing	هر N تراکنش، Snapshot بگیر و Log قدیمی را پاک کن	هیچ‌وقت Log را پاک نکن (بزرگ شدن بی‌نهایت)
۷	Deduplication در Recovery	Set<String> برای شناسه‌های بازیابی‌شده	بدون Dedup (تراکنش‌ها چند بار به صف اضافه می‌شوند)
۸	Persistent Queue	صف را با Log پشتیبانی کن	فقط صف در حافظه (در Crash از دست می‌رود)
۹	Graceful Shutdown	shutdown() + awaitTermination() + فلش نهایی	فقط shutdown() بدون انتظار
۱۰	Shutdown Hook	Runtime.addShutdownHook() با مدیریت خطا	بدون Shutdown Hook
۱۱	Thread-Safety	ConcurrentHashMap, BlockingQueue, synchronized	HashMap, ArrayList در محیط چندتردی
۱۲	Atomic Operations	putIfAbsent, remove برای عملیات همزمان	put و get جداگانه (Race Condition)
۱۳	InterruptedException	Thread.currentThread().interrupt() + لاگ + ادامه	throw new RuntimeException(e) یا نادیده گرفتن
۱۴	Log Rotation	بعد از Checkpoint، فایل‌های قدیمی را حذف کن	فایل‌ها را نگه دار (حجم زیاد)
۱۵	Error Recovery	در صورت خطا، تراکنش را به failedMap منتقل کن	تراکنش را رها کن (از دست رفتن داده)
📌 نکات تکمیلی:
شماره	نکته	توضیح
۱	WAL با APPEND	همیشه به انتهای فایل اضافه کن، نه بازنویسی
۲	Recovery با Dedup	از Set<String> برای جلوگیری از افزودن تکراری استفاده کن
۳	successfulMap هرگز پاک نشود	حتی بعد از Backup، در حافظه نگه دار
۴	failedMap بعد از Retry پاک شود	از تکثیر بی‌نهایت جلوگیری کن
۵	Checkpointing	هر ۱۰ ثانیه یا هر ۱۰۰ تراکنش، یک Snapshot بگیر
۶	فلش نهایی در Shutdown	قبل از خاموش شدن، آخرین Backup را انجام بده
۷	مدیریت InterruptedException	فقط لاگ کن و interrupt() را حفظ کن، ادامه بده
۸	جلوگیری از پردازش تکراری	قبل از queue.offer در Recovery، successfulMap را چک کن
🔥 مهم‌ترین نکات:
WAL = APPEND + قبل از تغییر (نه بعد از تغییر)
Recovery = Set<String> + حذف فایل بعد از خواندن
Idempotency = successfulMap دائمی (هرگز پاک نشود)
Retry = failedMap را به صف برگردان و پاک کن (نه تکثیر)
Checkpointing = هر چند وقت یکبار Snapshot بگیر (برای کاهش حجم Log)
Shutdown = فلش نهایی + awaitTermination
در Recovery، successfulMap را اول بازیابی کن (برای Dedup)

🧪 چک‌لیست نهایی (قبل از تحویل)
□ WAL با APPEND نوشته می‌شود (نه TRUNCATE)
□ Recovery با Set<String> برای Deduplication
□ بعد از Recovery، فایل‌های Log حذف می‌شوند
□ successfulMap هرگز پاک نمی‌شود
□ failedMap بعد از Retry پاک می‌شود
□ Checkpointing برای کاهش حجم Log وجود دارد
□ Shutdown شامل فلش نهایی و awaitTermination است
□ InterruptedException با interrupt() + لاگ مدیریت می‌شود
□ قبل از queue.offer در Recovery، successfulMap چک می‌شود
□ ConcurrentHashMap و BlockingQueue استفاده شده است