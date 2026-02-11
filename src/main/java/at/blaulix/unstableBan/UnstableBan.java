package at.blaulix.unstableBan;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
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
        boolean noLastReset = getConfig().getBoolean("ban-durations-last-reset");

        int banTimesLength = getConfig().getStringList("ban-durations").size();
        String path = "bans." + uuid;
        int playerBanCount = banCount(banConfig, uuid);

        if (!banConfig.contains(path)) {
            banConfig.set("bans." + uuid + ".banCount", 0);
            saveBansFile(banFile, banConfig, this);
            if(noLastReset){
                if(playerBanCount >= banTimesLength){
                    banConfig.set("bans." + uuid + ".banCount", 0);
                    saveBansFile(banFile, banConfig, this);
                }
            }
        }

        if (timeResetEnabled) {
            if (playerBanCount > 0) {
                long timeLeft = banConfig.getLong(path + ".timeLeft");
                if (timeLeft > 0) {
                    banCountdown.startBanCountdown(player, (int) timeLeft);
                } else {
                    String banLoseAfter = getConfig().getString("lose-ban-after-duration");

                    if (banLoseAfter == null || banLoseAfter.isEmpty()) {
                        return;
                    }
                    int banLoseAfterSeconds = TimeFormatter.formatToSeconds(banLoseAfter);

                    banCountdown.startBanCountdown(player, banLoseAfterSeconds);
                }
            } else {
                banConfig.set(path + ".timeLeft", 0);
                saveBansFile(banFile, banConfig, this);
            }
        }

        boolean joinMsgRadiusEnabled = getConfig().getBoolean("join-msg-radius-enabled");
        int joinRadius = getConfig().getInt("join-msg-radius");

        player.getWorld().getPlayers().forEach(p -> {
            if (p.getLocation().distanceSquared(player.getLocation()) <= joinRadius * joinRadius) {
                if (joinMsgRadiusEnabled) {
                    Component joinMsg = event.joinMessage();
                    event.joinMessage(null);
                    if (joinMsg != null) {
                        p.sendMessage(joinMsg);
                    }
                }
            }
        });

    }

    @EventHandler
    public void bans(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        int currentBanCount = banCount(banConfig, uuid);
        String banReason = getConfig().getString("ban-reason");
        String banReasonNoKiller = getConfig().getString("ban-reason-natural-causes");
        String banDuration = getConfig().getStringList("ban-durations").get(currentBanCount);
        int radius = getConfig().getInt("death-radius");


        Player killer = event.getPlayer().getKiller();
        boolean killResetEnabled = getConfig().getBoolean("lose-ban-after-kill");
        String soundName = getConfig().getString("death-sound-type");

        assert soundName != null;
        NamespacedKey key = NamespacedKey.fromString(soundName.toUpperCase());
        Sound deathSound;

        if (key != null) {
            deathSound = Registry.SOUNDS.get(key);
        } else {
            deathSound = Sound.ENTITY_WITHER_SPAWN;
        }

        boolean deathSoundEnabled = getConfig().getBoolean("death-sound");
        boolean deathMessageEnabled = getConfig().getBoolean("death-message");

        player.getWorld().getPlayers().forEach(p -> {
            if (p.getLocation().distanceSquared(player.getLocation()) <= radius * radius) {
                if (deathSoundEnabled) {
                    if (deathSound != null) {
                        p.playSound(player.getLocation(), deathSound, 1.0f, 1.0f);
                    }
                }

                if (deathMessageEnabled) {
                    Component deathMessage = event.deathMessage();
                    event.deathMessage(null);
                    if (deathMessage != null) {
                        p.sendMessage(deathMessage);
                    }
                }
            }
        });

        String reason;

        if (killer != null) {
            reason = Objects.requireNonNull(banReason)
                    .replace("%player%", player.getName())
                    .replace("%banCount%", String.valueOf(currentBanCount + 1));

            reason = reason.replace("%killer%", killer.getName());
        }else {
            reason = Objects.requireNonNull(banReasonNoKiller)
                    .replace("%player%", player.getName())
                    .replace("%banCount%", String.valueOf(currentBanCount + 1));
        }


        Bukkit.dispatchCommand(

                Bukkit.getConsoleSender(), "ban " + uuid + " " + banDuration + " " + reason);

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
                    banConfig.set("bans." + killerUuid + ".timeLeft", null);
                    saveBansFile(banFile, banConfig, this);
                    killer.sendMessage("§aYou have reduced your ban count to " + newBanCount + " by killing " + player.getName() + "!");
                }
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        boolean leaveMsgRadiusEnabled = getConfig().getBoolean("leave-msg-radius-enabled");
        int leaveRadius = getConfig().getInt("leave-msg-radius");

        player.getWorld().getPlayers().forEach(p -> {
            if (p.getLocation().distanceSquared(player.getLocation()) <= leaveRadius * leaveRadius) {
                if (leaveMsgRadiusEnabled) {
                    Component leaveMessage = event.quitMessage();
                    event.quitMessage(null);
                    if (leaveMessage != null) {
                        p.sendMessage(leaveMessage);
                    }
                }
            }
        });
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
                sender.sendMessage("§e/ub togglebossbar §7- Toggle the visibility of your ban countdown bossbar if you have bans.");
                return true;
            }
            sender.sendMessage("§6UnstableBan Help:");
            sender.sendMessage("§e/ub help §7- Show this help message.");
            sender.sendMessage("§e/ub bans §7- Look up how many bans you have.");
            sender.sendMessage("§e/ub reload §7- Reload the plugin configuration.");
            sender.sendMessage("§e/ub togglebossbar §7- Toggle the visibility of your ban countdown bossbar if you have bans.");
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
                    banConfig.set("bans." + targetUuid + ".timeLeft", null);
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
        int loseBanAmount = getConfig().getInt("lose-ban-amount-after-duration");
        int banAmount;

        if (currentBanCount > 0) {
            if (loseBanAmount == -1) {
                banAmount = 0;
            } else {
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
