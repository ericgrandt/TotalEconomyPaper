package com.ericgrandt.totaleconomy.listeners;

import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.services.JobService;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private final EconomyImpl economy;
    private final JobService jobService;

    public PlayerListener(EconomyImpl economy, JobService jobService) {
        this.economy = economy;
        this.jobService = jobService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        CompletableFuture.runAsync(() -> onPlayerJoinHandler(player));
    }

    public void onPlayerJoinHandler(Player player) {
        if (economy.hasAccount(player)) {
            return;
        }

        economy.createPlayerAccount(player);
        jobService.createJobExperienceForAccount(player.getUniqueId());
    }
}
