package com.ericgrandt.totaleconomy.services;

import com.ericgrandt.totaleconomy.data.JobData;
import com.ericgrandt.totaleconomy.data.dto.JobActionDto;
import com.ericgrandt.totaleconomy.data.dto.JobRewardDto;
import java.sql.SQLException;
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

    public void addExperience(String jobId, int experienceToAdd) {

    }
}
