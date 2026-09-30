package com.example.codeChallenge.excercise.exam3.order.processor.me2;

import java.util.Date;

public class Order {
    Long id;
    Date createdDate;
    Boolean processed;

    public Long getId() {
        return null;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getProcessed() {
        return processed;
    }

    public Boolean isProcessed() {
        return processed;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }
}
