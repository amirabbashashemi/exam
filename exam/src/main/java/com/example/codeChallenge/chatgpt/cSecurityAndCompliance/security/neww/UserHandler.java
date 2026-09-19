package com.example.codeChallenge.chatgpt.cSecurityAndCompliance.security.neww;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import codeChallenge.chatgpt.eCommon.UserDto;
import codeChallenge.chatgpt.eCommon.repo.UserRepository;

import java.io.IOException;

public class UserHandler implements HttpHandler {

    private final UserRepository repository = new UserRepository();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        // Authentication
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.sendResponseHeaders(401, -1);
            return;
        }

        String token = authHeader.substring(7);
        if (!JwtUtil.validateToken(token)) {
            exchange.sendResponseHeaders(401, -1);
            return;
        }

        Long currentUserId;
        try {
            currentUserId = JwtUtil.extractUserIdFromToken(token);
        } catch (Exception e) {
            exchange.sendResponseHeaders(401, -1);
            return;
        }

        // Authorization
        if (path.startsWith("/users/")) {
            Long requestedUserId;
            try {
                requestedUserId = Long.parseLong(path.replace("/users/", ""));
            } catch (NumberFormatException e) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            if (!currentUserId.equals(requestedUserId)) {
                exchange.sendResponseHeaders(403, -1);
                return;
            }

            UserDto userDto = repository.findById(requestedUserId);
            if (userDto == null) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            String response = userDto.toString();
            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        } else {
            exchange.sendResponseHeaders(404, -1);
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

