package at.blaulix.unstableBan;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.UUID;

public interface saveReadMethods {
    default void savebansfile(File banFile, FileConfiguration banConfig) {
        try {
            banConfig.save(banFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    default int banCount(FileConfiguration banConfig, UUID uuid) {
        return banConfig.getInt("bans." + uuid + ".banCount");
    }
}
