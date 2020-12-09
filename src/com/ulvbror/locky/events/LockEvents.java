package com.ulvbror.locky.events;

import com.ulvbror.locky.Locky;
import com.ulvbror.locky.data.DataManager;
import com.ulvbror.locky.data.LockData;
import com.ulvbror.locky.items.ItemManager;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.util.Vector;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.UUID;

public class LockEvents implements Listener {

    @EventHandler
    public static void onRightClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            BlockState blockState = block.getState();
            if(blockState instanceof Chest) {
                Chest chest = (Chest) blockState;
                Inventory inventory = chest.getInventory();
                if (event.getItem() != null) {
                    if (event.getItem().getItemMeta().equals(ItemManager.lock_personal.getItemMeta())) {
                        Location chestLocation = inventory.getLocation();
                        if(DataManager.getInstance().registerChestLock(chestLocation.toVector(), player.getUniqueId())) {
                            player.spawnParticle(Particle.CRIT_MAGIC, chestLocation, 100);
                            player.playSound(chestLocation, Sound.BLOCK_ANVIL_PLACE, 1f, 2f);
                            player.getInventory().getItem(EquipmentSlot.HAND).setAmount(player.getInventory()
                                    .getItem(EquipmentSlot.HAND).getAmount()-1);
                            player.sendRawMessage( (ChatColor.AQUA + "Locked chest at: ")
                                    + ChatColor.YELLOW + (chestLocation.getBlockX() + ", " + chestLocation.getBlockY()
                                    + ", " + chestLocation.getBlockZ()) );
                        } else {
                            player.sendRawMessage( ChatColor.RED + "That chest is already locked!");
                        }
                    } else if(event.getItem().getItemMeta().equals(ItemManager.unlock_personal.getItemMeta())) {
                        Location chestLocation = inventory.getLocation();
                        if(DataManager.getInstance().lockData.lockedChests.containsKey(chestLocation.toVector())) {
                            if(DataManager.getInstance().lockData.lockedChests
                                    .get(chestLocation.toVector()).equals(player.getUniqueId())){
                                player.spawnParticle(Particle.CRIT_MAGIC, chestLocation, 100);
                                player.playSound(chestLocation, Sound.BLOCK_ANVIL_PLACE, 1f, 2f);
                                player.getInventory().getItem(EquipmentSlot.HAND).setAmount(player.getInventory()
                                        .getItem(EquipmentSlot.HAND).getAmount()-1);
                            } else {
                                player.sendRawMessage(ChatColor.RED + "That locked chest doesn't belong to you!");
                            }
                        } else {
                            player.sendRawMessage(ChatColor.RED + "That chest isn't locked!");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public static void onInventoryOpen(InventoryOpenEvent event) {
        if(event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            if(event.getInventory().getType() == InventoryType.CHEST) {
                Location chestLocation = event.getInventory().getLocation();
                player.sendRawMessage(ChatColor.BLUE + ("LOC: "+chestLocation.toVector()));
                if(DataManager.getInstance().lockData.lockedChests.containsKey(chestLocation.toVector()) ) {
                    UUID lockedTo = DataManager.getInstance().lockData.lockedChests.get(chestLocation.toVector());
                    if(!lockedTo.equals(player.getUniqueId())) {
                        player.sendRawMessage(ChatColor.LIGHT_PURPLE + "Sorry, this chest is locked");
                        event.setCancelled(true);
                    }
                }
                if(player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                    if(player.getInventory().getItemInMainHand().getItemMeta().equals(ItemManager.lock_personal.getItemMeta()))
                        event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public static void onBreakBlock(BlockBreakEvent event) {
        Player p = event.getPlayer();
        Block b = event.getBlock();
        if(b.getState() instanceof Chest) {
            Chest chest = (Chest) b.getState();
            Inventory inv = chest.getInventory();
            if(inv instanceof DoubleChestInventory) {
                if (DataManager.getInstance().lockData.lockedChests.containsKey(inv.getLocation().toVector())) {
                    p.sendRawMessage(ChatColor.RED + "Unable to break a locked chest, unlock it first");
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public static void onBurnBlock(BlockBurnEvent event) {
        Block b = event.getBlock();
        if(b.getState() instanceof Chest) {
            Chest chest = (Chest) b.getState();
            Inventory inv = chest.getInventory();
            if(inv instanceof DoubleChestInventory) {
                if (DataManager.getInstance().lockData.lockedChests.containsKey(inv.getLocation().toVector())) {
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public static void onEntityExplode(EntityExplodeEvent event) {
        for(Block b : new ArrayList<>(event.blockList())) {
            if(b.getState() instanceof Chest) {
                Chest chest = (Chest) b.getState();
                Inventory inv = chest.getInventory();
                if(inv instanceof DoubleChestInventory) {
                    if (DataManager.getInstance().lockData.lockedChests.containsKey(inv.getLocation().toVector())) {
                        event.blockList().remove(b);
                    }
                }
            }
        }
    }

    @EventHandler
    public static void onTryCraft(PrepareItemCraftEvent event) {
        CraftingInventory inv = event.getInventory();
        for(ItemStack item : inv) {
            if (item != null && inv.getResult() != null) {
                if(!(inv.getResult().getItemMeta().equals(item.getItemMeta())) ) {//ignore result
                    if (ItemManager.isCustom(item)) {
                        inv.setResult(null);
                    }
                }
            }
        }
    }

    @EventHandler
    public static void onPlaceBlock(BlockPlaceEvent event) {
        Player p =  event.getPlayer();
        Block newBlock = event.getBlock();
        for(BlockFace face : BlockFace.values()) {
            Block oldBlock = event.getBlockPlaced().getRelative(face);
            if(newBlock.getType() == Material.CHEST && isAdjacent(face) && oldBlock.getType() == Material.CHEST) { // placing chest
                Chest chest = (Chest) oldBlock.getState();
                Inventory inv = chest.getInventory();
                if(DataManager.getInstance().lockData.lockedChests.containsKey(oldBlock.getLocation().toVector())) {
                    if(DataManager.getInstance().lockData.lockedChests.get(oldBlock.getLocation().toVector()).equals(p.getUniqueId())) {
                        if(!(inv instanceof DoubleChestInventory)){
                            DataManager.getInstance().lockData.lockedChests.remove(oldBlock.getLocation().toVector());
                            DataManager.getInstance().registerChestLock(doubleChested(face, oldBlock.getLocation().toVector()),p.getUniqueId());
                        }
                    } else {
                        p.sendRawMessage(ChatColor.RED + "Cannot place a chest beside someone else's locked chest");
                        event.setCancelled(true);
                    }
                }
            }
            else if(newBlock.getType() == Material.HOPPER && face == BlockFace.UP && oldBlock.getType() == Material.CHEST) { //Placing hopper
                Chest chest = (Chest) oldBlock.getState();
                Inventory inv = chest.getInventory();
                if (DataManager.getInstance().lockData.lockedChests.containsKey(inv.getLocation().toVector())) {
                    p.sendRawMessage(ChatColor.RED + "Cannot place a hopper beneath a locked chest");
                    event.setCancelled(true);
                }
            }
        }
    }
    public static boolean isAdjacent(BlockFace b) {
        return (b == BlockFace.EAST || b == BlockFace.NORTH || b == BlockFace.SOUTH || b == BlockFace.WEST ||
                b == BlockFace.DOWN || b == BlockFace.UP);
    }
    public static Vector doubleChested(BlockFace face, Vector current) {
        switch (face) {
            case EAST:
                return new Vector(current.getX()-0.5,current.getY(),current.getZ());
            case WEST:
                return new Vector(current.getX()+0.5,current.getY(),current.getZ());
            case NORTH:
                return new Vector(current.getX(),current.getY(),current.getZ()+0.5);
            case SOUTH:
                return new Vector(current.getX(),current.getY(),current.getZ()-0.5);
        }
        return current;
    }
}
