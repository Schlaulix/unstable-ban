package at.blaulix.unstableBan;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class BossBarManager {

    private final JavaPlugin plugin;

    public BossBarManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void createTimedBossBar(Player player, long durationSeconds, String title) {

        BossBar bossBar = Bukkit.createBossBar(
                title,
                BarColor.RED,
                BarStyle.SOLID
        );

        bossBar.addPlayer(player);
        bossBar.setProgress(1.0);

        new BukkitRunnable() {

            long timeLeft = durationSeconds;

            @Override
            public void run() {
                if (timeLeft <= 0 || !player.isOnline()) {
                    bossBar.removeAll();
                    cancel();
                    return;
                }

                double progress = (double) timeLeft / durationSeconds;
                bossBar.setProgress(progress);

                bossBar.setTitle(title + " §7(" + formatTime(timeLeft) + ")");

                timeLeft--;
            }

        }.runTaskTimer(plugin, 0L, 20L); // 20 Ticks = 1 Sekunde
    }

    private String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        if (minutes > 0) {
            return minutes + "m " + secs + "s";
        }
        return secs + "s";
    }
}

