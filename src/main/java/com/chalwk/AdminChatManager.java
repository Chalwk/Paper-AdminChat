package com.chalwk;

import com.chalwk.config.PluginConfig;
import com.chalwk.util.MessageHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AdminChatManager {
    private final AdminChat plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Set<UUID> hiddenPlayers = new HashSet<>();
    private final Map<UUID, String> toggledChannels = new HashMap<>(); // New: Store toggled channels

    public AdminChatManager(AdminChat plugin) {
        this.plugin = plugin;
    }

    public void toggleChannel(Player player, String channel) {
        UUID playerId = player.getUniqueId();
        PluginConfig config = plugin.getConfigManager().getConfig();

        // Validate channel
        if (!config.channelExists(channel)) {
            MessageHelper.sendMessage(player, config.getMessage("no_channel"));
            return;
        }

        // Check permission
        String permission = config.getChannelPermission(channel);
        if (!player.hasPermission(permission)) {
            MessageHelper.sendMessage(player, config.getMessage("no_permission"));
            return;
        }

        // Toggle the channel
        if (toggledChannels.containsKey(playerId) && toggledChannels.get(playerId).equals(channel)) {
            // Turn off the channel
            toggledChannels.remove(playerId);
            MessageHelper.sendMessage(player, config.getMessage("channel_off")
                    .replace("{channel}", channel));
        } else {
            // Turn on the channel or switch to a different one
            toggledChannels.put(playerId, channel);
            MessageHelper.sendMessage(player, config.getMessage("channel_on")
                    .replace("{channel}", channel));
        }
    }

    public String getToggledChannel(Player player) {
        return toggledChannels.get(player.getUniqueId());
    }

    public boolean hasToggledChannel(Player player) {
        return toggledChannels.containsKey(player.getUniqueId());
    }

    public void clearToggledChannel(Player player) {
        toggledChannels.remove(player.getUniqueId());
    }

    // Modified sendMessage to handle toggled channels
    public void sendMessage(Player sender, String channel, String message) {
        PluginConfig config = plugin.getConfigManager().getConfig();

        // Check cooldown
        if (hasCooldown(sender)) {
            int remaining = getRemainingCooldown(sender);
            MessageHelper.sendMessage(sender,
                    config.getMessage("cooldown").replace("{seconds}", String.valueOf(remaining)));
            return;
        }

        // Validate channel
        if (!config.channelExists(channel)) {
            MessageHelper.sendMessage(sender, config.getMessage("no_channel"));
            return;
        }

        // Check permission
        String permission = config.getChannelPermission(channel);
        if (!sender.hasPermission(permission)) {
            MessageHelper.sendMessage(sender, config.getMessage("no_permission"));
            return;
        }

        // Format message
        String format = config.getChannelFormat(channel)
                .replace("{sender}", sender.getName())
                .replace("{channel}", channel)
                .replace("{message}", message);

        Component formattedMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(format);

        // Send to all players with permission and not hidden
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("adminchat.use") && !isHidden(player)) {
                player.sendMessage(formattedMessage);

                // Play sound if enabled
                if (config.isSoundEnabled(channel)) {
                    try {
                        Sound sound = Sound.valueOf(config.getSoundType(channel));
                        player.playSound(
                                player.getLocation(),
                                sound,
                                (float) config.getSoundVolume(channel),
                                (float) config.getSoundPitch(channel)
                        );
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid sound type for channel " + channel);
                    }
                }
            }
        }

        // Also send to console
        Bukkit.getConsoleSender().sendMessage(formattedMessage);

        // Set cooldown
        setCooldown(sender);
    }

    // Overloaded method to send message using toggled channel
    public void sendToggledMessage(Player sender, String message) {
        String channel = getToggledChannel(sender);
        if (channel == null) {
            // Fall back to default channel if no toggle is set
            channel = plugin.getConfigManager().getConfig().getDefaultChannel();
        }
        sendMessage(sender, channel, message);
    }

    // Existing methods remain the same...
    public void toggleVisibility(Player player) {
        UUID playerId = player.getUniqueId();
        PluginConfig config = plugin.getConfigManager().getConfig();

        if (hiddenPlayers.contains(playerId)) {
            hiddenPlayers.remove(playerId);
            MessageHelper.sendMessage(player, config.getMessage("toggled_on"));
        } else {
            hiddenPlayers.add(playerId);
            MessageHelper.sendMessage(player, config.getMessage("toggled_off"));
        }
    }

    public void toggleVisibilityForPlayer(Player target, Player executor) {
        UUID targetId = target.getUniqueId();
        PluginConfig config = plugin.getConfigManager().getConfig();

        if (hiddenPlayers.contains(targetId)) {
            hiddenPlayers.remove(targetId);
            String message = config.getMessage("toggled_for")
                    .replace("{state}", "enabled")
                    .replace("{player}", target.getName());
            MessageHelper.sendMessage(executor, message);
            MessageHelper.sendMessage(target, "Your admin chat visibility was enabled by " + executor.getName());
        } else {
            hiddenPlayers.add(targetId);
            String message = config.getMessage("toggled_for")
                    .replace("{state}", "disabled")
                    .replace("{player}", target.getName());
            MessageHelper.sendMessage(executor, message);
            MessageHelper.sendMessage(target, "Your admin chat visibility was disabled by " + executor.getName());
        }
    }

    public boolean isHidden(Player player) {
        return hiddenPlayers.contains(player.getUniqueId());
    }

    private boolean hasCooldown(Player player) {
        if (!cooldowns.containsKey(player.getUniqueId())) return false;

        long lastMessage = cooldowns.get(player.getUniqueId());
        int cooldown = plugin.getConfigManager().getConfig().getCooldown();

        return (System.currentTimeMillis() - lastMessage) < (cooldown * 1000L);
    }

    private int getRemainingCooldown(Player player) {
        long lastMessage = cooldowns.get(player.getUniqueId());
        int cooldown = plugin.getConfigManager().getConfig().getCooldown();
        long remaining = (cooldown * 1000L) - (System.currentTimeMillis() - lastMessage);

        return (int) Math.ceil(remaining / 1000.0);
    }

    private void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());

        new BukkitRunnable() {
            @Override
            public void run() {
                cooldowns.remove(player.getUniqueId());
            }
        }.runTaskLater(plugin, plugin.getConfigManager().getConfig().getCooldown() * 20L);
    }
}