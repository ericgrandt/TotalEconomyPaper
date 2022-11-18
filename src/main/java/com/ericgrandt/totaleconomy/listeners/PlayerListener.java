package com.ericgrandt.totaleconomy.listeners;

import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private final EconomyImpl economy;

    public PlayerListener(EconomyImpl economy) {
        this.economy = economy;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (economy.hasAccount(player)) {
            return;
        }

        economy.createPlayerAccount(player);
    }
}
