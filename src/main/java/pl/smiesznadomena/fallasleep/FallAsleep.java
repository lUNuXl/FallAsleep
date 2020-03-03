package pl.smiesznadomena.fallasleep;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class FallAsleep extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this,this);
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onBedEnter(@NotNull PlayerBedEnterEvent event) {
        if(event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK)
            return;

        final World targetWorld = event.getPlayer().getWorld();
        final List<Player> playerList = targetWorld.getPlayers();

        final int playersOnWorld = playerList.size();
        if(playersOnWorld == 1)
            return;

        final int playersAsleep = countPlayersAsleep(playerList) + 1;
        int needed = playersOnWorld / 3;
        if(needed == 0) {
            needed++;
        }
        Bukkit.broadcastMessage(ChatColor.YELLOW + event.getPlayer().getDisplayName() + " is now sleeping (" + playersAsleep + "/" + needed + ")");

        if(playersAsleep >= needed) {
            BukkitRunnable bukkitRunnable = new BukkitRunnable() {
                @Override
                public void run() {
                    final List<Player> players = targetWorld.getPlayers();
                    final int playersOnWorld = players.size();
                    final int playersAsleep = countPlayersAsleep(players);

                    if(playersAsleep != 0 && playersAsleep >= playersOnWorld / 3)
                        setDay(targetWorld);
                }
            };
            bukkitRunnable.runTaskLater(this, 102L);
        }

    }

    @EventHandler
    public void onBedLeave(@NotNull PlayerBedLeaveEvent event) {
        Bukkit.broadcastMessage(ChatColor.YELLOW + event.getPlayer().getDisplayName() + " is no longer sleeping");
        if(event.getPlayer().getUniqueId().toString().equals("0d2d13f4-9f4a-45cd-95b3-c7dffe38408c")) // ravnous UUID
            Bukkit.getServer().broadcastMessage(ChatColor.AQUA + event.getPlayer().getDisplayName() + " doesn't know how to use beds, maybe some pills will help");
        event.getPlayer().sendMessage(ChatColor.RED + "You're supposed to fall asleep in the bed");
    }

    private int countPlayersAsleep(@NotNull List<Player> playerList) {
        int sleeping = 0;
        for(Player p : playerList) {
            if(p.isSleeping())
                sleeping++;
        }
        return sleeping;
    }

    private void setDay(@NotNull World world) {
        world.setTime(0);
        world.setThundering(false);
        world.setStorm(false);
    }

}
