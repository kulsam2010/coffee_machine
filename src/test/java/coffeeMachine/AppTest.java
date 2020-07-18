package coffeeMachine;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

/**
 * Unit test for the coffee machine app.
 */
public class AppTest 
{

    @Test
    public void shouldAnswerWithTrue()
    {
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
        App.bootstrap(data);

        Map<String, Order> orderMap = App.getBeverages(Arrays.asList("hot_tea", "black_tea", "hot_coffee","green_tea" ));
        for(Order order : orderMap.values()) {
            String beverage = order.getBeverage();
            if(beverage.equalsIgnoreCase("green_tea") || beverage.equalsIgnoreCase("hot_coffee") ) {
                Assert.assertEquals(order.getStatus(), OrderStatus.FAILED);
            } else {
                Assert.assertEquals(order.getStatus(), OrderStatus.SUCCESSFUL);
            }
        }
    }

    @Test
    public void unsurviceableBeverageRequested()
    {
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
        App.bootstrap(data);

        Map<String, Order> orderMap = App.getBeverages(Arrays.asList( "ginger_tea" ));
        OrderStatus orderStatus = null;
        for(Order order : orderMap.values()) {
            orderStatus = order.getStatus();
        }
        Assert.assertEquals(orderStatus, OrderStatus.REJECTED);
    }
}
