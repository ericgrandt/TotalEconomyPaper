package com.ericgrandt.totaleconomy.listeners;

import com.ericgrandt.totaleconomy.data.dto.JobRewardDto;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.services.JobService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class JobListener implements Listener {
    private final JobService jobService;
    private final EconomyImpl economy;

    public JobListener(EconomyImpl economy, JobService jobService) {
        this.economy = economy;
        this.jobService = jobService;
    }

    @EventHandler
    public void onBreakAction(BlockBreakEvent event) {
        String materialName = event.getBlock().getType().name().toLowerCase();
        JobRewardDto jobRewardDto = jobService.getJobReward("break", materialName);
        if (jobRewardDto == null) {
            return;
        }

        Player player = event.getPlayer();
        player.sendMessage(jobRewardDto.money() + " " + jobRewardDto.experience());
        // Check if reward exists for broken block
        // If it doesn't, return
        // If it does, use the values to call the service layer to add experience and money
    }

    // PlayerJoinEvent to set up job experience rows for the player
}
