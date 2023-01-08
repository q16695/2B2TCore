package me.miku.main;

import org.bukkit.entity.Chicken;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ChickenData {
    public Chicken chicken;
    public String uuid;
    public ItemStack shulker;
    public int time = 0;
    public int randomTime = 0;

    public ChickenData(Chicken chicken, UUID uuid, ItemStack shulker) {
        this.chicken = chicken;
        this.uuid = uuid.toString();
        this.shulker = shulker;
    }

    public ChickenData(Chicken chicken, String uuid, ItemStack shulker) {
        this.chicken = chicken;
        this.uuid = uuid;
        this.shulker = shulker;
    }
}
