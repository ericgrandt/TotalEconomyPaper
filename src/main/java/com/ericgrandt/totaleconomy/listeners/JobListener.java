package com.ericgrandt.totaleconomy.listeners;

import com.ericgrandt.totaleconomy.data.dto.JobRewardDto;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.models.AddExperienceResult;
import com.ericgrandt.totaleconomy.services.JobService;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class JobListener implements Listener {
    private final EconomyImpl economy;
    private final JobService jobService;

    public JobListener(EconomyImpl economy, JobService jobService) {
        this.economy = economy;
        this.jobService = jobService;
    }

    @EventHandler
    public void onBreakAction(BlockBreakEvent event) {
        JobRewardDto jobRewardDto = jobService.getJobReward("break", event.getBlock().getType().name().toLowerCase());
        if (jobRewardDto == null) {
            return;
        }

        Player player = event.getPlayer();
        addExperience(player, jobRewardDto);
        economy.depositPlayer(player, jobRewardDto.money().doubleValue());
    }

    @EventHandler
    public void onKillAction(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        // entity.getKiller();
    }

    @EventHandler
    public void createJobExperienceOnPlayerJoin(PlayerJoinEvent event) {
        jobService.createJobExperienceForAccount(event.getPlayer().getUniqueId());
    }

    private void addExperience(Player player, JobRewardDto jobRewardDto) {
        AddExperienceResult addExperienceResult = jobService.addExperience(
            player.getUniqueId(),
            UUID.fromString(jobRewardDto.jobId()),
            jobRewardDto.experience()
        );
        if (addExperienceResult.leveledUp()) {
            player.sendMessage(getLevelUpMessage(addExperienceResult));
        }
    }

    private Component getLevelUpMessage(AddExperienceResult addExperienceResult) {
        return Component.text(
            addExperienceResult.jobName(),
            TextColor.fromHexString("#DADFE1"),
            TextDecoration.BOLD
        ).append(
            Component.text(
                " is now level",
                TextColor.fromHexString("#708090")
            ).decoration(TextDecoration.BOLD, false)
        ).append(
            Component.text(
                String.format(" %s", addExperienceResult.level()),
                TextColor.fromHexString("#DADFE1"),
                TextDecoration.BOLD
            )
        );
    }
}
