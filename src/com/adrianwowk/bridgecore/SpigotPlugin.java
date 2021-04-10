package com.adrianwowk.bridgecore;

import com.adrianwowk.bridgecore.commands.CommandHandler;
import com.adrianwowk.bridgecore.commands.MapReset;
import com.adrianwowk.bridgecore.commands.SPTabCompleter;
import com.adrianwowk.bridgecore.events.SpigotPluginEvents;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class SpigotPlugin extends JavaPlugin {
    Server server;
    ConsoleCommandSender console;
    Game game;

    public ArrayList<Block> protectedBlocks = new ArrayList<>();

    public SpigotPlugin() {
        this.server = Bukkit.getServer();
        this.console = this.server.getConsoleSender();
    }

    public void onEnable() {
        System.out.println("=======================================================");
        getServer().createWorld(new WorldCreator("world"));
        System.out.println("=======================================================");

        this.game = new Game(this);

        new BukkitRunnable() {
            public void run() {
                World w = Bukkit.getWorld("world");
                Block b;

                for (int x = -50; x < 50; x++) {
                    for (int y = 85; y < 110; y++) {
                        for (int z = -20; z < 20; z++) {
                            b = w.getBlockAt(x, y, z);
                            if (b.getType() != Material.AIR) {
                                protectedBlocks.add(b);
                            }
                        }
                    }
                }
            }
        }.runTaskLater(this, 20L);

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "" + protectedBlocks.size() + " BLOCKS ADDED TO PROTECTED BLOCKS LIST");

        CommandHandler ch = new CommandHandler(this);

        // Register command tab completer and executer
        getCommand("map").setExecutor(ch);
        getCommand("world").setExecutor(ch);
        getCommand("nuke").setExecutor(ch);
        getCommand("hotbar").setExecutor(ch);
        getCommand("blockgoal").setExecutor(ch);
        // Register Event Listeners
        Bukkit.getServer().getPluginManager().registerEvents(new SpigotPluginEvents(this), this);

        new BukkitRunnable() {
            public void run() {
                MapReset.reset("", Bukkit.getConsoleSender(), false);
            }
        }.runTaskLater(this, 0L);
        // Server Console Message
        Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "=================================");
        Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "          [BridgeCore]           ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "  Has been successfully enabled! ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "     Author - Adrian Wowk        ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "=================================");
    }

    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + ChatColor.YELLOW + "Plugin Successfully Disabled");
    }

    public static String getPrefix() {
        return ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + "BridgeCore" + ChatColor.GRAY + "] " + ChatColor.YELLOW;
    }

    public Game getGame() {
        return game;
    }
}
