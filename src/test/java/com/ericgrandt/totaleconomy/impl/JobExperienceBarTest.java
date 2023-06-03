package com.ericgrandt.totaleconomy.impl;

import com.ericgrandt.totaleconomy.models.JobExperience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class JobExperienceBarTest {
    @Test
    @Tag("Unit")
    public void setExperienceBarName_ShouldChangeNameOfBossBar() {
        // Arrange
        JobExperience jobExperience = new JobExperience(
            "Miner",
            50,
            0,
            100,
            1
        );

        Player player = mock(Player.class);
        JobExperienceBar sut = new JobExperienceBar(player, null);

        // Act
        sut.setExperienceBarName(jobExperience, 25);

        Component actual = sut.getBossBar().name();
        Component expected = Component.text("Miner [+25 EXP]");

        // Assert
        assertEquals(expected, actual);
    }
}
