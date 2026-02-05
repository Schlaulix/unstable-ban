package at.blaulix.unstableBan;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BanCountdown {

    private final BossBarManager bossBarManager;
    private long timeLeftSave;

    public BanCountdown(BossBarManager bossBarManager) {
        this.bossBarManager = bossBarManager;
    }

    public void toggleVisibility(Player player) {
        bossBarManager.toggleVisibility(player);
    }

    public boolean isActive(Player player) {
        return bossBarManager.isActive(player);
    }

    public void startBanCountdown(Player player, int banLoseAfter, JavaPlugin plugin) {

        BossBarManager bossBarManager = new BossBarManager();
        String title = "§cUnstable Ban §7(You will lose your ban in " + bossBarManager.formatTime(banLoseAfter) + ")";
        bossBarManager.createBossBar(player, title);
        new BukkitRunnable() {

            long timeLeft = banLoseAfter;

            @Override
            public void run() {
                if (!player.isOnline() || timeLeft <= 0) {
                    bossBarManager.removeBossBar(player);
                    cancel();
                    return;
                }

                double progress = (double) timeLeft;

                bossBarManager.update(player, "§cUnstable Ban §7(You will lose your ban in " + bossBarManager.formatTime(timeLeft) + ")", progress);

                timeLeft--;
                timeLeftSave = timeLeft;
            }

        }.runTaskTimer(plugin, 20L, 20L);
    }

    public void saveSecondsLeft(Player player, FileConfiguration banConfig) {
        bossBarManager.saveSecondsLeft(player, timeLeftSave, banConfig);
    }
}
