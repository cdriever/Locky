package com.ulvbror.locky.data;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class DataManager {

    private DataManager() {}

    static DataManager instance = new DataManager();

    public static DataManager getInstance() {
        return instance;
    }

    FileConfiguration dataconfig;
    File file;

    public LockData lockData = new LockData();

    public void setup(Plugin p) {
        if(!p.getDataFolder().exists()) {
            p.getDataFolder().mkdir();
        }

        file = new File(p.getDataFolder(), "locks.yml");
        boolean isNew = false;
        if(!file.exists()) {
            try {
                isNew = true;
                file.createNewFile();
            } catch (IOException e) {
                Bukkit.getServer().getLogger().severe(ChatColor.RED + "ERROR: Unable to create locks.yml");
            }
        }

        dataconfig = YamlConfiguration.loadConfiguration(file);
        if(isNew)
            initLockYML();
    }

    public FileConfiguration getData() {
        return dataconfig;
    }

    public void saveData() {
        try {
            dataconfig.save(file);
        } catch (IOException e) {
            Bukkit.getServer().getLogger().severe(ChatColor.RED + "ERROR: Unable to save locks.yml");
        }
    }

    public void reloadData() {
        dataconfig = YamlConfiguration.loadConfiguration(file);
    }

    private void initLockYML() {
        Bukkit.getServer().getConsoleSender().sendMessage("Initializing locks");
        //Dummy door lock

        this.getData().set("locky.doorlocks.dummy.chest_0.x", -1);
        this.getData().set("locky.doorlocks.dummy.chest_0.y", -1);
        this.getData().set("locky.doorlocks.dummy.chest_0.z", -1);
        //Dummy chest lock
        this.getData().set("locky.chestlocks.dummy.chest_0.x", -1);
        this.getData().set("locky.chestlocks.dummy.chest_0.y", -1);
        this.getData().set("locky.chestlocks.dummy.chest_0.z", -1);

        this.saveData();
    }

    public boolean registerChestLock(Vector location, UUID id) {
        if(!this.lockData.lockedChests.containsKey(location)) {
            int count = 0;
            for (UUID uuid : this.lockData.lockedChests.values()) {
                if (uuid.equals(id))
                    count++;
            }
            float x = (float) location.getX();
            float y = (float) location.getY();
            float z = (float) location.getZ();

            this.lockData.addLockedChest(location, id);
            this.getData().set("locky.chestlocks." + id + ".chest_" + count + ".x", x);
            this.getData().set("locky.chestlocks." + id + ".chest_" + count + ".y", y);
            this.getData().set("locky.chestlocks." + id + ".chest_" + count + ".z", z);
            this.saveData();
            return true;
        } else
            return false;
    }

    public void removeChestLock(Vector location, UUID id) {
        Map<String, Object> chests = this.getData().getConfigurationSection("locky.chestlocks." + id).getValues(false);
        int chestIndex = -1, count = 0;
        for (Map.Entry<String, Object> chest : chests.entrySet()) {
            String path = "locky.chestlocks." + id + "." + chest.getKey() + ".";
            double x = (double) this.getData().get(path + "x");
            double y = (double) this.getData().get(path + "y");
            double z = (double) this.getData().get(path + "z");
            Vector loc = new Vector(x,y,z);
            if(loc.equals(location)) {
                chestIndex = Integer.parseInt(chest.getKey().substring(5));
            }
            count++;
        }
        for(int i = chestIndex; i < count; i++) {
            String oldPath = "locky.chestlocks." + id + ".chest_"+(i+1)+".";
            String newPath = "locky.chestlocks." + id + ".chest_"+i+".";
            if(i != count-1) {
                float x = (float) this.getData().get(oldPath + "x");
                float y = (float) this.getData().get(oldPath + "y");
                float z = (float) this.getData().get(oldPath + "z");
                this.getData().set(newPath + "x", x);
                this.getData().set(newPath + "y", y);
                this.getData().set(newPath + "z", z);
            } else {
                this.getData().set(newPath + "x", null);
                this.getData().set(newPath + "y", null);
                this.getData().set(newPath + "z", null);
            }
        }
        this.saveData();
    }

    public void loadData(){
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Loading Locks!");
        loadChestLocks();
    }

    private void loadChestLocks() {
        Map<String, Object> players = this.getData().getConfigurationSection("locky.chestlocks").getValues(false);
        Map<String, Object> chests = null;
        for(Map.Entry<String, Object> player : players.entrySet()) {
            if(!player.getKey().equalsIgnoreCase("dummy" +
                    "")) {
                chests = this.getData().getConfigurationSection("locky.chestlocks." + player.getKey()).getValues(false);
                for (Map.Entry<String, Object> chest : chests.entrySet()) {
                    String path = "locky.chestlocks." + player.getKey() + "." + chest.getKey() + ".";
                    double x = (double) this.getData().get(path + "x");
                    double y = (double) this.getData().get(path + "y");
                    double z = (double) this.getData().get(path + "z");
                    Vector location = new Vector(x,y,z);
                    this.lockData.addLockedChest(location, UUID.fromString(player.getKey()));
                }
            }
        }
    }
}

