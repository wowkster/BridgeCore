package com.adrianwowk.bridgecore;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class GamePlayer {
    public String playerName;
    public int kills;
    public int goals;
    public int deaths;
    public TeamColor teamColor;

    public GamePlayer(String playerName, TeamColor teamColor){
        this.playerName = playerName;
        this.teamColor = teamColor;
        kills = 0;
        goals = 0;
        deaths = 0;
    }

    public Player getPlayer(){
        return Bukkit.getPlayerExact(playerName);
    }

    public void resetInventory(){
        Player p = getPlayer();
        p.teleport(teamColor.getSpawnLocation());

        p.setGameMode(GameMode.SURVIVAL); // Change Later
        p.setFlying(false);
        p.setSaturation(20f);
        p.setFoodLevel(20);
        p.setHealth(20f);
        p.setAllowFlight(false);
        p.setExp(0f);
        p.setLevel(0);
        p.setSprinting(false);
        Collection<PotionEffect> effects = p.getActivePotionEffects();
        for (PotionEffect pe : effects){
            p.removePotionEffect(pe.getType());
        }

        PlayerInventory pi = p.getInventory();
        pi.clear();
        ItemStack shirt = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack pants = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack shoes = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta meta = (LeatherArmorMeta) shirt.getItemMeta();
        meta.setColor(teamColor.getRGBColor());
        meta.spigot().setUnbreakable(true);
        shirt.setItemMeta(meta);
        pants.setItemMeta(meta);
        shoes.setItemMeta(meta);
        pi.setChestplate(shirt);
        pi.setLeggings(pants);
        pi.setBoots(shoes);
        pi.setHelmet(new ItemStack(Material.AIR));

        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta m = sword.getItemMeta();
        m.spigot().setUnbreakable(true);
        sword.setItemMeta(m);
        pickaxe.setItemMeta(m);


        pi.setItem(0, sword);
        pi.setItem(1, new ItemStack(Material.BOW));
        pi.setItem(2, pickaxe);
        pi.setItem(3, new ItemStack(Material.STAINED_CLAY, 64, (short)(teamColor == TeamColor.RED ? 14 : 11)));
        pi.setItem(4, new ItemStack(Material.STAINED_CLAY, 64, (short)(teamColor == TeamColor.RED ? 14 : 11)));
        pi.setItem(5, new ItemStack(Material.GOLDEN_APPLE, 8));
        pi.setItem(8, new ItemStack(Material.ARROW, 3));
    }
}
