نکات طلایی Resilience (تاب‌آوری)
شماره	مفهوم	✅ درست	❌ غلط
۱	Retry (تکرار)	حلقه while با MAX_ATTEMPT محدود + delay *= 2 (Exponential Backoff)	بازگشتی بی‌نهایت، تاخیر ثابت، بدون محدودیت
۲	Circuit Breaker	سه حالت CLOSED → OPEN → HALF_OPEN با شمارش خطاها و Timeout برای بازگشت	فقط دو حالت (بدون HALF_OPEN) یا شمارش دستی ناقص
۳	Bulkhead	Semaphore با tryAcquire() و release() در finally (با پرچم acquired)	Semaphore بدون مدیریت release() (منفی شدن) یا بدون محدودیت
۴	Fallback	پاسخ جایگزین (مثلاً PENDING_MANUAL یا CACHED_RESPONSE) + ذخیره در Map	return null; یا پرتاب Exception به بالا
۵	Timeout	poll(timeout, unit) برای صف، get(timeout, unit) برای Future، timeout() در HttpClient	بدون Timeout (بلوکه شدن بی‌نهایت)
۶	Dead Letter Queue (DLQ)	BlockingQueue یا ConcurrentHashMap برای خطاهای دائمی (Validation, 4xx)	فقط لاگ کردن و نادیده گرفتن (از دست رفتن داده)
۷	Error Classification	خطاهای موقت (۵xx, Timeout, Network) → Retry؛ خطاهای دائمی (۴xx, Validation) → DLQ فوری	همه خطاها یکسان رفتار شدن (مثلاً Retry کردن Validation Error)
۸	InterruptedException	Thread.currentThread().interrupt() + خروج از حلقه (break یا return)	throw new RuntimeException(e) یا نادیده گرفتن
۹	Idempotency	چک کردن successfulMap یا processedIdSet قبل از پردازش برای جلوگیری از تکرار	پردازش مجدد بدون چک (Duplicate Processing)
۱۰	Thread-Safety	ConcurrentHashMap, ConcurrentLinkedQueue, Semaphore, synchronized	HashMap, ArrayList, LinkedList در محیط چندتردی
۱۱	Graceful Shutdown	shutdown() + awaitTermination(timeout) + shutdownNow() در صورت Timeout	فقط shutdown() بدون انتظار
۱۲	Resilience4j (اختیاری)	استفاده در صورت مجاز بودن (کد تمیزتر و سریع‌تر)	پیاده‌سازی دستی بدون درک مفاهیم (وقت‌گیر و پرخطا)

📌 نکات تکمیلی:
شماره	نکته	توضیح
۱	Retry ≠ Forever	همیشه MAX_ATTEMPT داشته باش (معمولاً ۳ یا ۵ بار)
۲	Backoff = حیاتی	delay *= 2 (۱s, ۲s, ۴s, ...) تا فشار روی سیستم خارجی کاهش یابد
۳	Half-Open = ضروری	در Circuit Breaker، برای اینکه سیستم بتواند خودش را بازیابی کند، HALF_OPEN حتماً باید وجود داشته باشد
۴	Bulkhead = محافظت از منابع	تعداد درخواست‌های همزمان را محدود کن تا اگر یک سرویس خارجی کند شد، کل سیستم از کار نیفتد
۵	Validation Error = DLQ فوری	خطاهای اعتبارسنجی هرگز Retry نشوند (چون با تکرار هم درست نمی‌شوند)
۶	حذف از Retry Map	بعد از موفقیت یا انتقال به DLQ، حتماً از Map حذف کن تا دوبار پردازش نشود
۷	acquired flag در Semaphore	برای جلوگیری از release() بی‌جا، حتماً یک boolean acquired نگه دار و فقط در صورت موفقیت release() کن

🧪 چک‌لیست نهایی (قبل از تحویل)
□ Retry با while و MAX_ATTEMPT و delay *= 2
□ Circuit Breaker با ۳ حالت (CLOSED, OPEN, HALF_OPEN)
□ Bulkhead با Semaphore و مدیریت acquired
□ Fallback با پاسخ جایگزین (نه null)
□ Timeout برای تمام عملیات‌های I/O
□ DLQ برای خطاهای دائمی (Validation, 4xx)
□ Error Classification (موقت vs دائمی)
□ InterruptedException با interrupt() و خروج از حلقه
□ Idempotency (چک successfulMap قبل از پردازش)
□ Graceful Shutdown کامل
□ Thread-Safe (ConcurrentHashMap, Semaphore, synchronized)