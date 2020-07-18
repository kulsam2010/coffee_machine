package org.sameer.coffee.machine.enums;

/**
 * Enum to hold the status of orders
 * Three state flows possible:
 * 1. RECEIVED -> REJECTED
 * 2. RECEIVED -> FAILED
 * 2. RECEIVED -> IN_PROGRESS -> SUCCESSFUL
 */
public enum OrderStatus {
    SUCCESSFUL("successful"),
    RECEIVED("received"),
    IN_PROGRESS("in_progress"),
    REJECTED("rejected"),
    FAILED("failed");
    private String status;

    OrderStatus(String status) {
        this.status= status;
    }
    public String getStatus(){
        return this.status;
    }

}
