package org.sameer.coffee.machine;

import org.json.JSONException;
import org.json.JSONObject;
import org.sameer.coffee.machine.Utils.Constants;
import org.sameer.coffee.machine.Utils.Utils;
import org.sameer.coffee.machine.enums.OrderStatus;
import org.sameer.coffee.machine.exceptions.IngredientsNotAvailableException;
import org.sameer.coffee.machine.exceptions.InsufficientDataException;
import org.sameer.coffee.machine.exceptions.InvalidBeverageException;
import org.sameer.coffee.machine.exceptions.InvalidDataException;
import org.sameer.coffee.machine.pojo.Order;

import java.sql.Timestamp;
import java.util.*;

public class CoffeMachineDriver {
    private static CoffeeMachine coffeeMachine;

    public static void main(String[] args) {
/*        String dataString = args[0];
        bootstrap(dataString);
        getBeverages(Arrays.asList("hot_coffe", "green_tea"));*/
    }

    /**
     * Request a list of beverages
     * @param beverages list of name of beverages
     * @return
     */
    public static Map<String, Order> getBeverages(List<String> beverages) {
        for (String beverage : beverages) {
            String orderId = UUID.randomUUID().toString();
            Order order = Order.builder()
                    .orderId(orderId)
                    .beverage(beverage)
                    .status(OrderStatus.RECEIVED)
                    .startTime(new Timestamp(System.currentTimeMillis()))
                    .build();
            processOrder(order);
        }

        //wait while all the orders are served
        while (!areAllOrdersServed(coffeeMachine.getOrders())) {
            try {
                Thread.sleep(Constants.EXECUTION_TIME/2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        printOrderBook();
        return coffeeMachine.getOrders();

    }

    /**
     * Print the order book of machine
     */
    private static void printOrderBook(){
        System.out.println("Order book");
        coffeeMachine.getOrders().forEach((k, v) -> System.out.println(v.getOrderId() + " :: " + v.getBeverage() + "  " + v.getStatus()
                + " start: " + v.getStartTime() + " :: end:" + v.getEndTime()));
        System.out.println("==========");
    }

    /**
     * Check if all the orders are served or not
     * @param beverageOrderMap
     * @return
     */
    private static boolean areAllOrdersServed(Map<String, Order> beverageOrderMap) {
        Set<OrderStatus> runningStatus = new HashSet<>();
        runningStatus.add(OrderStatus.IN_PROGRESS);
        runningStatus.add(OrderStatus.RECEIVED);
        for (Order order : beverageOrderMap.values()) {
            if (runningStatus.contains(order.getStatus())) {
                return false;
            }
        }
        return true;

    }

    /**
     * Process an order:
     * 1. Update the orderBook
     * 2. Order machine to get beverage
     * @param order
     */
    private static void processOrder(Order order) {
        System.out.println(order.getOrderId() + " :: " + order.getBeverage() + " received ");
        coffeeMachine.appendToOrderBook(order);
        try {
            coffeeMachine.getBeverage(order.getOrderId());
        } catch (IngredientsNotAvailableException | InvalidBeverageException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Initialize coffee machine with the dataString
     * @param dataString json for the coffe machine
     * @throws InsufficientDataException
     * @throws InvalidDataException
     */
    public static void bootstrap(String dataString) throws InsufficientDataException, InvalidDataException {
        JSONObject data;
        try {
            data = new JSONObject(dataString);
        } catch (JSONException e) {
            throw new InvalidDataException(e.getMessage());
        }
        coffeeMachine = Utils.initializeData(data);
    }

    /**
     * Replenish inventory with ingredient
     * @param ingredient
     * @param quantity
     * @throws InvalidDataException
     */
    public static void addToInventory(String ingredient, int quantity) {
        coffeeMachine.addToInventory(ingredient, quantity);
    }



}
