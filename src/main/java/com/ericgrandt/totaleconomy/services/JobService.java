package com.ericgrandt.totaleconomy.services;

import com.ericgrandt.totaleconomy.data.JobData;
import com.ericgrandt.totaleconomy.data.dto.JobActionDto;
import com.ericgrandt.totaleconomy.data.dto.JobExperienceDto;
import com.ericgrandt.totaleconomy.data.dto.JobRewardDto;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobService {
    private final Logger logger;
    private final JobData jobData;

    public JobService(Logger logger, JobData jobData) {
        this.logger = logger;
        this.jobData = jobData;
    }

    public JobRewardDto getJobReward(String actionName, String materialName) {
        try {
            JobActionDto jobActionDto = jobData.getJobActionByName(actionName);
            return jobData.getJobReward(jobActionDto.id(), materialName);
        } catch (SQLException e) {
            logger.log(
                Level.SEVERE,
                String.format(
                    "[Total Economy] Error calling getJobReward (actionName: %s, materialName: %s)",
                    actionName,
                    materialName
                ),
                e
            );
            return null;
        }
    }

    public void addExperience(UUID accountId, UUID jobId, int experienceToAdd) {
        try {
            JobExperienceDto jobExperienceDto = jobData.getExperienceForJob(accountId, jobId);
            if (jobExperienceDto == null) {
                logger.log(
                    Level.WARNING,
                    String.format(
                        "[Total Economy] No job experience entry found for user (accountId: %s, jobId: %s)",
                        accountId,
                        jobId
                    )
                );
                return;
            }

            jobData.updateExperienceForJob(
                accountId,
                jobId,
                jobExperienceDto.experience() + experienceToAdd
            );
        } catch (SQLException e) {
            logger.log(
                Level.SEVERE,
                String.format(
                    "[Total Economy] Error calling addExperience (accountId: %s, jobId: %s, experience: %s)",
                    accountId,
                    jobId,
                    experienceToAdd
                ),
                e
            );
        }
    }
}
