package com.ulvbror.locky.data;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LockData {

    public Map<Vector, UUID> lockedChests;
    public Map<Vector, UUID> lockedDoors;

    public LockData() {
        lockedChests = new HashMap<>();
        lockedDoors = new HashMap<>();
    }

    public void addLockedChest(Vector location, UUID id) {
        if(!this.lockedChests.containsKey(location))
            this.lockedChests.put(location,id);
    }

    public void removeLockedChest(Vector location, UUID id) {
        this.lockedChests.remove(location);
    }


    public void addLockedDoor(Vector location, UUID id) {
        if(!this.lockedDoors.containsKey(location))
            this.lockedDoors.put(location,id);
    }

    public void removeLockedDoor(Vector location, UUID id) {
        this.lockedDoors.remove(location);
    }
}
