package com.chalwk.commands;

import com.chalwk.AdminChat;
import com.chalwk.util.MessageHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record AdminChatCommand(AdminChat plugin) implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        if (!sender.hasPermission("adminchat.use")) {
            MessageHelper.sendMessage(sender, "You don't have permission to use admin chat!");
            return true;
        }

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                MessageHelper.sendMessage(sender, "Only players can use admin chat!");
                return true;
            }

            // If player has a toggled channel, send usage message for toggled mode
            if (plugin.getChatManager().hasToggledChannel(player)) {
                String channel = plugin.getChatManager().getToggledChannel(player);
                String message = plugin.getConfigManager().getConfig().getMessage("usage_toggled")
                        .replace("{channel}", channel);
                MessageHelper.sendMessage(sender, message);
            } else {
                // Regular usage message
                String message = plugin.getConfigManager().getConfig().getMessage("usage");
                MessageHelper.sendMessage(sender, message);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("toggle")) {
            if (!(sender instanceof Player player)) {
                MessageHelper.sendMessage(sender, "Only players can toggle admin chat!");
                return true;
            }

            if (args.length > 1 && sender.hasPermission("adminchat.admin")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    String message = plugin.getConfigManager().getConfig().getMessage("player_not_found");
                    MessageHelper.sendMessage(sender, message);
                    return true;
                }
                plugin.getChatManager().toggleVisibilityForPlayer(target, player);
            } else {
                plugin.getChatManager().toggleVisibility(player);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("adminchat.admin")) {
            plugin.reload();
            String message = plugin.getConfigManager().getConfig().getMessage("reloaded");
            MessageHelper.sendMessage(sender, message);
            return true;
        }

        if (args[0].equalsIgnoreCase("off")) {
            if (!(sender instanceof Player player)) {
                MessageHelper.sendMessage(sender, "Only players can toggle channels!");
                return true;
            }

            if (plugin.getChatManager().hasToggledChannel(player)) {
                String channel = plugin.getChatManager().getToggledChannel(player);
                plugin.getChatManager().clearToggledChannel(player);
                String message = plugin.getConfigManager().getConfig().getMessage("channel_off")
                        .replace("{channel}", channel);
                MessageHelper.sendMessage(sender, message);
            } else {
                MessageHelper.sendMessage(sender, "&cYou don't have any channel toggled!");
            }
            return true;
        }

        if (!(sender instanceof Player player)) {
            MessageHelper.sendMessage(sender, "Only players can send admin chat messages!");
            return true;
        }

        String channel;
        String message;

        // Check if the first argument is a valid channel name
        if (plugin.getConfigManager().getConfig().channelExists(args[0].toLowerCase())) {
            channel = args[0].toLowerCase();

            // If there are no more arguments, toggle the channel
            if (args.length == 1) {
                plugin.getChatManager().toggleChannel(player, channel);
                return true;
            } else {
                // If there are more arguments, send message to this channel
                message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            }
        } else {
            if (plugin.getChatManager().hasToggledChannel(player)) {
                channel = plugin.getChatManager().getToggledChannel(player);
            } else {
                // No toggled channel, use default
                channel = plugin.getConfigManager().getConfig().getDefaultChannel();
            }
            message = String.join(" ", args);
        }

        plugin.getChatManager().sendMessage(player, channel, message);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String label,
                                      @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();

            for (String channel : new String[]{"mod", "admin", "trial"}) {
                if (channel.startsWith(partial) && sender.hasPermission("adminchat.channel." + channel)) {
                    completions.add(channel);
                }
            }

            if ("toggle".startsWith(partial)) {
                completions.add("toggle");
            }
            if ("reload".startsWith(partial) && sender.hasPermission("adminchat.admin")) {
                completions.add("reload");
            }
            if ("off".startsWith(partial)) {
                completions.add("off");
            }

            if (sender.hasPermission("adminchat.admin")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(partial)) {
                        completions.add(player.getName());
                    }
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("toggle")) {
            String partial = args[1].toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(partial)) {
                    completions.add(player.getName());
                }
            }
        }

        return completions;
    }
}