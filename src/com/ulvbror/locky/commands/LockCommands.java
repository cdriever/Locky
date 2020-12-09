package com.ulvbror.locky.commands;

import com.ulvbror.locky.items.ItemManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LockCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only Player can use that command!");
            return true;
        }

        Player player = (Player) sender;
        if(command.getName().equalsIgnoreCase("givelock")) {
            if(args.length <= 0) {
                sender.sendMessage(ChatColor.RED + "Invalid usage of /givelock, please");
                sender.sendMessage(ChatColor.RED + "type '/locky help' for more info");
                return true;
            }

            if(args[0].equalsIgnoreCase("personal")) {
                int count = 1;
                if(args.length > 1) {
                    try {
                        count = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Invalid usage of /givelock personal <amount>,");
                        sender.sendMessage(ChatColor.RED + "the amount given must be a valid integer");
                        return true;
                    }
                    if(count >= 1 && count <= 64) {
                        ItemStack temp = ItemManager.lock_personal;
                        temp.setAmount(count);
                        player.getInventory().addItem(temp);
                        sender.sendMessage(ChatColor.AQUA + (count + " Personal lock(s) added to inventory"));
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid usage of /givelock personal <amount>,");
                        sender.sendMessage(ChatColor.RED + "the amount given must be between 1 to 64");
                        return true;
                    }
                } else {
                    player.getInventory().addItem(ItemManager.lock_personal);
                    sender.sendMessage(ChatColor.AQUA + "Personal lock added to inventory");
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + "Invalid usage of /givelock, please");
                sender.sendMessage(ChatColor.RED + "type '/locky help' for more info");
                return true;
            }
        }

        return true;
    }
}
