package org.sameer.coffee.machine.pojo;

import lombok.Builder;
import lombok.Data;
import org.sameer.coffee.machine.pojo.Beverage;

import java.util.Map;

/**
 * Class to that holds the inventory of the machine
 */
@Builder
@Data
public class Inventory {
    private Map<String, Integer> inventoryMap;

    public int getQuantity(String ingredientName) {
        return inventoryMap.getOrDefault(ingredientName,0);
    }

    public void updateStock(String ingredientName, int quantity) {
        if(inventoryMap.containsKey(ingredientName)) {
            inventoryMap.put(ingredientName, inventoryMap.get(ingredientName) + quantity);
        }
    }

    public void increaseQuantity(String ingredientName, int quantity) {
        int currentQuantity = getQuantity(ingredientName);
        inventoryMap.put(ingredientName, currentQuantity + quantity);
    }

    public void decreaseQuantity(String ingredientName, int quantity) {
        int currentQuantity = getQuantity(ingredientName);
        int updatedQuantity = (currentQuantity-quantity >0)? currentQuantity -quantity:0;
        inventoryMap.put(ingredientName, updatedQuantity);
    }

    public void updateInventoryAfterDispnesingBeverage(Beverage beverage){
        for(String ingredient: beverage.getRecipe().keySet() ) {
            decreaseQuantity(ingredient, beverage.getRecipe().get(ingredient));
        }

    }
}
