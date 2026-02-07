package at.blaulix.unstableBan;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class BossBarManager {
    private final Map<Player, BossBar> bars = new HashMap<>();
    private final BarColor bossBarColor;

    public BossBarManager(UnstableBan plugin) {

        String colorName = Objects.requireNonNull(plugin.getConfig().getString("countdown-bossbar-color")).toUpperCase();
        BarColor color;
        try {
            color = BarColor.valueOf(colorName);
        } catch (IllegalArgumentException e) {
            color = BarColor.RED;
        }

        this.bossBarColor = color;
    }

    public void createBossBar(Player player, String title) {
        BossBar bossBar = Bukkit.createBossBar(title, bossBarColor, BarStyle.SOLID);
        bossBar.setProgress(1.0);
        bossBar.addPlayer(player);
        bars.put(player, bossBar);
    }

    public void update(Player player, String title, double progress) {
        BossBar bossBar = bars.get(player);
        if (bossBar == null) return;

        bossBar.setTitle(title);
        bossBar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
    }

    public void removeBossBar(Player player) {
        BossBar bossBar = bars.remove(player);
        if (bossBar != null) {
            bossBar.removeAll();
        }
    }

    public void toggleVisibility(Player player) {
        BossBar bossBar = bars.get(player);
        if (bossBar != null) {
            bossBar.setVisible(!bossBar.isVisible());
        }
    }

    public boolean isActive(Player player) {
        return bars.containsKey(player);
    }

    public void saveSecondsLeft(Player player, long seconds, FileConfiguration banConfig) {

        UUID uuid = player.getUniqueId();
        banConfig.set("bans." + uuid + ".timeLeft", seconds);
    }


    public String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (hours > 0) return hours + "h " + minutes + "m";
        if (minutes > 0) return minutes + "m " + secs + "s";
        return secs + "s";
    }
}
