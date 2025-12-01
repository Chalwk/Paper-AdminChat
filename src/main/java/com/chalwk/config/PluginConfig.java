package com.chalwk.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class PluginConfig {
    private final Map<String, Object> settings = new HashMap<>();

    public void loadFromConfig(org.bukkit.configuration.ConfigurationSection config) {
        // General settings
        settings.put("default_channel", config.getString("default_channel", "admin"));
        settings.put("cooldown", config.getInt("cooldown", 2));

        // Channels
        ConfigurationSection channels = config.getConfigurationSection("channels");
        if (channels != null) {
            for (String channel : channels.getKeys(false)) {
                ConfigurationSection channelSection = channels.getConfigurationSection(channel);
                if (channelSection != null) {
                    settings.put("channel." + channel + ".permission",
                            channelSection.getString("permission", "adminchat.channel." + channel));
                    settings.put("channel." + channel + ".format",
                            channelSection.getString("format", "&8[{channel}] &f{sender}&8: &7{message}"));
                    settings.put("channel." + channel + ".prefix",
                            channelSection.getString("prefix", "&8[{channel}]"));

                    settings.put("channel." + channel + ".sound.enabled",
                            channelSection.getBoolean("sound.enabled", true));
                    settings.put("channel." + channel + ".sound.type",
                            channelSection.getString("sound.type", "BLOCK_NOTE_BLOCK_PLING"));
                    settings.put("channel." + channel + ".sound.volume",
                            channelSection.getDouble("sound.volume", 0.5));
                    settings.put("channel." + channel + ".sound.pitch",
                            channelSection.getDouble("sound.pitch", 1.5));
                }
            }
        }

        // Messages
        ConfigurationSection messages = config.getConfigurationSection("messages");
        if (messages != null) {
            for (String key : messages.getKeys(false)) {
                settings.put("message." + key, messages.getString(key));
            }
        }

        // Notifications
        settings.put("notifications.join_notification", config.getBoolean("notifications.join_notification", true));
        settings.put("notifications.join_message", config.getString("notifications.join_message"));
        settings.put("notifications.quit_notification", config.getBoolean("notifications.quit_notification", true));
        settings.put("notifications.quit_message", config.getString("notifications.quit_message"));
    }

    public String getMessage(String key) {
        return (String) settings.getOrDefault("message." + key, "&cMessage not configured: " + key);
    }

    public String getDefaultChannel() {
        return (String) settings.get("default_channel");
    }

    public int getCooldown() {
        return (int) settings.get("cooldown");
    }

    public boolean channelExists(String channel) {
        return settings.containsKey("channel." + channel + ".permission");
    }

    public String getChannelPermission(String channel) {
        return (String) settings.get("channel." + channel + ".permission");
    }

    public String getChannelFormat(String channel) {
        return (String) settings.get("channel." + channel + ".format");
    }

    public boolean isSoundEnabled(String channel) {
        return (boolean) settings.get("channel." + channel + ".sound.enabled");
    }

    public String getSoundType(String channel) {
        return (String) settings.get("channel." + channel + ".sound.type");
    }

    public double getSoundVolume(String channel) {
        return (double) settings.get("channel." + channel + ".sound.volume");
    }

    public double getSoundPitch(String channel) {
        return (double) settings.get("channel." + channel + ".sound.pitch");
    }

    public boolean isJoinNotificationEnabled() {
        return (boolean) settings.get("notifications.join_notification");
    }

    public String getJoinMessage() {
        return (String) settings.get("notifications.join_message");
    }

    public boolean isQuitNotificationEnabled() {
        return (boolean) settings.get("notifications.quit_notification");
    }

    public String getQuitMessage() {
        return (String) settings.get("notifications.quit_message");
    }
}