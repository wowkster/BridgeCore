package com.adrianwowk.bridgecore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class SPTabCompleter implements TabCompleter {

    private List<String> list = new ArrayList<>(); // Same list object is used for each command call

    @Override // Main method that is called when a tab complete is attempted
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

        list.clear(); // Empty the list

        // Check if the command entered matches the string
        if (cmd.getName().equalsIgnoreCase("command")) {

            // Check if the sender has the permission and then execute the command specific method
            if (sender.hasPermission("spigotplugin.command"))
                commandCommand(sender, args);

        }

        // Return the final list of options
        return list;
    }

    // Command specific method
    private void commandCommand(CommandSender sender, String[] args){

        // Add different options depending on the number of arguments
        if (args.length < 1){
            list.add("argument-1");
        } else if (args.length <= 2) {
            list.add("argument-2");
        }
    }
}
