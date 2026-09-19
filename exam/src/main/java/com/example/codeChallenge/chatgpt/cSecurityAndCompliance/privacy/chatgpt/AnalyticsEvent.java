package com.example.codeChallenge.chatgpt.cSecurityAndCompliance.privacy.chatgpt;

public class AnalyticsEvent {
    private final String anonymizedUserId;
    private final String action;
    private final long timestamp;

    public AnalyticsEvent(String anonymizedUserId, String action, long timestamp) {
        this.anonymizedUserId = anonymizedUserId;
        this.action = action;
        this.timestamp = timestamp;
    }

    public String getAnonymizedUserId() {
        return anonymizedUserId;
    }

    public String getAction() {
        return action;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

/*
دیتایی که «ندونی برای کیه» بی‌ارزش نیست،
ولی دیتایی که «ندونی برای چی جمع شده» کاملاً بی‌ارزشه.

Privacy میگه:
Who → مهم نیست
What / Why / How often → مهمه


-----------------------------
Privacy یعنی چی؟ (در سیستم دیزاین)

Privacy یعنی:

سیستم طوری طراحی بشه که کمترین اطلاعات ممکن از افراد جمع‌آوری، نگهداری و قابل شناسایی باشه.

نه بیشتر، نه کمتر.

سه اصل طلایی Privacy
1️⃣ Data Minimization

فقط دیتایی رو بگیر که واقعاً لازم داری

❌ اسم، ایمیل، IP وقتی برای تحلیل لازم نیست
✅ event، زمان، نوع action

2️⃣ Purpose Limitation

هر دیتایی باید هدف مشخص داشته باشه

اگر نتونی بگی:

«این دیتا برای چی گرفته شده؟»

→ نباید ذخیره بشه.

3️⃣ Identifiability Control

سیستم نباید بتونه به‌راحتی بفهمه «این کیه»

راهش:

Hash

Pseudonym

Aggregation

حذف mapping

یک جمله طلایی (حفظ کن برای امتحان)

Privacy is not about hiding data, it is about removing unnecessary identity.

یا ساده‌تر:

What happened matters, who did it usually doesn’t.

مثال ۱۰ ثانیه‌ای
❌ بدون Privacy
userId=123
email=a@b.com
clicked buy at 10:32

✅ با Privacy
anonymousUser=8f92ab...
clicked buy at 10:32
 */