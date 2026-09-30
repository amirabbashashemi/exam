package com.example.codeChallenge.excercise.exam3.catalog.ds;
/*
یک سرویس ProductCatalogService داریم که اطلاعات محصولات را از دیتابیس می‌خواند و به کاربران نمایش می‌دهد.
 در حال حاضر روزانه ۱۰۰۰ درخواست به این سرویس می‌رسد و هر درخواست حدود ۵۰۰ میلی‌ثانیه طول می‌کشد (چون هر بار به دیتابیس مراجعه می‌کند).
  پیش‌بینی می‌شود در فصل تخفیف‌ها، تعداد درخواست‌ها به ۱۰۰,۰۰۰ درخواست در روز برسد و هر کاربر ممکن است چندین بار یک محصول را ببیند.
   با کد فعلی، دیتابیس به گلوگاه تبدیل می‌شود و زمان پاسخ به شدت افزایش می‌یابد، به‌طوری که برخی درخواست‌ها بیش از ۱۰ ثانیه طول می‌کشند و کاربران صفحه را ترک می‌کنند.
 */

public class ProductCatalogService {
    public Product getProduct(Long productId) {

        // هر بار به دیتابیس مراجعه می‌کند
        return fetchFromDatabase(productId);
    }

    private Product fetchFromDatabase(Long productId) {
        // شبیه‌سازی کوئری دیتابیس
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        return new Product(productId, "Product " + productId);
    }
}