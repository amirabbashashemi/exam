package com.example.codeChallenge.excercise.exam2.availability;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerService {
    private static final Map<Long, Customer> LONG_CUSTOMER_MAP = new ConcurrentHashMap<>();

    private final RestClient restClient = new RestClient();

    public Customer load(Long id) {

        Customer customer = restClient.get("/customers/" + id);

        LONG_CUSTOMER_MAP.put(id, customer);

        return LONG_CUSTOMER_MAP.get(id);
    }
}