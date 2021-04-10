package com.adrianwowk.bridgecore.events;

import com.adrianwowk.bridgecore.*;
import com.sun.media.jfxmedia.events.BufferListener;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SpigotPluginEvents implements Listener {

    SpigotPlugin instance;
    Game game;
    boolean goalOpen = true;

    public SpigotPluginEvents(SpigotPlugin plugin) {
        this.instance = plugin;
        this.game = instance.getGame();
    }

    @EventHandler
        // One method for every event
    void onPlayerJoin(final PlayerJoinEvent event) {
        event.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 36.5d, 10.0d, 0.5d, 90.0f, 0.0f));
        event.setJoinMessage(ChatColor.YELLOW + event.getPlayer().getName()
                + ChatColor.WHITE + " has joined the game");
        ItemStack empty = new ItemStack(Material.AIR);
        event.getPlayer().getInventory().setChestplate(empty);
        event.getPlayer().getInventory().setLeggings(empty);
        event.getPlayer().getInventory().setBoots(empty);
        event.getPlayer().getInventory().clear();
        event.getPlayer().setGameMode(GameMode.ADVENTURE);

        event.getPlayer().setDisplayName("[YOUTUBE] " + event.getPlayer().getName());
        event.getPlayer().setPlayerListName("[YOUTUBE] " + event.getPlayer().getName());
        event.getPlayer().setCustomName("[YOUTUBE] " + event.getPlayer().getName());
        event.getPlayer().setCustomNameVisible(true);
    }

    @EventHandler
    void onPlayerLeave(final PlayerQuitEvent event) {
        if (game.hasStartedCounting()) {
            game.cancelGame();
        }
        if (game.playerIsOnTeam(event.getPlayer())) {
            TeamColor tc = game.getGamePlayer(event.getPlayer()).teamColor;
            game.endGame(game.getTeamGamePlayer(tc == TeamColor.RED ? TeamColor.BLUE : TeamColor.RED).getPlayer(), GameEndCause.DISCONNECT);
        }
    }

    @EventHandler
    void onPortalEnter(final PlayerPortalEvent event) {
        event.setCancelled(true);
        event.getPlayer().sendMessage("Entered Portal");
        if (game.hasStarted()) {
            if (goalOpen) {
                goalOpen = false;
                game.newRound();
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title @a times 0 60 0");
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title @a title [{\"text\":\"" + event.getPlayer().getName() + " Scored\",\"bold\":false,\"color\":\"" + game.getGamePlayer(event.getPlayer()).teamColor.getColor() + "\"}]");
                game.getGamePlayer(event.getPlayer()).goals++;

                if (game.getGamePlayer(event.getPlayer()).goals >= 5) {
                    game.endGame(event.getPlayer(), GameEndCause.FIVE_GOALS);
                }
                new BukkitRunnable() {
                    public void run() {
                        goalOpen = true;
                    }
                }.runTaskLater(instance, 20L);
            }
        }
    }

    @EventHandler
    void onPlayerFallInVoid(final PlayerMoveEvent event) {
        if (event.getPlayer().getLocation().getY() < 75 && !event.getPlayer().getLocation().getWorld().getName().equalsIgnoreCase("lobby_the_end")) {
            if (!game.hasStarted())
                event.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 36.5d, 100.0d, 0.5d, 90.0f, 0.0f));
            else {
                // Player Death
                game.getGamePlayer(event.getPlayer()).resetInventory();
            }
        }
    }

    @EventHandler
    void onWeatherChange(final WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDeath(final PlayerDeathEvent e) {
        Player p = e.getEntity();
        p.setHealth(p.getMaxHealth());
        p.setVelocity(new Vector());
        if (!game.hasStarted())
            p.teleport(new Location(Bukkit.getWorld("world"), 36.5d, 100d, 0.5d, 90f, 0f)); // Change
        else
            game.getGamePlayer(p).resetInventory();
    }

    @EventHandler
    public void onKill(final EntityDamageByEntityEvent e) {
        Player p = (Player) e.getDamager();
        if (( (Player)e.getEntity()).getHealth() - e.getFinalDamage() <= 0)
            p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 1.0f, 1.0f);
    }

    @EventHandler
    public void onFallDamage(final EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTNTPrime(final ExplosionPrimeEvent event) {
        event.setRadius(50);
    }

    @EventHandler
    public void onGappleEat(final PlayerItemConsumeEvent e) {
        if (e.getItem().getType() == Material.GOLDEN_APPLE) {
            e.getPlayer().removePotionEffect(PotionEffectType.REGENERATION);
            e.getPlayer().setHealth(20);
        }
    }

    @EventHandler
    public void onFoodChange(final FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        Block block = event.getBlock();
        if (instance.protectedBlocks.contains(block) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You can not break that block.");
        }
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        Block b = event.getBlockPlaced();
        if (b.getY() < 80 || b.getY() > 100 || b.getX() < -30 || b.getX() > 30 || b.getZ() < -20 || b.getZ() > 20) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            } else
                instance.protectedBlocks.add(b);
        }
    }

    @EventHandler
    public void onDropItem(final PlayerDropItemEvent e){
        e.setCancelled(true);
    }
}
