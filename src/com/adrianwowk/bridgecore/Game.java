package com.adrianwowk.bridgecore;

import com.adrianwowk.bridgecore.commands.MapReset;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Collection;

public class Game {
    private ArrayList<GamePlayer> players;
    boolean started;
    int playerCount;
    boolean counting;
    Plugin instance;
    boolean ended = false;

    public Game(Plugin plugin) {
        new BukkitRunnable() {
            public void run() {
                updateScoreBoards();
                playerCount = Bukkit.getWorld("world").getPlayers().size();
                if (playerCount == 2 && !counting && !started && !ended) {
                    initiateGame();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
        this.instance = plugin;
        if (playerCount == 2) {
            initiateGame();
        }
    }

    public void startGame(ArrayList<GamePlayer> playas) {
        this.players = playas;
        this.started = true;

        // Game Start Code i.e. Players get gear and put in cages, survival mode,
        World w = Bukkit.getWorld("world");
        for (int x = 35; x <= 39; x++) {
            for (int z = -2; z <= 2; z++) {
                Block b = w.getBlockAt(x, 90, z);
                if (b.getType() == Material.AIR) {
                    b.setType(Material.ENDER_PORTAL);
                }

            }
        }

        for (int x = -39; x <= -35; x++) {
            for (int z = -2; z <= 2; z++) {
                Block b = w.getBlockAt(x, 90, z);
                if (b.getType() == Material.AIR) {
                    b.setType(Material.ENDER_PORTAL);
                }

            }
        }
        Bukkit.broadcastMessage("Game Started!");
        newRound();
    }

    public void newRound() {
        for (GamePlayer gp : players) {
            gp.resetInventory();
            gp.getPlayer().setGameMode(GameMode.SURVIVAL);
        }
    }

    public void startGame(Player player1, TeamColor team1, Player player2, TeamColor team2) {
        GamePlayer gp1 = new GamePlayer(player1.getName(), team1);
        GamePlayer gp2 = new GamePlayer(player2.getName(), team2);
        ArrayList<GamePlayer> playas = new ArrayList<>();
        playas.add(gp1);
        playas.add(gp2);
        this.startGame(playas);
    }

    public void startGame(Player player1, TeamColor teamColor, Player player2) {
        this.startGame(player1, teamColor, player2, (teamColor == TeamColor.BLUE ? TeamColor.RED : TeamColor.BLUE));
    }


    public void startGame(Player player1, Player player2) {
        int random = (int) (Math.random() * 2);
        this.startGame(player1, (random == 0 ? TeamColor.RED : TeamColor.BLUE),
                player2, (random == 1 ? TeamColor.RED : TeamColor.BLUE));
    }

    public void endGame(Player victor, GameEndCause cause) {
        // Fire Works, Titles, etc.
//        started = false;
        counting = false;

        cause.runEnding(victor, this);


        World w = Bukkit.getWorld("world");
        for (int x = 35; x <= 39; x++) {
            for (int z = -2; z <= 2; z++) {
                Block b = w.getBlockAt(x, 90, z);
                if (b.getType() == Material.ENDER_PORTAL) {
                    b.setType(Material.AIR);
                }

            }
        }

        for (int x = -39; x <= -35; x++) {
            for (int z = -2; z <= 2; z++) {
                Block b = w.getBlockAt(x, 90, z);
                if (b.getType() == Material.ENDER_PORTAL) {
                    b.setType(Material.AIR);
                }

            }
        }

        ended = true;

        new BukkitRunnable() {
            public void run() {
                MapReset.reset("", Bukkit.getConsoleSender(), false);
                ended = false;
                players = null;
                started = false;
            }
        }.runTaskLater(instance, 200L);

    }

    private void updateScoreBoards() {

        for (Player player : Bukkit.getOnlinePlayers()) {


            Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

            Objective health = board.registerNewObjective("Health", "dummy");
            health.setDisplaySlot(DisplaySlot.BELOW_NAME);
            health.setDisplayName(ChatColor.RED + "❤");

            for (Player online : Bukkit.getOnlinePlayers()) {
                Score score = health.getScore(online);
                score.setScore((int) online.getHealth()); //Example
            }

            Objective obj = board.registerNewObjective(ChatColor.WHITE + "" + ChatColor.BOLD + "BRIDGE DUEL", "dummy");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);

            obj.getScore(ChatColor.GRAY + "01/07/21 " + ChatColor.DARK_GRAY + "m001").setScore(15);

            obj.getScore(ChatColor.GRAY + "").setScore(14);

            //------------------------------------------------

            if (started) {
                // Normal In Game Scoreboard
                obj.getScore("Time Left: " + ChatColor.GREEN + "00:00").setScore(13);
                obj.getScore(ChatColor.GRAY + " ").setScore(12);

                GamePlayer redGP = getTeamGamePlayer(TeamColor.RED);
                GamePlayer blueGP = getTeamGamePlayer(TeamColor.BLUE);

                if (blueGP != null) {
                    int goals = blueGP.goals;
                    String finalStr = "";
                    for (int i = 0; i < goals; i++)
                        finalStr += ChatColor.BLUE + "⬤";
                    for (int i = goals; i < 5; i++)
                        finalStr += ChatColor.GRAY + "⬤";

                    obj.getScore(ChatColor.BLUE + "[B]" + finalStr).setScore(11);
                } else
                    obj.getScore(ChatColor.BLUE + "[B]" + ChatColor.GRAY + "⬤⬤⬤⬤⬤").setScore(11);


                if (redGP != null) {
                    int goals = redGP.goals;
                    String finalStr = "";
                    for (int i = 0; i < goals; i++)
                        finalStr += ChatColor.RED + "⬤";
                    for (int i = goals; i < 5; i++)
                        finalStr += ChatColor.GRAY + "⬤";

                    obj.getScore(ChatColor.RED + "[R]" + finalStr).setScore(10);
                } else
                    obj.getScore(ChatColor.RED + "[R]" + ChatColor.GRAY + "⬤⬤⬤⬤⬤").setScore(10);


                obj.getScore(ChatColor.GRAY + "  ").setScore(9);
                obj.getScore("Kills: " + ChatColor.GREEN + getGamePlayer(player).kills).setScore(8);
                obj.getScore("Goals: " + ChatColor.GREEN + getGamePlayer(player).goals).setScore(7);
                obj.getScore("Deaths: " + ChatColor.GREEN + getGamePlayer(player).deaths).setScore(6);
                obj.getScore(ChatColor.GRAY + "   ").setScore(5);
                obj.getScore("Mode: " + ChatColor.GREEN + "The Bridge Ripoff").setScore(4);


            } else {
                obj.getScore("Map: " + ChatColor.GREEN + "Prism").setScore(13);
                obj.getScore("Players: " + ChatColor.GREEN + playerCount + "/2").setScore(12);
                obj.getScore(ChatColor.GRAY + "   ").setScore(11);

                if (counting) {
                    // Starting...
                    obj.getScore(ChatColor.RED + "Starting...").setScore(2); // "Starting... " + ChatColor.GREEN + (5,4,3,2,1)
                } else {
                    // 1/2
                    obj.getScore("Waiting...").setScore(2); // "Starting... " + ChatColor.GREEN + (5,4,3,2,1)
                }
            }

            obj.getScore(ChatColor.GRAY + "        ").setScore(1);
            obj.getScore(ChatColor.YELLOW + "play.adrianwowk.com").setScore(0);

            player.setScoreboard(board);
        }
    }

    public GamePlayer getGamePlayer(Player player) {
        if (players == null)
            return null;
        for (GamePlayer gp : players) {
            if (gp.playerName.equals(player.getName())) {
                return gp;
            }
        }
        return null;
    }

    public GamePlayer getTeamGamePlayer(TeamColor teamColor) {
        if (players == null)
            return null;
        for (GamePlayer gp : players) {
            if (gp.teamColor == teamColor)
                return gp;
        }
        return null;
    }

    public void initiateGame() {
        Bukkit.broadcastMessage("Game Initiated");
        counting = true;

        for (int i = 0; i < 5; i++) {

            int finalI = i;
            new BukkitRunnable() {
                public void run() {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (counting) {
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title @a times 0 20 0");
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title @a title [{\"text\":\"" + (5 - finalI) + "\",\"color\":\"green\"}]");
                            p.playSound(p.getLocation(), Sound.NOTE_STICKS, 1.0f, 1.0f);
                        }
                        ItemStack empty = new ItemStack(Material.AIR);
                        p.getInventory().setChestplate(empty);
                        p.getInventory().setLeggings(empty);
                        p.getInventory().setBoots(empty);
                        p.getInventory().clear();
                        p.setGameMode(GameMode.ADVENTURE);
                    }
                }
            }.runTaskLater(instance, i * 20L);
        }

        new BukkitRunnable() {
            public void run() {
                if (counting) {
                    ArrayList<Player> ps = new ArrayList<>(Bukkit.getOnlinePlayers());
                    Player p1 = ps.get(0);
                    Player p2 = ps.get(1);
                    startGame(p1, p2);
                }
            }
        }.runTaskLater(instance, 5 * 20L);

    }

    public void cancelGame() {
//        Bukkit.broadcastMessage("Game Cancelled");
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title @a title [{\"text\":\"GAME CANCELED\",\"color\":\"red\"}]");
        counting = false;
    }

    public boolean hasStartedCounting() {
        return counting;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public void addPlayer() {
        this.playerCount++;
    }

    public void removePlayer() {
        this.playerCount--;
    }

    public boolean hasStarted() {
        return this.started;
    }

    public boolean playerIsOnTeam(Player p) {
        if (players == null)
            return false;
        return (players.get(0).playerName.equals(p.getName()) || players.get(1).playerName.equals(p.getName()));
    }

    public ArrayList<GamePlayer> getPlayers() {
        return players;
    }

    public boolean hasEnded() {
        return ended;
    }
}
