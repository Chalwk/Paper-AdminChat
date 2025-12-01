package com.chalwk.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;

public class MessageHelper {
    public static void sendMessage(CommandSender sender, String message) {
        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        sender.sendMessage(component);
    }
}