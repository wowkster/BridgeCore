package com.adrianwowk.bridgecore;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum GameEndCause {
    DISCONNECT("disconnect", "Opponent Disconnected"),
    FIVE_GOALS("win", "Victor was first to reach 5 goals");

    private final String desc;
    private final String name;

    GameEndCause(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getDescription() {
        return desc;
    }

    public void runEnding(Player victor, Game game) {

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title @a times 0 60 0");
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + victor.getName() + " title [{\"text\":\"VICTORY\",\"bold\":true,\"color\":\"gold\"}]");

        victor.teleport(game.getGamePlayer(victor).teamColor.getSpawnLocation());
        ItemStack empty = new ItemStack(Material.AIR);
        victor.getInventory().setChestplate(empty);
        victor.getInventory().setLeggings(empty);
        victor.getInventory().setBoots(empty);
        victor.getInventory().clear();
        victor.setGameMode(GameMode.ADVENTURE);

        if (this.name.equalsIgnoreCase("disconnect")) {


        } else if (this.name.equalsIgnoreCase("win")) {

            Player loser;

            if (game.getGamePlayer(victor).teamColor == TeamColor.RED) {
                loser = game.getTeamGamePlayer(TeamColor.BLUE).getPlayer();
            } else {
                loser = game.getTeamGamePlayer(TeamColor.RED).getPlayer();
            }

            loser.teleport(game.getGamePlayer(loser).teamColor.getSpawnLocation());
            loser.getInventory().setChestplate(empty);
            loser.getInventory().setLeggings(empty);
            loser.getInventory().setBoots(empty);
            loser.getInventory().clear();
            loser.setGameMode(GameMode.ADVENTURE);

            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + loser.getName() + " title [{\"text\":\"GAME OVER\",\"bold\":true,\"color\":\"red\"}]");
        }
    }

    @Override
    public String toString() {
        return "GameEndCause{" +
                "desc='" + desc + '\'' +
                '}';
    }
}
