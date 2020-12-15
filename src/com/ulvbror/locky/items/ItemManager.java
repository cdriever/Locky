package com.ulvbror.locky.items;

import com.sun.istack.internal.Nullable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {

    public static ItemStack lock_personal;
    public static ItemStack unlock_personal;

    public static void init() {
        createLockPersonal();
        createUnlockPersonal();
    }

    private static void createLockPersonal() {
        ItemStack item = new ItemStack(Material.GUNPOWDER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + " Personal Locking Powder");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "This will lock a chest/door to your person");
        meta.setLore(lore);
        meta.addEnchant(Enchantment.LUCK, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        lock_personal = item;
    }
    public static boolean isLockPersonal(ItemStack item) {
        if(item.getType() == Material.GUNPOWDER) {
            return item.getItemMeta().getDisplayName()
                    .equalsIgnoreCase(ChatColor.LIGHT_PURPLE + " Personal Locking Powder");
        }
        return false;
    }

    private static void createUnlockPersonal() {
        ItemStack item = new ItemStack(Material.SUGAR, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + " Personal Unlocking Powder");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "This will unlock a chest/door from your person");
        meta.setLore(lore);
        meta.addEnchant(Enchantment.LUCK, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        unlock_personal = item;
    }
    public static boolean isUnlockPersonal(ItemStack item) {
        if(item.getType() == Material.SUGAR) {
            return item.getItemMeta().getDisplayName()
                    .equalsIgnoreCase(ChatColor.LIGHT_PURPLE + " Personal Unlocking Powder");
        }
        return false;
    }

    public static boolean isCustom(ItemStack item) {
        return ( isLockPersonal(item) || isUnlockPersonal(item) );
    }
}
