package com.daveestar.bettervanilla.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;

public final class ItemStackUtils {

  // private constructor to prevent instantiation
  private ItemStackUtils() {
  }

  public static Map<String, Object> serialize(ItemStack item) {
    return item != null ? item.serialize() : null;
  }

  public static ItemStack deserialize(Map<String, Object> data) {
    return data != null ? ItemStack.deserialize(data) : null;
  }

  public static List<Map<String, Object>> serializeArray(ItemStack[] items) {
    return Arrays.stream(Optional.ofNullable(items).orElse(new ItemStack[0]))
        .map(ItemStackUtils::serialize)
        .collect(Collectors.toList());
  }

  public static ItemStack[] deserializeArray(List<?> data) {
    List<?> list = Optional.ofNullable(data).orElse(Collections.emptyList());
    ItemStack[] result = new ItemStack[list.size()];

    for (int i = 0; i < list.size(); i++) {
      Object obj = list.get(i);
      if (obj instanceof Map) {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) obj;
        result[i] = deserialize(map);
      } else {
        result[i] = null;
      }
    }

    return result;
  }
}
