
شماره	مفهوم	✅ درست	❌ غلط
۱	خواندن فایل	Files.lines() یا BufferedReader.lines()	Files.readAllLines()
۲	حافظه	پردازش خط به خط (Streaming)	نگهداری کل فایل در List
۳	Virtual Threads	Executors.newVirtualThreadPerTaskExecutor()	Executors.newFixedThreadPool()
۴	CompletableFuture	supplyAsync() برای کارهای نتیجه‌دار	runAsync() برای کارهای نتیجه‌دار
۵	منتظر ماندن	allOf().join() قبل از get()	get() بدون join()
۶	زنجیره‌سازی	thenApplyAsync(task, executor)	thenApplyAsync(task) بدون Executor
۷	جمع‌آوری آمار	یک بار تکرار با for یا teeing()	چندین بار stream() روی داده
۸	Thread-Safety	CopyOnWriteArrayList یا synchronizedList	ArrayList در محیط چندتردی
۹	Graceful Shutdown	shutdown() + awaitTermination()	فقط shutdown() بدون انتظار
۱۰	Shutdown Hook	addShutdownHook() برای خاموش شدن خودکار	بدون Shutdown Hook
۱۱	تقسیم بر صفر	totalRequests > 0 ? sum/total : 0	sum / total مستقیم
۱۲	مجموعه کاربران	Set<String> برای userId	Set<LogEntry> یا long شمارنده
۱۳	مدیریت خطا	catch (ExecutionException e) با getCause()	catch (Exception e) کلی
۱۴	InterruptedException	Thread.currentThread().interrupt() + خروج	throw new RuntimeException(e)
۱۵	کد تمیز	shutdown() با املای درست	shoudown() با املای اشتباه
📌 نکات تکمیلی:
شماره	نکته	توضیح
۱	toList() ممنوع!	bufferedReader.lines().toList() کل فایل را در حافظه می‌آورد
۲	Thread.sleep() در parseLine	باعث کندی شدید می‌شود (۱۶ دقیقه برای ۱ میلیون خط)
۳	CopyOnWriteArrayList برای نوشتن زیاد	مناسب نیست، از ArrayList معمولی استفاده کن
۴	shutdown() در Shutdown Hook	کافی است، نیازی به صدا زدن در analyzeLog() نیست
۵	IOException به جای RuntimeException	برای خطای فایل از IOException استفاده کن
۶	forEach با get()	بهتر است از for معمولی استفاده کنی

-----------------------------------
نکات طلایی که در تمرین‌ها یاد گرفتی:
-----------------------------------

هرگز Files.readAllLines() استفاده نکن ← OOM
هرگز toList() روی Stream بزرگ استفاده نکن ← OOM
هرگز Thread.sleep() در پردازش خطوط استفاده نکن ← کندی شدید
همیشه shutdown() + awaitTermination() داشته باش ← Graceful Shutdown
همیشه allOf().join() قبل از get() ← Non-blocking
همیشه Set<String> برای کاربران منحصربه‌فرد ← دقت در آمار
همیشه IOException به جای RuntimeException ← مدیریت خطای صحیح
