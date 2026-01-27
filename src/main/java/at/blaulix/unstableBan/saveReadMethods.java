package at.blaulix.unstableBan;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.UUID;

import static sun.font.FontUtilities.getLogger;

public interface saveReadMethods {
    default void savebansfile(File banFile, FileConfiguration banConfig) {
        try {
            banConfig.save(banFile);
        } catch (Exception e) {
            getLogger().info("ERROR while saving config!");
        }
    }

    default int banCount(FileConfiguration banConfig, UUID uuid) {
        return banConfig.getInt("bans." + uuid + ".banCount");
    }
}
