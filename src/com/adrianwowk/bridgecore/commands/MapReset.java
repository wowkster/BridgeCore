package com.adrianwowk.bridgecore.commands;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

public class MapReset {

    private static String working = "C:\\Users\\adyow\\Desktop\\Minecraft\\Bungee\\Bridge Server 1\\world";
    private static String orig = "C:\\Users\\adyow\\Desktop\\Minecraft\\Bungee\\Bridge Server 1\\original_map";

    public static void reset(String str, CommandSender sender, boolean restart) {
        manipulate(orig, working, true, sender, restart);
    }

    public static void save(String map, CommandSender sender, boolean restart) {
        manipulate(working, orig, true, sender, restart);
    }

    private static void manipulate(String src, String dest, boolean save, CommandSender sender, boolean restart) {
        sendMessage(sender, ChatColor.AQUA + (save ? "Saving Map..." : "Resetting Map..."));
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.getInventory().clear();
            p.teleport(new Location(Bukkit.getWorld("lobby"), 36.5d, 100, 0.5d, 90f, 0f));
        }
        if (Bukkit.getWorld("world") != null)
            sendMessage(sender, ChatColor.GREEN + "'world' IS NOT NULL");
        else
            sendMessage(sender, ChatColor.RED + "'world' IS NULL");
        if (Bukkit.unloadWorld("world", true))
            sendMessage(sender, ChatColor.GREEN + "SUCCESSFULLY UNLOADED WORLD 'world'");
        else {
            sendMessage(sender, ChatColor.RED + "COULD NOT UNLOAD WORLD 'world'");
            return;
        }
        sendMessage(sender, ChatColor.BLUE + "Unloaded Worlds...");
        File dir = new File(dest);
        removeDirectory(dir);
        sendMessage(sender, ChatColor.RED + "Deleted Old Files...");
        try {
            Path src2 = Paths.get(src); // "C:\\Users\\adyow\\Desktop\\Minecraft\\Bungee\\Bridge Server 1\\world");
            Path dest2 = Paths.get(dest); // "C:\\Users\\adyow\\Desktop\\Minecraft\\Bungee\\Bridge Server 1\\original_map");
            Stream<Path> files = Files.walk(src2);
            files.forEach(file -> {
                try {
                    Files.copy(file, dest2.resolve(src2.relativize(file)),
                            StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            files.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        sendMessage(sender, ChatColor.YELLOW + "Coppied Schematic To Destination...");
        Bukkit.getServer().createWorld(new WorldCreator("world"));
        sendMessage(sender, ChatColor.GREEN + "Loaded New World...");
        if (restart) {
            sendMessage(sender, ChatColor.GREEN + "Reloading Server...");
            Bukkit.getServer().reload();
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.teleport(new Location(Bukkit.getWorld("world"), 36.5d, 100.0d, 0.5d, 90.0f, 0.0f));
        }
    }

    private static void sendMessage(CommandSender sender, String message) {
        Bukkit.getConsoleSender().sendMessage(message);
        sender.sendMessage(message);
    }

    private static void removeDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (File aFile : files) {
                    removeDirectory(aFile);
                }
            }
            dir.delete();
        } else {
            dir.delete();
        }
    }
}