package at.blaulix.unstableBan;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class UnstableBan extends JavaPlugin implements Listener, saveBans {
    private File banFile;
    private FileConfiguration banConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        File banFile = new File(getDataFolder(), "bans.yaml");
        if (!banFile.exists()){
            saveResource("bans.yaml", false);
        }

        banConfig = YamlConfiguration.loadConfiguration(banFile);

        getLogger().info("UnstableBan enabled (v"+ getPluginMeta().getVersion() +")");
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onFirstJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore() || banConfig.getInt("bans." + player.getUniqueId().toString() + ".banCount"){
            String uuid = player.getUniqueId().toString();

            banConfig.set("bans." + uuid + ".banCount", 0);
            savebansfile(banFile, banConfig);
        }
    }

    public void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getPlayer();
        Entity killer = player.getKiller();
        if (killer instanceof Player){
            Bukkit.getConsoleSender();
        }
    }
}
