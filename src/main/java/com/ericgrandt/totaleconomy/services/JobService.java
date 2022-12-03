package com.ericgrandt.totaleconomy.services;

import com.ericgrandt.totaleconomy.data.JobData;
import com.ericgrandt.totaleconomy.data.dto.JobActionDto;
import com.ericgrandt.totaleconomy.data.dto.JobDto;
import com.ericgrandt.totaleconomy.data.dto.JobExperienceDto;
import com.ericgrandt.totaleconomy.data.dto.JobRewardDto;
import com.ericgrandt.totaleconomy.models.AddExperienceResult;
import com.ericgrandt.totaleconomy.models.JobExperience;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public List<JobExperience> getExperienceForAllJobs(UUID accountId) throws SQLException {
        List<JobExperienceDto> jobExperienceDtos = jobData.getExperienceForAllJobs(accountId);
        List<JobExperience> jobExperienceList = new ArrayList<>();

        for (JobExperienceDto jobExperienceDto : jobExperienceDtos) {
            JobDto jobDto = jobData.getJob(UUID.fromString(jobExperienceDto.jobId()));
            int curLevel = calculateLevelFromExperience(jobExperienceDto.experience());

            jobExperienceList.add(
                new JobExperience(
                    jobDto.jobName(),
                    jobExperienceDto.experience(),
                    calculateExperienceForNextLevel(curLevel),
                    curLevel
                )
            );
        }

        return jobExperienceList;
    }

    public void createJobExperienceForAccount(UUID accountId) {
        try {
            jobData.createJobExperienceRows(accountId);
        } catch (SQLException e) {
            logger.log(
                Level.SEVERE,
                String.format(
                    "[Total Economy] Error calling createJobExperienceForAccount (accountId: %s)",
                    accountId
                ),
                e
            );
        }
    }

    public AddExperienceResult addExperience(UUID accountId, UUID jobId, int experienceToAdd) {
        try {
            Optional<JobExperienceDto> jobExperienceDtoOptional = getJobExperienceDto(accountId, jobId);
            if (jobExperienceDtoOptional.isEmpty()) {
                return new AddExperienceResult("", -1, false);
            }

            JobExperienceDto jobExperienceDto = jobExperienceDtoOptional.get();
            JobDto jobDto = jobData.getJob(jobId);
            int currentExperience = jobExperienceDto.experience();
            int newExperience = jobExperienceDto.experience() + experienceToAdd;
            int currentLevel = calculateLevelFromExperience(currentExperience);
            int newLevel = calculateLevelFromExperience(newExperience);

            jobData.updateExperienceForJob(accountId, jobId, newExperience);

            return new AddExperienceResult(jobDto.jobName(), newLevel, newLevel > currentLevel);
        } catch (SQLException e) {
            logger.log(
                Level.SEVERE,
                String.format(
                    "[Total Economy] Error calling addExperience (accountId: %s, jobId: %s, experienceToAdd: %s)",
                    accountId,
                    jobId,
                    experienceToAdd
                ),
                e
            );
            return new AddExperienceResult("", -1, false);
        }
    }

    public int calculateLevelFromExperience(int experience) {
        // Inverse of: 49 * (cur_level ^ 2)
        return (int) Math.ceil(Math.sqrt(experience) / 7);
    }

    private int calculateExperienceForNextLevel(int curLevel) {
        return (int) Math.ceil(49 * Math.pow(curLevel, 2)) + 1;
    }

    private Optional<JobExperienceDto> getJobExperienceDto(UUID accountId, UUID jobId) throws SQLException {
        return Optional.ofNullable(jobData.getExperienceForJob(accountId, jobId));
    }
}
