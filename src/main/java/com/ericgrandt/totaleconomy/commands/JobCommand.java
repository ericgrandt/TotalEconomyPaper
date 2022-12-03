package com.ericgrandt.totaleconomy.commands;

import com.ericgrandt.totaleconomy.models.JobExperience;
import com.ericgrandt.totaleconomy.services.JobService;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class JobCommand implements CommandExecutor {
    private final Logger logger;
    private final JobService jobService;

    public JobCommand(Logger logger, JobService jobService) {
        this.logger = logger;
        this.jobService = jobService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        try {
            TextComponent.@NotNull Builder message = Component.empty().content("\n").toBuilder();
            List<JobExperience> jobExperienceList = jobService.getExperienceForAllJobs(player.getUniqueId());
            for (JobExperience jobExperience : jobExperienceList) {
                message.append(Component.text(jobExperience.jobName(), NamedTextColor.GRAY, TextDecoration.BOLD))
                    .append(Component.text(String.format(" [LVL %s]", jobExperience.level()), NamedTextColor.GRAY))
                    .append(Component.text(
                        String.format(
                            " [%s/%s EXP]",
                            jobExperience.experience(),
                            jobExperience.experienceToNext()
                        ),
                        NamedTextColor.GRAY
                    )).append(Component.newline());
            }

            player.sendMessage(message.build());
        } catch (SQLException e) {
            player.sendMessage(
                Component.text("An error has occurred. Please contact an administrator.", NamedTextColor.RED)
            );
            logger.log(
                Level.SEVERE,
                "An exception occurred during the handling of the job command.",
                e
            );
            return false;
        }

        return true;
    }
}
