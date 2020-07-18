package org.sameer.coffee.machine.pojo;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class Beverage {
    private String name;
    private long etaInMilliseconds;
    private Map<String, Integer> recipe;
}

