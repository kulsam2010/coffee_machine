package org.sameer;

import org.junit.Assert;
import org.junit.Test;
import org.sameer.coffee.machine.CoffeMachineDriver;
import org.sameer.coffee.machine.exceptions.InsufficientDataException;
import org.sameer.coffee.machine.exceptions.InvalidDataException;
import org.sameer.coffee.machine.pojo.Order;
import org.sameer.coffee.machine.enums.OrderStatus;

import java.util.Arrays;
import java.util.Map;

/**
 * Unit test for the coffee machine app.
 */
public class CoffeMachineDriverTest {

    /**
     * Test when an order can't be fulfilled as inventory is running low on the requested beverage's ingredient
     * @throws InsufficientDataException
     * @throws InvalidDataException
     */
    @Test
    public void testInventoryIsShortOfItems() throws InsufficientDataException, InvalidDataException {
        System.out.println("testInventoryIsShortOfItems ");
        String data = "{" +
                "  \"machine\": {" +
                "    \"outlets\": {" +
                "      \"count_n\": 3" +
                "    }," +
                "    \"total_items_quantity\": {" +
                "      \"hot_water\": 1500," +
                "      \"hot_milk\": 1500," +
                "      \"ginger_syrup\": 2100," +
                "      \"sugar_syrup\": 1100," +
                "      \"tea_leaves_syrup\": 2100" +
                "    }," +
                "    \"beverages\": {" +
                "      \"hot_tea\": {" +
                "        \"hot_water\": 200," +
                "        \"hot_milk\": 100," +
                "        \"ginger_syrup\": 10," +
                "        \"sugar_syrup\": 10," +
                "        \"tea_leaves_syrup\": 30" +
                "      }," +
                "      \"hot_coffee\": {" +
                "        \"hot_water\": 100," +
                "        \"ginger_syrup\": 30," +
                "        \"hot_milk\": 400," +
                "        \"sugar_syrup\": 50," +
                "        \"tea_leaves_syrup\": 30" +
                "      }," +
                "      \"black_tea\": {" +
                "        \"hot_water\": 300," +
                "        \"ginger_syrup\": 30," +
                "        \"sugar_syrup\": 50," +
                "        \"tea_leaves_syrup\": 30" +
                "      }," +
                "      \"green_tea\": {" +
                "        \"hot_water\": 100," +
                "        \"ginger_syrup\": 30," +
                "        \"sugar_syrup\": 50," +
                "        \"green_mixture\": 30" +
                "      }," +
                "    }" +
                "  }" +
                "}";
        CoffeMachineDriver.bootstrap(data);

        Map<String, Order> orderMap = CoffeMachineDriver.getBeverages(Arrays.asList("hot_tea", "black_tea", "hot_coffee", "green_tea"));
        for (Order order : orderMap.values()) {
            String beverage = order.getBeverage();
            if (beverage.equalsIgnoreCase("green_tea")) {
                Assert.assertEquals(OrderStatus.FAILED, order.getStatus());
            } else {
                Assert.assertEquals(OrderStatus.SUCCESSFUL, order.getStatus());
            }
        }
        System.out.println("testInventoryIsShortOfItems done.");
    }

    /**
     * Test the inventory replenish flow
     * Green tea ingredients are missing in first call.
     * So we replenish the inventory and call again.
     * @throws InsufficientDataException
     * @throws InvalidDataException
     */
    @Test
    public void testInventoryReplenished() throws InsufficientDataException, InvalidDataException {
        System.out.println("testInventoryReplenished");
        String data = "{" +
                "  \"machine\": {" +
                "    \"outlets\": {" +
                "      \"count_n\": 3" +
                "    }," +
                "    \"total_items_quantity\": {" +
                "      \"hot_water\": 500," +
                "      \"hot_milk\": 500," +
                "      \"ginger_syrup\": 100," +
                "      \"sugar_syrup\": 100," +
                "      \"tea_leaves_syrup\": 100" +
                "    }," +
                "    \"beverages\": {" +
                "      \"hot_tea\": {" +
                "        \"hot_water\": 200," +
                "        \"hot_milk\": 100," +
                "        \"ginger_syrup\": 10," +
                "        \"sugar_syrup\": 10," +
                "        \"tea_leaves_syrup\": 30" +
                "      }," +
                "      \"hot_coffee\": {" +
                "        \"hot_water\": 100," +
                "        \"ginger_syrup\": 30," +
                "        \"hot_milk\": 400," +
                "        \"sugar_syrup\": 50," +
                "        \"tea_leaves_syrup\": 30" +
                "      }," +
                "      \"black_tea\": {" +
                "        \"hot_water\": 300," +
                "        \"ginger_syrup\": 30," +
                "        \"sugar_syrup\": 50," +
                "        \"tea_leaves_syrup\": 30" +
                "      }," +
                "      \"green_tea\": {" +
                "        \"hot_water\": 100," +
                "        \"ginger_syrup\": 30," +
                "        \"sugar_syrup\": 50," +
                "        \"green_mixture\": 30" +
                "      }," +
                "    }" +
                "  }" +
                "}";
        CoffeMachineDriver.bootstrap(data);

        Map<String, Order> orderMap = CoffeMachineDriver.getBeverages(Arrays.asList("hot_tea", "black_tea", "hot_coffee", "green_tea"));
        for (Order order : orderMap.values()) {
            String beverage = order.getBeverage();
            if (beverage.equalsIgnoreCase("green_tea") || beverage.equalsIgnoreCase("hot_coffee")) {
                Assert.assertEquals(OrderStatus.FAILED, order.getStatus());
            } else {
                Assert.assertEquals(OrderStatus.SUCCESSFUL, order.getStatus());
            }
        }

        //Replenish the inventory
        CoffeMachineDriver.addToInventory("green_mixture", 1000);
        CoffeMachineDriver.addToInventory("sugar_syrup", 200);
        CoffeMachineDriver.addToInventory("hot_water", 500);


        //Request green tea again
        CoffeMachineDriver.getBeverages(Arrays.asList("green_tea"));

        Order[] arrayKeys = orderMap.values().toArray( new Order[ orderMap.size() ] );
        //Check the last entry
        Assert.assertEquals(OrderStatus.SUCCESSFUL, arrayKeys[arrayKeys.length-1].getStatus());
        System.out.println("testInventoryReplenished done.");
    }

    /**
     * Test if more items are requested than taps, only count_n requests are processed at a time
     * @throws InsufficientDataException
     * @throws InvalidDataException
     */
    @Test
    public void testMoreRequestsThanTaps() throws InsufficientDataException, InvalidDataException {
        System.out.println("testMoreRequestsThanTaps");
        String data = "{" +
                "  \"machine\": {" +
                "    \"outlets\": {" +
                "      \"count_n\": 1" +
                "    }," +
                "    \"total_items_quantity\": {" +
                "      \"hot_water\": 1500," +
                "      \"hot_milk\": 1500," +
                "      \"ginger_syrup\": 1100," +
                "      \"sugar_syrup\": 1100," +
                "      \"tea_leaves_syrup\": 1100" +
                "    }," +
                "    \"beverages\": {" +
                "      \"hot_tea\": {" +
                "        \"hot_water\": 200," +
                "        \"hot_milk\": 100," +
                "        \"ginger_syrup\": 10," +
                "        \"sugar_syrup\": 10," +
                "        \"tea_leaves_syrup\": 30" +
                "      }," +
                "      \"hot_coffee\": {" +
                "        \"hot_water\": 100," +
                "        \"ginger_syrup\": 30," +
                "        \"hot_milk\": 400," +
                "        \"sugar_syrup\": 50," +
                "        \"tea_leaves_syrup\": 30" +
                "      }," +
                "      \"black_tea\": {" +
                "        \"hot_water\": 300," +
                "        \"ginger_syrup\": 30," +
                "        \"sugar_syrup\": 50," +
                "        \"tea_leaves_syrup\": 30" +
                "      }," +
                "      \"green_tea\": {" +
                "        \"hot_water\": 100," +
                "        \"ginger_syrup\": 30," +
                "        \"sugar_syrup\": 50," +
                "        \"green_mixture\": 30" +
                "      }," +
                "    }" +
                "  }" +
                "}";
        CoffeMachineDriver.bootstrap(data);

        Map<String, Order> orderMap = CoffeMachineDriver.getBeverages(Arrays.asList("hot_tea", "black_tea", "hot_coffee"));

        //count the number of requests that waited
        int count = 0;
        for (Order order : orderMap.values()) {
            if(order.isHasWaitedForOtherOrders()) {
                count++;
            }
        }

        //One tap so one order at a time, so 2 requests should have waited
        Assert.assertEquals(2,count);
        System.out.println("testMoreRequestsThanTaps done.");
    }

    @Test
    public void unserviceableBeverageRequested() throws InsufficientDataException, InvalidDataException {
        System.out.println("unserviceableBeverageRequested");
        String data = "{" +
                "  \"machine\": {" +
                "    \"outlets\": {" +
                "      \"count_n\": 3" +
                "    }," +
                "    \"total_items_quantity\": {" +
                "      \"hot_water\": 500," +
                "      \"hot_milk\": 500," +
                "      \"ginger_syrup\": 100," +
                "      \"sugar_syrup\": 100," +
                "      \"tea_leaves_syrup\": 100" +
                "    }," +
                "    \"beverages\": {" +
                "      \"hot_tea\": {" +
                "        \"hot_water\": 200," +
                "        \"hot_milk\": 100," +
                "        \"ginger_syrup\": 10," +
                "        \"sugar_syrup\": 10," +
                "        \"tea_leaves_syrup\": 30" +
                "      }," +
                "      \"hot_coffee\": {" +
                "        \"hot_water\": 100," +
                "        \"ginger_syrup\": 30," +
                "        \"hot_milk\": 400," +
                "        \"sugar_syrup\": 50," +
                "        \"tea_leaves_syrup\": 30" +
                "      }," +
                "      \"black_tea\": {" +
                "        \"hot_water\": 300," +
                "        \"ginger_syrup\": 30," +
                "        \"sugar_syrup\": 50," +
                "        \"tea_leaves_syrup\": 30" +
                "      }," +
                "      \"green_tea\": {" +
                "        \"hot_water\": 100," +
                "        \"ginger_syrup\": 30," +
                "        \"sugar_syrup\": 50," +
                "        \"green_mixture\": 30" +
                "      }," +
                "    }" +
                "  }" +
                "}";
        CoffeMachineDriver.bootstrap(data);
        Map<String, Order> orderMap = CoffeMachineDriver.getBeverages(Arrays.asList("ginger_tea"));
        OrderStatus orderStatus = null;
        for (Order order : orderMap.values()) {
            orderStatus = order.getStatus();
        }
        Assert.assertEquals(orderStatus, OrderStatus.REJECTED);
        System.out.println("unserviceableBeverageRequested done.");
    }

    @Test(expected = InsufficientDataException.class)
    public void testEmptyMachine() throws Exception {
        System.out.println("testEmptyMachine");
        String data = "{}";
        CoffeMachineDriver.bootstrap(data);
    }

    @Test(expected = InvalidDataException.class)
    public void testInvalidJson() throws Exception {
        System.out.println("testInvalidJson");
        String data = "{" +
                "  \"machine\": {" +
                "    \"outlets\": {" +
                "      \"count_n\": 3" +
                "    }," +
                "    \"total_items_quantity\": {" +
                "      \"hot_water\": 500," +
                "      \"hot_milk\": 500," +
                "      \"ginger_syrup\": 100," +
                "      \"sugar_syrup\": 100," +
                "      \"tea_leaves_syrup\": 100" +
                "    }}";
        CoffeMachineDriver.bootstrap(data);
    }

    @Test(expected = InvalidDataException.class)
    public void testForZeroTapMachine() throws Exception {
        System.out.println("testForZeroTapMachine");
        String data = "{\n" +
                "  \"machine\": {\n" +
                "    \"outlets\": {\n" +
                "      \"count_n\": 0\n" +
                "    },\n" +
                "    \"total_items_quantity\": {\n" +
                "      \"hot_water\": 500,\n" +
                "      \"hot_milk\": 500,\n" +
                "      \"ginger_syrup\": 100,\n" +
                "      \"sugar_syrup\": 100,\n" +
                "      \"tea_leaves_syrup\": 100\n" +
                "    },\n" +
                "    \"beverages\": {\n" +
                "      \"hot_tea\": {\n" +
                "        \"hot_water\": 200,\n" +
                "        \"hot_milk\": 100,\n" +
                "        \"ginger_syrup\": 10,\n" +
                "        \"sugar_syrup\": 10,\n" +
                "        \"tea_leaves_syrup\": 30\n" +
                "      },\n" +
                "      \"hot_coffee\": {\n" +
                "        \"hot_water\": 100,\n" +
                "        \"ginger_syrup\": 30,\n" +
                "        \"hot_milk\": 400,\n" +
                "        \"sugar_syrup\": 50,\n" +
                "        \"tea_leaves_syrup\": 30\n" +
                "      },\n" +
                "      \"black_tea\": {\n" +
                "        \"hot_water\": 300,\n" +
                "        \"ginger_syrup\": 30,\n" +
                "        \"sugar_syrup\": 50,\n" +
                "        \"tea_leaves_syrup\": 30\n" +
                "      },\n" +
                "      \"green_tea\": {\n" +
                "        \"hot_water\": 100,\n" +
                "        \"ginger_syrup\": 30,\n" +
                "        \"sugar_syrup\": 50,\n" +
                "        \"green_mixture\": 30\n" +
                "      },\n" +
                "    }\n" +
                "  }\n" +
                "}";
        CoffeMachineDriver.bootstrap(data);
    }

    @Test(expected = InvalidDataException.class)
    public void testMissingInventory() throws Exception {
        System.out.println("testMissingInventory");
        String data = "{\n" +
                "  \"machine\": {\n" +
                "    \"outlets\": {\n" +
                "      \"count_n\": 0\n" +
                "    },\n" +
                "    \"beverages\": {\n" +
                "      \"hot_tea\": {\n" +
                "        \"hot_water\": 200,\n" +
                "        \"hot_milk\": 100,\n" +
                "        \"ginger_syrup\": 10,\n" +
                "        \"sugar_syrup\": 10,\n" +
                "        \"tea_leaves_syrup\": 30\n" +
                "      },\n" +
                "      \"hot_coffee\": {\n" +
                "        \"hot_water\": 100,\n" +
                "        \"ginger_syrup\": 30,\n" +
                "        \"hot_milk\": 400,\n" +
                "        \"sugar_syrup\": 50,\n" +
                "        \"tea_leaves_syrup\": 30\n" +
                "      },\n" +
                "      \"black_tea\": {\n" +
                "        \"hot_water\": 300,\n" +
                "        \"ginger_syrup\": 30,\n" +
                "        \"sugar_syrup\": 50,\n" +
                "        \"tea_leaves_syrup\": 30\n" +
                "      },\n" +
                "      \"green_tea\": {\n" +
                "        \"hot_water\": 100,\n" +
                "        \"ginger_syrup\": 30,\n" +
                "        \"sugar_syrup\": 50,\n" +
                "        \"green_mixture\": 30\n" +
                "      },\n" +
                "    }\n" +
                "  }\n" +
                "}";
        CoffeMachineDriver.bootstrap(data);
    }
}
