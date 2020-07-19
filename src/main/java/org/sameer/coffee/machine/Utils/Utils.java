package org.sameer.coffee.machine.Utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;
import org.sameer.coffee.machine.CoffeeMachine;
import org.sameer.coffee.machine.exceptions.InsufficientDataException;
import org.sameer.coffee.machine.exceptions.InvalidDataException;
import org.sameer.coffee.machine.pojo.Beverage;
import org.sameer.coffee.machine.pojo.Inventory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Utils {

    /**
     * Set up the inventory and recipe mapping
     * @param data JSON data
     * @return CoffeMachine object
     */
    public static CoffeeMachine initializeData(JSONObject data) throws InsufficientDataException, InvalidDataException {
        JSONObject machine = data.optJSONObject("machine");
        if(machine == null) {
            throw new InsufficientDataException("Machine is missing from input");
        }
        Map<Integer, Boolean> tapMap = new ConcurrentHashMap<>();
        initializeTaps(tapMap, machine);

        Inventory inventory = Inventory
                .builder()
                .inventoryMap(getInventoryMap(machine))
                .build();
        Map<String, Beverage> beverageMap = getRecipeMap(machine);

        return CoffeeMachine
                .builder()
                .inventory(inventory)
                .beverageMap(beverageMap)
                .name("CoffeeMachine-1")
                .tapMap(tapMap)
                .orders(new LinkedHashMap<>())
                .build();
    }

    private static Map<String, Integer> getInventoryMap(JSONObject machine) throws InsufficientDataException {
        Map<String, Integer> inventoryMap = new HashMap<>();
        JSONObject totalItemsQuantity = machine.optJSONObject("total_items_quantity");
        if(totalItemsQuantity == null) {
            throw new InsufficientDataException("Inventory info is missing from input");
        }
        Iterator<String> items = totalItemsQuantity.keys();

        try {
            while (items.hasNext()) {
                String key = items.next();
                inventoryMap.put(key, totalItemsQuantity.optInt(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inventoryMap;

    }

    private static void initializeTaps(Map<Integer, Boolean> tapMap, JSONObject machine) throws InsufficientDataException, InvalidDataException {
        JSONObject outlets = machine.optJSONObject("outlets");
        if (outlets == null) {
            throw new InsufficientDataException("Outlets is missing from input");
        }
        int numberOfOutlets = outlets.optInt("count_n");

        if (numberOfOutlets < 1) {
            throw new InvalidDataException("Number of outlets can not be less than 1");
        }

        for (int i = 1; i <= numberOfOutlets; i++) {
            tapMap.put(i, true);
        }
    }

    private static Map<String, Beverage> getRecipeMap(JSONObject machine) throws InsufficientDataException, InvalidDataException {
        Map<String, Beverage> beverageMap = new HashMap<>();
        JSONObject beverages = machine.optJSONObject("beverages");
        if(beverages == null) {
            throw new InsufficientDataException("Beverages recipe info is missing from input");
        }
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
        }  catch (JSONException je) {
            je.printStackTrace();
            throw new InvalidDataException(je.getMessage());
        }
        return beverageMap;
    }
}
