package com.ericgrandt.totaleconomy.listeners;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.data.dto.JobRewardDto;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.services.JobService;
import java.math.BigDecimal;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JobListenerTest {
    @Test
    @Tag("Unit")
    public void onBreakAction_WithJobRewardFound_ShouldDepositMoney() {
        // Arrange
        Material material = Material.STONE;
        String materialName = material.name().toLowerCase();
        JobRewardDto jobRewardDto = new JobRewardDto(
            "",
            "",
            "",
            1,
            materialName,
            BigDecimal.TEN,
            1
        );

        Block blockMock = mock(Block.class);
        Player playerMock = mock(Player.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        JobService jobServiceMock = mock(JobService.class);
        when(blockMock.getType()).thenReturn(material);
        when(jobServiceMock.getJobReward("break", materialName)).thenReturn(jobRewardDto);

        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(blockMock, playerMock);
        JobListener sut = new JobListener(economyMock, jobServiceMock);

        // Act
        sut.onBreakAction(blockBreakEvent);

        // Assert
        verify(economyMock, times(1)).depositPlayer(playerMock, BigDecimal.TEN.doubleValue());
    }

    @Test
    @Tag("Unit")
    public void onBreakAction_WithJobRewardFound_ShouldAddExperience() {
        // Arrange
        Material material = Material.STONE;
        String materialName = material.name().toLowerCase();
        JobRewardDto jobRewardDto = new JobRewardDto(
            "",
            "jobId",
            "",
            1,
            materialName,
            BigDecimal.TEN,
            1
        );

        Block blockMock = mock(Block.class);
        Player playerMock = mock(Player.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        JobService jobServiceMock = mock(JobService.class);
        when(blockMock.getType()).thenReturn(material);
        when(jobServiceMock.getJobReward("break", materialName)).thenReturn(jobRewardDto);

        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(blockMock, playerMock);
        JobListener sut = new JobListener(economyMock, jobServiceMock);

        // Act
        sut.onBreakAction(blockBreakEvent);

        // Assert
        verify(jobServiceMock, times(1)).addExperience(
            jobRewardDto.jobId(),
            jobRewardDto.experience()
        );
    }

    @Test
    @Tag("Unit")
    public void onBreakAction_WithNoJobRewardFound_ShouldNotDepositMoney() {
        // Arrange
        Material material = Material.STONE;
        String materialName = material.name().toLowerCase();

        Block blockMock = mock(Block.class);
        Player playerMock = mock(Player.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        JobService jobServiceMock = mock(JobService.class);
        when(blockMock.getType()).thenReturn(material);
        when(jobServiceMock.getJobReward("break", materialName)).thenReturn(null);

        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(blockMock, playerMock);
        JobListener sut = new JobListener(economyMock, jobServiceMock);

        // Act
        sut.onBreakAction(blockBreakEvent);

        // Assert
        verify(economyMock, times(0)).depositPlayer(any(Player.class), any(Double.class));
    }

    @Test
    @Tag("Unit")
    public void onBreakAction_WithNoJobRewardFound_ShouldNotAddExperience() {
        // Arrange
        Material material = Material.STONE;
        String materialName = material.name().toLowerCase();

        Block blockMock = mock(Block.class);
        Player playerMock = mock(Player.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        JobService jobServiceMock = mock(JobService.class);
        when(blockMock.getType()).thenReturn(material);
        when(jobServiceMock.getJobReward("break", materialName)).thenReturn(null);

        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(blockMock, playerMock);
        JobListener sut = new JobListener(economyMock, jobServiceMock);

        // Act
        sut.onBreakAction(blockBreakEvent);

        // Assert
        verify(jobServiceMock, times(0)).addExperience(any(String.class), any(Integer.class));
    }
}
