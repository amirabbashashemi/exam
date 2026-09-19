package com.example.codeChallenge.chatgpt.cSecurityAndCompliance.security.old;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import codeChallenge.chatgpt.eCommon.User;
import codeChallenge.chatgpt.eCommon.repo.UserRepository;

import java.io.IOException;

public class UserHandler implements HttpHandler {

    private final UserRepository repository = new UserRepository();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.startsWith("/users/")) {
            Long userId = Long.parseLong(path.replace("/users/", ""));
            User user = repository.findById(userId);

            String response = user.toString();
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        }
    }
}
/*
کد را طوری بازطراحی کنید که:

اطلاعات حساس کاربران افشا نشود
احراز هویت و دسترسی کنترل شود
API در برابر سوءاستفاده امن‌تر شود
داده‌ها به‌صورت امن نگهداری و منتقل شوند
اصول پایه Security در طراحی رعایت شوند
 */

