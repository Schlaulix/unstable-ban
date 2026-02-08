package at.blaulix.unstableBan;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.UUID;

public class BanCountdown {

    private final BossBarManager bossBarManager;
    private final UnstableBan plugin;
    private long timeLeftSave;
    String configTitle;


    public BanCountdown(BossBarManager bossBarManager, UnstableBan plugin) {
        this.bossBarManager = bossBarManager;
        this.plugin = plugin;

        configTitle = plugin.getConfig().getString("countdown-bossbar-title");
        if (configTitle == null || configTitle.isEmpty()) {
            configTitle = "§cUnstable Ban §7(You will lose your ban in %time%)";
        }
    }

    public void toggleVisibility(Player player) {
        bossBarManager.toggleVisibility(player);
    }

    public boolean isActive(Player player) {
        return bossBarManager.isActive(player);
    }

    public void startBanCountdown(Player player, int banLoseAfter) {

        String title = "§cUnstable Ban §7(You will lose your ban in " + bossBarManager.formatTime(banLoseAfter) + ")";
        bossBarManager.createBossBar(player, title);

        int configTime = TimeFormatter.formatToSeconds(Objects.requireNonNull(plugin.getConfig().getString("lose-ban-after-duration")));
        new BukkitRunnable() {

            long timeLeft = banLoseAfter;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    bossBarManager.removeBossBar(player);
                    cancel();
                    return;
                }
                if (timeLeft <= 0) {
                    bossBarManager.removeBossBar(player);

                    UUID uuid = player.getUniqueId();

                    plugin.reduceBanCount(uuid);

                    int newBanCount = plugin.banCount(plugin.getBanConfig(), uuid);

                    if (newBanCount > 0) {
                        int seconds = TimeFormatter.formatToSeconds(Objects.requireNonNull(plugin.getConfig().getString("lose-ban-after-duration")));
                        startBanCountdown(player, seconds);
                    }

                    cancel();
                    return;
                }

                double progress = (double) timeLeft / configTime;


                bossBarManager.update(player, configTitle.replace("%time%", bossBarManager.formatTime(timeLeft)), progress);

                timeLeft--;
                timeLeftSave = timeLeft;
            }

        }.runTaskTimer(plugin, 20L, 20L);
    }

    public void saveSecondsLeft(Player player, FileConfiguration banConfig) {
        bossBarManager.saveSecondsLeft(player, timeLeftSave, banConfig);
    }
}
