package coffeeMachine;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
class Beverage {
    private String name;
    private long etaInMilliseconds;
    private Map<String, Integer> recipe;
}

