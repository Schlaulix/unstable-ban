package at.blaulix.unstableBan;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.UUID;
import java.util.logging.Level;


public interface SaveReadMethods {
    default void saveBansFile(File banFile, FileConfiguration banConfig, JavaPlugin plugin) {
        try {
            banConfig.save(banFile);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "ERROR while saving bans config!", e);
        }
    }

    default int banCount(FileConfiguration banConfig, UUID uuid) {
        return banConfig.getInt("bans." + uuid + ".banCount");
    }

    /*default void playTimeatBan(FileConfiguration banConfig, UUID uuid, long playTimeAtBan) {
        banConfig.set("bans." + uuid + ".playTimeAtBan", playTimeAtBan);
    }*/
}
