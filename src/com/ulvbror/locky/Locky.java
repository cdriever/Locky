package com.ulvbror.locky;

import com.ulvbror.locky.commands.LockCommands;
import com.ulvbror.locky.data.DataManager;
import com.ulvbror.locky.data.LockData;
import com.ulvbror.locky.events.LockEvents;
import com.ulvbror.locky.items.ItemManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Locky extends JavaPlugin {

    public static final String PLUGIN_ID = "[Locky]";
    private final DataManager dataManager = DataManager.getInstance();

    @Override
    public void onEnable() {
        ItemManager.init();
        getCommand("givelock").setExecutor(new LockCommands());
        getServer().getPluginManager().registerEvents(new LockEvents(), this);

        dataManager.setup(this);
        dataManager.loadData();

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + (Locky.PLUGIN_ID +  " Plugin enabled!") );
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + (Locky.PLUGIN_ID +  " Plugin disabled!") );
    }
}
