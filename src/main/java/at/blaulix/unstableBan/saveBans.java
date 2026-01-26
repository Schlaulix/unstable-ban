package at.blaulix.unstableBan;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public interface saveBans {
    default void savebansfile(File banFile, FileConfiguration banConfig) {
        try {
            banConfig.save(banFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
