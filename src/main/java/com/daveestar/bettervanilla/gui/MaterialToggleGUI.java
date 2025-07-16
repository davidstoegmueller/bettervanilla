package com.daveestar.bettervanilla.gui;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.daveestar.bettervanilla.Main;
import com.daveestar.bettervanilla.utils.CustomGUI;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class MaterialToggleGUI implements Listener {
  private final Main _plugin;
  private final String _title;
  private final List<Material> _materials;
  private final Supplier<List<String>> _getList;
  private final Consumer<List<String>> _setList;

  public MaterialToggleGUI(String title, List<Material> materials,
      Supplier<List<String>> getter, Consumer<List<String>> setter) {
    _plugin = Main.getInstance();
    _title = title;
    _materials = materials;
    _getList = getter;
    _setList = setter;
    _plugin.getServer().getPluginManager().registerEvents(this, _plugin);
  }

  public void displayGUI(Player p, CustomGUI parent) {
    Map<String, ItemStack> entries = new HashMap<>();
    List<String> allowed = _getList.get();

    for (Material mat : _materials) {
      boolean state = allowed.contains(mat.name());
      ItemStack item = new ItemStack(mat);
      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + mat.name()));
        meta.lore(Arrays.asList(
            ChatColor.YELLOW + "» " + ChatColor.GRAY + "State: " + (state ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
            ChatColor.YELLOW + "» " + ChatColor.GRAY + "Left-Click: Toggle")
            .stream().filter(Objects::nonNull).map(Component::text).collect(Collectors.toList()));
        item.setItemMeta(meta);
      }
      entries.put(mat.name(), item);
    }

    int rows = Math.max(1, (int) Math.ceil(entries.size() / 9.0)) + 1;
    CustomGUI gui = new CustomGUI(_plugin, p,
        ChatColor.YELLOW + "" + ChatColor.BOLD + "» " + _title,
        entries, rows, null, parent,
        EnumSet.of(CustomGUI.Option.DISABLE_PAGE_BUTTON));

    Map<String, CustomGUI.ClickAction> actions = new HashMap<>();
    for (Material mat : _materials) {
      actions.put(mat.name(), new CustomGUI.ClickAction() {
        @Override
        public void onLeftClick(Player player) {
          List<String> list = new java.util.ArrayList<>(_getList.get());
          if (list.contains(mat.name())) {
            list.remove(mat.name());
          } else {
            list.add(mat.name());
          }
          _setList.accept(list);
          displayGUI(player, parent);
        }
      });
    }

    gui.setClickActions(actions);
    gui.open(p);
  }
}
