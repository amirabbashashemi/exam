package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.performance;

import codeChallenge.chatgpt.eCommon.Order;
import codeChallenge.chatgpt.eCommon.User;
import codeChallenge.chatgpt.eCommon.UserDto;
import codeChallenge.chatgpt.eCommon.repo.UserRepository;
import codeChallenge.chatgpt.eCommon.srv.AddressService;
import codeChallenge.chatgpt.eCommon.srv.OrderService;

import java.util.List;

public class UserServiceOld {
    UserRepository userRepository;
    AddressService addressService;
    OrderService orderService;

    public UserDto UserService(String userId) {
        User user = userRepository.findById(userId);
        String address = addressService.getAddress(userId);
        List<Order> orders = orderService.getOrders(userId);

        return new UserDto(user, address, orders);
    }
/*
bottleneck اصلی کجاست؟
چرا design فعلی performance خوبی ندارد؟
با pure Java 21 چه تغییراتی می‌دهید تا latency کم شود؟
اگر یکی از remote callها کند شود چه می‌کنید؟
چه trade-offهایی ایجاد می‌شود؟
 */
}
