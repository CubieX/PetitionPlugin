package com.floyd.bukkit.petition;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Handle events for all Player related events
 * @author FloydATC
 */

public class PetitionPlayerListener implements Listener {
    private final PetitionPlugin plugin;

    public PetitionPlayerListener(PetitionPlugin instance) {
        plugin = instance;
    	Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    //Insert Player related code here    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Play back messages stored in this player's maildir (if any)
        String[] messages = plugin.getMessages(player);
        if (messages.length > 0) {
            for (String message : messages) {
                player.sendMessage(message);
            }
            player.sendMessage("[Pe] §7Verwende /ticket um deine offenen Tickets zu verwalten.");
        }
    }
}
