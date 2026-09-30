package com.example.codeChallenge.excercise.exam3.observability;

public class Order {
    Long id;
    String traceId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", traceId='" + traceId + '\'' +
                '}';
    }
}
