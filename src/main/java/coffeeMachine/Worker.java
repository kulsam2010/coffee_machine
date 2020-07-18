package coffeeMachine;

import lombok.AllArgsConstructor;

import java.sql.Timestamp;
import java.util.Map;


@AllArgsConstructor
public class Worker implements Runnable {
    private long sleepTime;
    private int tap;
    private Map<Integer, Boolean> tapMap;
    String orderId;
    Map<String, Order> orderMap;

    public void run() {
        {
            try {
                Order order = orderMap.get(orderId);
//                System.out.println(order.getBeverage() + " :: Brewing started from tap " + tap + " " + orderId);
                order.setStatus(OrderStatus.IN_PROGRESS);
                orderMap.put(orderId, order);
                Thread.sleep(sleepTime);
//                System.out.println(orderId + " :: " +order.processOrder() + " is served. Enjoy! Tap " + tap + " is free");
                tapMap.put(tap, true);
                System.out.println();
                order = orderMap.get(orderId);
                order.setStatus(OrderStatus.SUCCESSFUL);
                order.setEndTime(new Timestamp(System.currentTimeMillis()));
                orderMap.put(orderId, order);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
