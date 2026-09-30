package com.example.codeChallenge.excercise.exam3.observability.me;

import java.util.Date;

public class Order {
    Long id;
    String traceId;
    Date createdDate;
    Date endDate;

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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", traceId='" + traceId + '\'' +
                '}';
    }
}
