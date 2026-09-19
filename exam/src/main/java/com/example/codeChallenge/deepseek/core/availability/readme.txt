نکات طلایی برای availability رو بگو به فرمت قبل
📝 نکات طلایی Availability از تمرین‌ها
شماره	مفهوم	✅ درست	❌ غلط
۱	Retry Mechanism	ScheduledExecutorService.schedule() با تاخیر افزایشی	Thread.sleep() در حلقه
۲	Fallback	Dead Letter Queue + پاسخ پیش‌فرض	نادیده گرفتن خطا و ادامه
۳	Circuit Breaker	سه حالت CLOSED, OPEN, HALF_OPEN	فقط دو حالت (بدون HALF_OPEN)
۴	Circuit Breaker - Permission	tryAcquirePermission() + releasePermission() در finally	فقط tryAcquirePermission() بدون releasePermission()
۵	Bulkhead	FixedThreadPool با تعداد ترد متفاوت برای هر نوع	VirtualThreads بدون محدودیت
۶	Backup/Persistence	کپی از Queue با new ArrayList<>(queue)	queue.drainTo() که داده را حذف می‌کند
۷	Backup Path	Path.of("backup.txt") با مسیر مشخص	Path.of("") مسیر خالی
۸	Retry with Exponential Backoff	delay = attempt * 1000 (افزایشی)	Thread.sleep(100) ثابت
۹	Graceful Shutdown	shutdown() + awaitTermination(30s) + shutdownNow()	فقط shutdown()
۱۰	Shutdown Hook	Runtime.addShutdownHook() با try-catch	بدون Shutdown Hook
۱۱	Resilience4j	کتابخانه سبک و مستقل (مجاز)	Spring, Hibernate, Kafka (ممنوع)
۱۲	Dead Letter Queue	BlockingQueue<Order> deadLetter برای ذخیره خطاها	حذف تراکنش‌های خطادار
۱۳	InterruptedException	Thread.currentThread().interrupt() + لاگ	throw new RuntimeException(e)
۱۴	Record Exceptions	recordExceptions(TimeoutException.class, RuntimeException.class)	recordExceptions(Exception.class)
۱۵	Bulkhead Types	Heavy: ۵, Light: ۲۰, Normal: ۱۰ ترد	همه با یک تعداد ترد
۱۶	Shutdown Timeout	awaitTermination(30, TimeUnit.SECONDS)	awaitTermination(2, TimeUnit.SECONDS)


📌 نکات تکمیلی:
شماره	نکته	توضیح
۱	Retry با تاخیر افزایشی	delay = attempt * 1000 میلی‌ثانیه (۱, ۲, ۳, ... ثانیه)
۲	Circuit Breaker - OPEN timeout	بعد از ۳۰ ثانیه → HALF_OPEN
۳	Circuit Breaker - HALF_OPEN	فقط ۲-۳ درخواست تستی ارسال کن
۴	Fallback کامل	لاگ + Dead Letter Queue + پاسخ پیش‌فرض
۵	Backup دوره‌ای	scheduleWithFixedDelay() هر ۱ ثانیه
۶	Bulkhead با FixedThreadPool	Heavy: ۵, Light: ۲۰, Normal: ۱۰
۷	ServiceLoader	برای بارگذاری پویا و توسعه‌پذیری
۸	Strategy Pattern	جدا کردن منطق هر نوع درخواست


🔥 مهم‌ترین نکات:
Retry = ScheduledExecutorService (نه Thread.sleep())
Fallback = Dead Letter Queue + پاسخ پیش‌فرض
Circuit Breaker = ۳ حالت + tryAcquirePermission() + releasePermission()
Bulkhead = FixedThreadPool با تعداد متفاوت
Backup = کپی از Queue (نه drainTo())
Shutdown = shutdown() + awaitTermination(30s) + shutdownNow()
InterruptedException = interrupt() + لاگ (نه throw RuntimeException)

