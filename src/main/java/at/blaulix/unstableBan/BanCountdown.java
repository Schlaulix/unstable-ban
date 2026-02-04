package at.blaulix.unstableBan;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BanCountdown {
    public void startBanCountdown(Player player, int banLoseAfter, JavaPlugin plugin) {

        int playtimeTicks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        int playtimeSeconds = playtimeTicks / 20;
        int countdownSeconds = playtimeSeconds + banLoseAfter;

        new BukkitRunnable() {
            int timeLeft = countdownSeconds;

            @Override
            public void run() {
                if (timeLeft <= 0 || !player.isOnline()) {
                    cancel();
                    return;
                }


                BossBarManager bossBarManager = new BossBarManager((JavaPlugin) Bukkit.getPluginManager().getPlugin("UnstableBan"));
                String title = "§cUnstable Ban §7(You will lose your ban in " + bossBarManager.formatTime(timeLeft) + ")";

                bossBarManager.createTimedBossBar(player, countdownSeconds, title);


                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L); // 20 Ticks = 1 Sekunde
    }
}
