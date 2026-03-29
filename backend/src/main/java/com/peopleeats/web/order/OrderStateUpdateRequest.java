package com.peopleeats.web.order;

import jakarta.validation.constraints.NotBlank;

public class OrderStateUpdateRequest {
    @NotBlank
    private String state;
    private String riderName;

    public OrderStateUpdateRequest() {
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }
}
