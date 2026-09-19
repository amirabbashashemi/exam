شماره	مفهوم	✅ درست	❌ غلط
۱	Producer-Consumer	queue.offer() + while	پردازش مستقیم در Producer
۲	Virtual Threads	I/O-intensive	CPU-intensive
۳	Batch Processing	بافر با BATCH_SIZE	هر خط جداگانه
۴	Memory Management	BufferedReader.lines()	Files.readAllLines()
۵	CompletableFuture	supplyAsync() + allOf().join()	runAsync() + بدون join()
۶	Thread Safety	ConcurrentHashMap	HashMap
۷	File I/O	CREATE + APPEND + \n	فقط APPEND
۸	Shutdown	shutdown() + awaitTermination()	بدون shutdown()
۹	Cleanup	ScheduledExecutorService	while(true) + sleep()
۱۰	Flags	volatile	بدون volatile

-----------------------------------
نکات طلایی که در تمرین‌ها یاد گرفتی:
-----------------------------------
۱. معماری Producer-Consumer
✅ Producer فقط offer() به Queue کند
✅ Consumer با while (running) و poll(timeout) کار کند
❌ Producer مستقیم پردازش نکند


۲. Virtual Threads
✅ برای I/O-intensive استفاده کن
✅ Executors.newVirtualThreadPerTaskExecutor()
❌ برای CPU-intensive استفاده نکن


۳. Batch Processing
✅ بافر با BATCH_SIZE (مثلاً ۱۰۰۰)
✅ drainTo() برای جمع‌آوری چند آیتم
✅ StringBuilder برای ساخت String یکجا
❌ هر خط را جداگانه ننویس


۴. Graceful Shutdown
✅ shutdown() + awaitTermination()
✅ Shutdown Hook
✅ volatile boolean running برای کنترل حلقه
❌ shutdown() در processFile() (فقط در processAllFiles())


۵. مدیریت حافظه
✅ BufferedReader.lines() خط به خط
✅ بافر با اندازه مناسب
❌ Files.readAllLines() برای فایل‌های بزرگ
❌ نگهداری کل خروجی در List


۶. CompletableFuture
✅ supplyAsync() برای کارهای نتیجه‌دار
✅ allOf().join() قبل از گرفتن نتایج
❌ runAsync() برای کارهای نتیجه‌دار
❌ get() بدون join()


۷. Thread Safety
✅ ConcurrentHashMap به جای HashMap
✅ volatile برای flags
❌ synchronized روی کل متد
❌ HashMap در محیط چندتردی


۸. File I/O
✅ StandardOpenOption.CREATE + APPEND
✅ TRUNCATE_EXISTING برای فایل جدید
✅ جداکننده خط (\n یا newLine())
❌ فقط APPEND بدون CREATE
❌ Files.write(path, list) بدون جداکننده


۹. ScheduledExecutorService
✅ scheduleAtFixedRate() برای کارهای دوره‌ای
❌ Thread.sleep() در while(true)


۱۰. Trade-off
✅ همیشه ذکر کن: حافظه vs I/O، سرعت vs امنیت، سادگی vs پیچیدگی
✅ نشان بده که به عمق مسئله فکر کرده‌ای

