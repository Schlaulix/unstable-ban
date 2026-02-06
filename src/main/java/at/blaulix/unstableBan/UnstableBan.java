package at.blaulix.unstableBan;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

public final class UnstableBan extends JavaPlugin implements Listener, SaveReadMethods {
    private FileConfiguration banConfig;
    private File banFile;
    private BanCountdown banCountdown;

    @Override
    public void onEnable() {
        banFile = new File(getDataFolder(), "bans.yml");
        saveDefaultConfig();


        if (!banFile.exists()) {
            saveResource("bans.yml", false);
        }

        banConfig = YamlConfiguration.loadConfiguration(banFile);

        getLogger().info("UnstableBan enabled (v" + getPluginMeta().getVersion() + ")");
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(this, this);

        Objects.requireNonNull(getCommand("unstableban")).setTabCompleter(new CommandTabCompleter());

        BossBarManager bossBarManager = new BossBarManager(this);
        banCountdown = new BanCountdown(bossBarManager, this);
    }

    @Override
    public void onDisable() {
        try {
            saveBansFile(banFile, banConfig, this);
            getLogger().info("UnstableBan saved config and disabled (v" + getPluginMeta().getVersion() + ")");
        } catch (Exception e) {
            getLogger().info("ERROR while saving config on disable!");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        boolean timeResetEnabled = getConfig().getBoolean("lose-ban-after-time");

        int banTimesLength = getConfig().getStringList("ban-durations").size();
        String path = "bans." + uuid;
        int playerBanCount = banCount(banConfig, uuid);

        if (!banConfig.contains(path) || playerBanCount >= banTimesLength) {
            banConfig.set("bans." + uuid + ".banCount", 0);
            saveBansFile(banFile, banConfig, this);
        }

        if(timeResetEnabled) {
            if (playerBanCount > 0) {
                long timeLeft = banConfig.getLong(path + ".timeLeft");
                if (timeLeft > 0) {
                    banCountdown.startBanCountdown(player, (int) timeLeft, this);
                } else {
                    String banLoseAfter = getConfig().getString("lose-ban-after-duration");

                    if (banLoseAfter == null || banLoseAfter.isEmpty()) {
                        return;
                    }
                    int banLoseAfterSeconds = TimeFormatter.formatToSeconds(banLoseAfter);

                    banCountdown.startBanCountdown(player, banLoseAfterSeconds, this);
                }
            } else {
                banConfig.set(path + ".timeLeft", 0);
                saveBansFile(banFile, banConfig, this);
            }
        }

    }

    @EventHandler
    public void bans(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        int currentBanCount = banCount(banConfig, uuid);
        String banDuration = getConfig().getStringList("ban-durations").get(currentBanCount);

        Player killer = event.getPlayer().getKiller();
        boolean killResetEnabled = getConfig().getBoolean("lose-ban-after-kill");


        Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(), "ban " + uuid + " " + banDuration + " You got banned!"
        );

        banConfig.set("bans." + uuid + ".banCount", currentBanCount + 1);
        saveBansFile(banFile, banConfig, this);


        if (killResetEnabled) {

            if (killer != null) {
                UUID killerUuid = killer.getUniqueId();
                int killerCurrentBanCount = banCount(banConfig, killerUuid);
                int banAmountLost = getConfig().getInt("lose-ban-amount-on-kill");
                int newBanCount;

                if (killerCurrentBanCount > 0) {
                    if (banAmountLost == -1) {
                        newBanCount = 0;
                    } else if (banAmountLost >= 0) {
                        newBanCount = Math.max(0, killerCurrentBanCount - banAmountLost);
                    } else {
                        return;
                    }
                    banConfig.set("bans." + killerUuid + ".banCount", newBanCount);
                    saveBansFile(banFile, banConfig, this);
                    killer.sendMessage("§aYou have reduced your ban count to " + newBanCount + " by killing " + player.getName() + "!");
                }
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        banCountdown.saveSecondsLeft(player, banConfig);
        saveBansFile(banFile, banConfig, this);

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String @NotNull [] args) {
        if (!command.getName().equalsIgnoreCase("unstableban")) {
            return false;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            if (!sender.hasPermission("unstablenban.help")) {
                sender.sendMessage("§6UnstableBan Help:");
                sender.sendMessage("§eYou can use /ub or /unstableban for the commands.");
                sender.sendMessage("§e/ub bans §7- Look up how many bans you have.");
                return true;
            }
            sender.sendMessage("§6UnstableBan Help:");
            sender.sendMessage("§e/ub bans §7- Look up how many bans you have.");
            sender.sendMessage("§e/ub reload §7- Reload the plugin configuration.");
            sender.sendMessage("§e/ub help §7- Show this help message.");
            sender.sendMessage("§e/ub getbans <player> §7- Get the ban count of a player.");
            sender.sendMessage("§e/ub setbans <player> <value> §7- Set the ban count of a player.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("bans")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cOnly players can use this command.");
            } else {
                UUID uuid = player.getUniqueId();
                int banCount = banCount(banConfig, uuid);
                player.sendMessage("§aYou have " + banCount + " bans.");
            }
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("unstableban.reload")) {
                sender.sendMessage("§cYou don't have permission to use this command.");
                return true;
            }
            reloadConfig();
            sender.sendMessage("§aReloaded UnstableBan Config.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("togglebossbar")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cOnly players can use this command.");
            } else if (banCountdown.isActive(player)) {
                banCountdown.toggleVisibility(player);
            } else {
                sender.sendMessage("§cYou don't have an active ban countdown.");
            }
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("getbans")) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (!sender.hasPermission("unstableban.getbans")) {
                sender.sendMessage("§cYou don't have permission to use this command.");
                return true;
            } else if (targetPlayer != null) {
                UUID targetUuid = targetPlayer.getUniqueId();
                int banCount = banCount(banConfig, targetUuid);
                sender.sendMessage("§a" + targetPlayer.getName() + " has " + banCount + " bans.");
                return true;

            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("setbans")) {
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (!sender.hasPermission("unstableban.setbans")) {
                sender.sendMessage("§cYou don't have permission to use this command.");
                return true;
            } else if (targetPlayer != null) {
                UUID targetUuid = targetPlayer.getUniqueId();
                int banTimesLength = getConfig().getStringList("ban-durations").size();
                int value = args[2] != null ? Integer.parseInt(args[2]) : 0;

                if (value <= banTimesLength && value >= 0) {
                    banConfig.set("bans." + targetUuid + ".banCount", value);
                    saveBansFile(banFile, banConfig, this);
                    sender.sendMessage("§aSet " + targetPlayer.getName() + "'s ban count to " + value + ".");
                } else {
                    sender.sendMessage("§cThe ban count must be less than " + banTimesLength + " and more than 0.");
                }
                return true;
            }
        }


        sender.sendMessage("§cFor help use /unstableban help");
        return true;
    }

    public void reduceBanCount(UUID uuid) {
        int currentBanCount = banCount(banConfig, uuid);
        int loseBanAmount = getConfig().getInt("lose-ban-amount");
        int banAmount;

        if (currentBanCount > 0) {
            if (loseBanAmount == -1) {
                banAmount = 0;
            }else{
                banAmount = Math.max(0, currentBanCount - loseBanAmount);
            }
            banConfig.set("bans." + uuid + ".banCount", banAmount);
            banConfig.set("bans." + uuid + ".timeLeft", null);
            saveBansFile(banFile, banConfig, this);
        }
    }

    public FileConfiguration getBanConfig() {
        return banConfig;
    }
}
