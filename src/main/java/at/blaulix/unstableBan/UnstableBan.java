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
import java.util.UUID;

public final class UnstableBan extends JavaPlugin implements Listener, SaveReadMethods {
    private FileConfiguration banConfig;
    File banFile = new File(getDataFolder(), "bans.yml");

    @Override
    public void onEnable() {
        saveDefaultConfig();


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
        try{
            savebansfile(banFile, banConfig, this);
            getLogger().info("UnstableBan saved config and disabled (v"+ getPluginMeta().getVersion() +")");
        } catch (Exception e) {
            getLogger().info("ERROR while saving config on disable!");
        }
    }

    @EventHandler
    public void onFirstJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        int banTimesLength = getConfig().getStringList("ban-times").size();

        if (!player.hasPlayedBefore() || banCount(banConfig, uuid) == banTimesLength){

            banConfig.set("bans." + uuid + ".banCount", 0);
            savebansfile(banFile, banConfig, this);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getPlayer();
        Entity killer = player.getKiller();
        if (killer != null){
            UUID uuid = player.getUniqueId();
            int currentBanCount = banCount(banConfig, uuid);
            String banTime = getConfig().getStringList("ban-times").get(currentBanCount);
            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(), "ban" + uuid + " " + banTime + " You got killed banned by " + killer.getName()
            );
            banConfig.set("bans." + uuid + ".banCount", currentBanCount + 1);
            savebansfile(banFile, banConfig, this);
        }
    }
}
