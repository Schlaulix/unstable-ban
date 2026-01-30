package at.blaulix.unstableBan;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String alias,
            @NotNull String @NotNull [] args
    ) {
        List<String> completions = new ArrayList<>();

        if (!command.getName().equalsIgnoreCase("unstableban")) {
            return completions;
        }

        // /unstableban <subcommand>
        if (args.length == 1) {
            completions.add("help");

            if (sender.hasPermission("unstableban.reload")) {
                completions.add("reload");
            }
            if (sender.hasPermission("unstableban.getbans")) {
                completions.add("getbans");
            }
            if (sender.hasPermission("unstableban.setbans")) {
                completions.add("setbans");
            }
            return completions;
        }

        // /unstableban getbans <player>
        if (args.length == 2 && args[0].equalsIgnoreCase("getbans")) {
            if (!sender.hasPermission("unstableban.getbans")) return completions;

            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
            return completions;
        }

        // /unstableban setbans <player>
        if (args.length == 2 && args[0].equalsIgnoreCase("setbans")) {
            if (!sender.hasPermission("unstableban.setbans")) return completions;

            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
            return completions;
        }

        // /unstableban setbans <player> <value>
        if (args.length == 3 && args[0].equalsIgnoreCase("setbans")) {
            if (!sender.hasPermission("unstableban.setbans")) return completions;

            completions.add("0");
            completions.add("1");
            completions.add("3");
            return completions;
        }

        return completions;
    }
}
