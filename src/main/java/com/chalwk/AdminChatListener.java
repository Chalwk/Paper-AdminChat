package com.chalwk;

import com.chalwk.config.PluginConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public record AdminChatListener(AdminChat plugin) implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PluginConfig config = plugin.getConfigManager().getConfig();

        if (config.isJoinNotificationEnabled() && player.hasPermission("adminchat.use")) {
            String message = config.getJoinMessage().replace("{player}", player.getName());
            Component formattedMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(message);

            for (Player staff : plugin.getServer().getOnlinePlayers()) {
                if (staff.hasPermission("adminchat.use") && !plugin.getChatManager().isHidden(staff)) {
                    staff.sendMessage(formattedMessage);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PluginConfig config = plugin.getConfigManager().getConfig();

        if (config.isQuitNotificationEnabled() && player.hasPermission("adminchat.use")) {
            String message = config.getQuitMessage().replace("{player}", player.getName());
            Component formattedMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(message);

            for (Player staff : plugin.getServer().getOnlinePlayers()) {
                if (staff.hasPermission("adminchat.use") && !plugin.getChatManager().isHidden(staff)) {
                    staff.sendMessage(formattedMessage);
                }
            }
        }
    }
}