package com.fool.gamearchivemanager.util;

import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

public class CollUtils {

    public static <T, R> Map<T, R> firstValueMap(MultiValueMap<T, R> multiValueMap) {
        Map<T, R> map = new HashMap<>();

        multiValueMap.forEach((k, v) -> {
            map.put(k, v.getFirst());
        });

        return map;
    }

}
