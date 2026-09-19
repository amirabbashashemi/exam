package com.example.codeChallenge.chatgpt.aCoreSystemDesignAttributes.performance;

import codeChallenge.chatgpt.eCommon.UserDto;
import codeChallenge.chatgpt.eCommon.repo.UserRepository;
import codeChallenge.chatgpt.eCommon.srv.AddressService;
import codeChallenge.chatgpt.eCommon.srv.OrderService;

import java.util.concurrent.StructuredTaskScope;

public class UserService {
    private UserRepository userRepository;
    private AddressService addressService;
    private OrderService orderService;

    public UserDto UserService(String userId) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            var user = scope.fork(() -> userRepository.findById(userId));
            var address = scope.fork(() -> addressService.getAddress(userId));
            var orders = scope.fork(() -> orderService.getOrders(userId));

            scope.join();
            scope.throwIfFailed();

            return new UserDto(
                    user.get(),
                    address.get(),
                    orders.get()
            );
        } catch (Exception exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("An exception occurred in method UserService", exception);
        }
    }

}
