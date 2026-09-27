نکات طلایی Robustness از تمرین‌ها
شماره	مفهوم	✅ درست	❌ غلط
۱	Retry Mechanism	while با MAX_ATTEMPT و Thread.sleep(delay)	بازگشتی بدون شرط خروج
۲	Exponential Backoff	delay *= 2 (۱s, ۲s, ۴s, ...)	تاخیر ثابت یا خیلی کوچک (مثلاً ۵ms)
۳	Dead Letter Queue (DLQ)	BlockingQueue<Message> dlq برای خطاهای دائمی	فقط لاگ کردن و نادیده گرفتن
۴	Error Classification	خطاهای موقت (Retry) vs دائمی (DLQ)	همه خطاها را یکسان رفتار کردن
۵	Retry Success/Failure	بعد از موفقیت return، بعد از همه Retryها → DLQ	Retry کردن حتی بعد از موفقیت
۶	Exception Handling	catch (Exception e) + لاگ + ادامه یا DLQ	catch کردن و نادیده گرفتن (مثل catch (RuntimeException e) {})
۷	InterruptedException	Thread.currentThread().interrupt() + خروج	throw new RuntimeException(e) یا نادیده گرفتن
۸	Timeout for I/O	future.get(5, TimeUnit.SECONDS) یا poll(timeout)	بدون Timeout (بلوکه شدن بی‌نهایت)
۹	Async with join()	CompletableFuture.runAsync(...).join()	submit() بدون join() (نیمه‌کاره ماندن)
۱۰	Validation Errors	IllegalArgumentException → DLQ فوری	Validation error → Retry (بی‌فایده)
۱۱	Graceful Shutdown	shutdown() + awaitTermination() + shutdownNow()	فقط shutdown() بدون انتظار
۱۲	Shutdown Hook	Runtime.addShutdownHook() با مدیریت خطا	بدون Shutdown Hook
۱۳	Logging	لاگ کردن هر خطا با جزئیات (ID, دلیل, زمان)	لاگ نکردن یا لاگ خیلی کلی
۱۴	Fallback	پاسخ جایگزین یا وضعیت PENDING_MANUAL	خطا را به بالا پرتاب کردن و ترد را کشتن
۱۵	Thread-Safety	ConcurrentHashMap, BlockingQueue, synchronized	HashMap, ArrayList در محیط چندتردی
۱۶	Busy-Waiting	queue.poll(timeout, unit)	queue.poll() در حلقه بی‌نهایت (مصرف CPU)
۱۷	Retry with Backoff	Thread.sleep(delay); delay *= 2;	Thread.sleep(100); ثابت
۱۸	Idempotency	بررسی processedIdSet قبل از پردازش	پردازش مجدد بدون چک (احتمال دوبار پردازش)
۱۹	DLQ Full Handling	dlq.offer(msg, timeout, unit) با مدیریت خطا	dlq.add(msg) بدون کنترل ظرفیت (استثنا)
📌 نکات تکمیلی:
شماره	نکته	توضیح
۱	Retry با تعداد محدود	معمولاً ۳ بار کافی است (بیشتر از آن باعث بار اضافی می‌شود)
۲	تاخیر حداقل ۱۰۰ms	تاخیرهای خیلی کم (مثلاً ۵ms) هیچ تأثیری ندارند
۳	DLQ برای خطاهای دائمی	Validation error, IllegalArgumentException, NullPointerException
۴	InterruptedException را همیشه interrupt() کن	برای حفظ وضعیت قطع شدن ترد
۵	Timeout برای همه I/O	دیتابیس, API, Queue, File, Network
۶	CompletableFuture با get(timeout)	برای جلوگیری از بلوکه شدن بی‌نهایت
۷	Laگ دقیق	شامل timestamp, thread, transactionId, error message
۸	Fallback هوشمند	پاسخ پیش‌فرض, وضعیت PENDING_MANUAL, یا ذخیره در DLQ
🔥 مهم‌ترین نکات:
Retry = فقط برای خطاهای موقت (شبکه, Timeout, 5xx)
DLQ = برای خطاهای دائمی (Validation, 4xx, داده‌های نامعتبر)
Exponential Backoff = تاخیر افزایشی (۱s, ۲s, ۴s, ...)
InterruptedException = همیشه interrupt() کن
همیشه poll(timeout) به جای poll()
همیشه get(timeout) به جای get()

