package com.adrianwowk.bridgecore;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;

public enum TeamColor {
    RED("red", Color.fromRGB(255, 0,0), new Location(Bukkit.getWorld("world"), 36.5d, 100d, 0.5d, 90f, 0f)),
    BLUE("blue", Color.fromRGB(0,0,255), new Location(Bukkit.getWorld("world"), -36.5d, 100d, 0.5d, -90f, 0f));

    private final String color;
    private final Color c;
    private Location spawn;

    TeamColor(String color, Color c, Location spawn){
        this.color = color;
        this.c = c;
        this.spawn = spawn;
    }

    public Location getSpawnLocation(){
        return this.spawn;
    }

    public String getColor() {
        return color;
    }

    public Color getRGBColor(){
        return c;
    }

    @Override
    public String toString() {
        return "TeamColor{" +
                "color='" + color + '\'' +
                '}';
    }
}
