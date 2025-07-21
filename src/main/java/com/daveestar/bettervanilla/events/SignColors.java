package com.daveestar.bettervanilla.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class SignColors implements Listener {
  @EventHandler
  public void onSignChange(SignChangeEvent e) {
    for (int i = 0; i < e.lines().size(); i++) {
      Component current = e.line(i);
      String raw = PlainTextComponentSerializer.plainText().serialize(current);
      Component colored = LegacyComponentSerializer.legacyAmpersand().deserialize(raw);

      e.line(i, colored);
    }
  }
}
