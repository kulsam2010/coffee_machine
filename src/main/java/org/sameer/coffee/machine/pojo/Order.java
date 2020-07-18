package org.sameer.coffee.machine.pojo;

import lombok.Builder;
import lombok.Data;
import org.sameer.coffee.machine.enums.OrderStatus;

import java.sql.Timestamp;


/**
 * POJO to hold details of an order
 */
@Data
@Builder
public class Order {
    private String orderId;
    private OrderStatus status;
    private String beverage;
    private Timestamp startTime;
    private Timestamp endTime;
}
