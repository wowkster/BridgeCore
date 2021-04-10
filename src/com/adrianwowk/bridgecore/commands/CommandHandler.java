package com.adrianwowk.bridgecore.commands;

import com.adrianwowk.bridgecore.SpigotPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public class CommandHandler implements CommandExecutor {
    private final SpigotPlugin plugin;

    public CommandHandler(SpigotPlugin plugin){
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if (cmd.getName().equalsIgnoreCase("map")) {

            // Check if the sender has the permission and then execute the command specific method
            if (sender.hasPermission("map"))
                playerMapCommand(sender, cmd, args);

        } else if (cmd.getName().equalsIgnoreCase("world")){
            if (sender.hasPermission("map"))
                playerWorldCommand(sender, cmd, args);
        } else if (cmd.getName().equalsIgnoreCase("nuke")){
            if (sender.hasPermission("map"))
                playerNukeCommand(sender, cmd, args);
        } else if (cmd.getName().equalsIgnoreCase("hotbar")){
            playerHotbarCommand(sender, cmd, args);
        }else if (cmd.getName().equalsIgnoreCase("blockgoal")){
            playerBlockGoalCommand(sender, cmd, args);
        }

        return true;
    }

    public boolean playerBlockGoalCommand(final CommandSender sender, final Command cmd, final String[] args) {

        if (args.length < 1){
            sender.sendMessage("Specify the Team Name");
        } else if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("red")){
                World w = Bukkit.getWorld("world");
                for (int x = 35; x <= 39; x++) {
                    for (int z = -2; z <= 2; z++) {
                        Block b = w.getBlockAt(x, 92, z);
                        if (b.getType() == Material.AIR) {
                            b.setType(Material.STAINED_GLASS,true);
                            b.setData(DyeColor.RED.getData());
                            plugin.protectedBlocks.add(b);
                        }

                    }
                }
            }
            else if (args[0].equalsIgnoreCase("blue")){
                World w = Bukkit.getWorld("world");
                for (int x = -39; x <= -35; x++) {
                    for (int z = -2; z <= 2; z++) {
                        Block b = w.getBlockAt(x, 92, z);
                        if (b.getType() == Material.AIR) {
                            b.setType(Material.STAINED_GLASS,true);
                            b.setData(DyeColor.BLUE.getData());
                            plugin.protectedBlocks.add(b);
                        }

                    }
                }
            }
            else
                sender.sendMessage("Unknown TEAM");
        }
        return true;
    }

    public boolean playerNukeCommand(final CommandSender sender, final Command cmd, final String[] args){
        if (!(sender instanceof Player)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You must be a player to utilize these commands!");
            return false;
        }

        Player p = (Player) sender;
        World world = p.getLocation().getWorld();
        for (int i = 0; i < 100; i ++){
            TNTPrimed tnt = (TNTPrimed) world.spawnEntity(p.getLocation(), EntityType.PRIMED_TNT);
            tnt.setFuseTicks((int)(Math.random() * 20 + 100));
            Vector vector = Vector.getRandom();
            vector.setY(Math.abs(vector.getY()));
            vector.setX(vector.getX() * 0.05);
            vector.setY(vector.getY() * 0);
            vector.setZ(vector.getZ() * 0.05);
            tnt.setVelocity(vector.multiply(1));

        }

        return true;
    }

    public boolean playerHotbarCommand(final CommandSender sender, final Command cmd, final String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You must be a player to utilize these commands!");
            return false;
        }
        sender.sendMessage(ChatColor.GREEN + "This Command will be used to edit your inventory layout!");
        return true;
    }

    public boolean playerWorldCommand(final CommandSender sender, final Command cmd, final String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You must be a player to utilize these commands!");
            return false;
        }
        if (args.length < 1){
            sender.sendMessage(Bukkit.getWorlds().toString());
            sender.sendMessage(((Player)sender).getLocation().getWorld().getName());
        } else if (args.length >= 1) {
            if (Bukkit.getWorld(args[0]) != null)
                ((Player)sender).teleport(new Location(Bukkit.getWorld(args[0]),36.5d, 100.0d, 0.5d, 90.0f, 0.0f));
        }
        return true;
    }

    public boolean playerMapCommand(final CommandSender sender, final Command cmd, final String[] args) {

        // Add different options depending on the number of arguments
        if (args.length < 1){
            sender.sendMessage(SpigotPlugin.getPrefix() + "You executed a command with no arguments");
        } else if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reset")){
                MapReset.reset("bridge", sender, true);
            } else if (args[0].equalsIgnoreCase("save")){
                MapReset.save("bridge", sender, true);
            }
        }

        return false;
    }
}
