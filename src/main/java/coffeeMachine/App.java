package coffeeMachine;

import coffeeMachine.Exceptions.IngredientsNotAvailableException;
import coffeeMachine.Exceptions.InvalidBeverageException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.*;

public class App {
    private static CoffeeMachine coffeeMachine;
    private static Map<String, Order> orders;

    public static void main(String[] args) {
//        String dataString = args[0];
//        bootstrap(dataString);
//        getBeverages(Arrays.asList("hot_coffe", "green_tea"));
    }

    public static Map<String, Order> getBeverages(List<String> beverages) {
        for (String s : beverages) {
            String orderId = UUID.randomUUID().toString();
            Order order = Order.builder()
                    .orderId(orderId)
                    .beverage(s)
                    .status(OrderStatus.RECEIVED)
                    .startTime(new Timestamp(System.currentTimeMillis()))
                    .build();
            orders.put(orderId, order);
            processOrder(order);
        }
        while (!areAllOrdersServed(orders)) {
            try {
                Thread.sleep(Constants.EXECUTION_TIME/2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        orders.forEach((k, v) -> System.out.println(v.getOrderId() + " :: " + v.getBeverage() + "  " + v.getStatus()
                + " start: " + v.getStartTime() + " :: end:" + v.getEndTime()));
        return orders;

    }

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

    public static void processOrder(Order order) {
        System.out.println(order.getOrderId() + " :: " + order.getBeverage() + " received ");
        try {
            coffeeMachine.getBeverage(order.getOrderId(), orders);
        } catch (IngredientsNotAvailableException | InvalidBeverageException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void bootstrap(String dataString) {
        orders = new LinkedHashMap<>();
        JSONObject data = null;
        try {
            data = new JSONObject(dataString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(data != null) {
            coffeeMachine = initializeData(data);
        }
    }

    /**
     * Set up the inventory and recipe mapping
     * @param data JSON data
     * @return CoffeMachine object
     */
    private static CoffeeMachine initializeData(JSONObject data) {
        JSONObject machine = data.optJSONObject("machine");
        JSONObject outlets = machine.optJSONObject("outlets");
        int numberOfOutlets = outlets.optInt("count_n");

        Map<Integer, Boolean> tapMap = new HashMap<>();
        for (int i = 1; i <= numberOfOutlets; i++) {
            tapMap.put(i, true);
        }


        Map<String, Integer> inventoryMap = new HashMap<>();

        JSONObject totalItemsQuantity = machine.optJSONObject("total_items_quantity");
        Iterator<String> items = totalItemsQuantity.keys();

        try {
            while (items.hasNext()) {
                String key = items.next();
                inventoryMap.put(key, totalItemsQuantity.optInt(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Inventory inventory = Inventory.builder().inventoryMap(inventoryMap).build();
        Map<String, Beverage> beverageMap = new HashMap<>();

        JSONObject beverages = machine.optJSONObject("beverages");
        Iterator<String> keys = beverages.keys();

        try {
            while (keys.hasNext()) {
                String key = keys.next();
                if (beverages.get(key) instanceof JSONObject) {
                    Map<String, Integer> recipeMap = new Gson().fromJson(
                            ((JSONObject) beverages.get(key)).toString(), new TypeToken<HashMap<String, Integer>>() {
                            }.getType()
                    );
                    Beverage beverage = Beverage.builder()
                            .name(key)
                            .recipe(recipeMap)
                            .etaInMilliseconds(Constants.EXECUTION_TIME)
                            .build();
                    beverageMap.put(key, beverage);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return CoffeeMachine
                .builder()
                .inventory(inventory)
                .beverageMap(beverageMap)
                .name("CofeeMachine-1")
                .taps(numberOfOutlets)
                .tapMap(tapMap)
                .build();
    }

}
