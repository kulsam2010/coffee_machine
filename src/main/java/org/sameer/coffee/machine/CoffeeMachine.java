package org.sameer.coffee.machine;

import lombok.Builder;
import lombok.Data;
import org.sameer.coffee.machine.Utils.Constants;
import org.sameer.coffee.machine.Utils.Worker;
import org.sameer.coffee.machine.enums.OrderStatus;
import org.sameer.coffee.machine.exceptions.IngredientsNotAvailableException;
import org.sameer.coffee.machine.exceptions.InvalidBeverageException;
import org.sameer.coffee.machine.pojo.Beverage;
import org.sameer.coffee.machine.pojo.Inventory;
import org.sameer.coffee.machine.pojo.Order;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Data
@Builder
public class CoffeeMachine {
    private String name;
    private Inventory inventory;
    private Map<String, Beverage> beverageMap;
    private Map<Integer, Boolean> tapMap;
    private Map<String, Order> orders;


    public void getBeverage(String orderId) throws IngredientsNotAvailableException, InvalidBeverageException {
        Order order = orders.get(orderId);
        Beverage beverage = beverageMap.get(order.getBeverage());

        if (beverage == null) {
            order.setStatus(OrderStatus.REJECTED);
            order.setEndTime(new Timestamp(System.currentTimeMillis()));
            orders.put(orderId, order);
            throw new InvalidBeverageException(name + " Sorry, we don't serve this drink. Please check back letter.");
        }

        if (!isMakeable(beverage)) {
            order.setStatus(OrderStatus.FAILED);
            order.setEndTime(new Timestamp(System.currentTimeMillis()));
            orders.put(orderId, order);
            throw new IngredientsNotAvailableException(name + " Sorry, ingredients are not available ");

        }
        while (true) {
            Integer freeTap = getFreeTap();
            if (freeTap != null) {
                inventory.updateInventoryAfterDispnesingBeverage(beverage);
                brew(beverage,freeTap,orderId);
                return;
            } else {
                order.setHasWaitedForOtherOrders(true);
                try {
                    Thread.sleep(Constants.EXECUTION_TIME/2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                System.out.println(beverage.getName() + " Sorry, the taps are full. Please be patient!");
            }

        }
    }

    private void brew(Beverage beverage, int tap, String orderId) {
        try {
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.schedule(new Worker(beverage.getEtaInMilliseconds(), tap, tapMap, orderId, orders),0, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Checks if machine has sufficient inventory to make the beverage
     * @param beverage
     * @return
     */
    private boolean isMakeable(Beverage beverage){
        Map<String, Integer> inventoryMap = inventory.getInventoryMap();
        Map<String, Integer> recipe = beverage.getRecipe();

        for(String ingredient: recipe.keySet() ) {
            if(!inventoryMap.containsKey(ingredient) || inventoryMap.containsKey(ingredient) && inventoryMap.get(ingredient) < recipe.get(ingredient)) {
                System.out.println("Sorry we have run out of " + ingredient + " for beverage " + beverage.getName());
                return false;
            }
        }
        return true;

    }

    /**
     * Gets any available free tap
     * @return
     */
    private Integer getFreeTap(){
        for (Map.Entry<Integer, Boolean> entry : tapMap.entrySet()) {
            if(entry.getValue() != null && entry.getValue()) {
                entry.setValue(false);
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Add ingredient to inventory
     * @param ingredientName
     * @param quantity
     */
    public void addToInventory(String ingredientName, int quantity) {
        this.inventory.increaseQuantity(ingredientName, quantity);

    }

    public void appendToOrderBook(Order order) {
        this.orders.put(order.getOrderId(), order);
    }
}
