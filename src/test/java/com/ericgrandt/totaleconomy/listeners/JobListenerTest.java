package com.ericgrandt.totaleconomy.listeners;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.TestUtils;
import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.BalanceData;
import com.ericgrandt.totaleconomy.data.Database;
import com.ericgrandt.totaleconomy.data.JobData;
import com.ericgrandt.totaleconomy.data.dto.BalanceDto;
import com.ericgrandt.totaleconomy.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.data.dto.JobExperienceDto;
import com.ericgrandt.totaleconomy.data.dto.JobRewardDto;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.models.AddExperienceResult;
import com.ericgrandt.totaleconomy.services.JobService;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JobListenerTest {
    @Mock
    private Logger loggerMock;

    @Test
    @Tag("Unit")
    public void onBreakAction_WithJobRewardFound_ShouldDepositMoney() {
        // Arrange
        Material material = Material.STONE;
        String materialName = material.name().toLowerCase();
        UUID playerId = UUID.randomUUID();
        JobRewardDto jobRewardDto = new JobRewardDto(
            "",
            "de8ee82d-e988-4b6e-8dfd-8768415e4a0d",
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
        when(playerMock.getUniqueId()).thenReturn(playerId);
        when(jobServiceMock.getJobReward("break", materialName)).thenReturn(jobRewardDto);
        when(jobServiceMock.addExperience(playerId, UUID.fromString(jobRewardDto.jobId()), 1)).thenReturn(
            new AddExperienceResult("Test Job 1", 3, true)
        );

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
        UUID playerId = UUID.randomUUID();
        JobRewardDto jobRewardDto = new JobRewardDto(
            "",
            "de8ee82d-e988-4b6e-8dfd-8768415e4a0d",
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
        when(playerMock.getUniqueId()).thenReturn(playerId);
        when(jobServiceMock.getJobReward("break", materialName)).thenReturn(jobRewardDto);
        when(jobServiceMock.addExperience(playerId, UUID.fromString(jobRewardDto.jobId()), 1)).thenReturn(
            new AddExperienceResult("Test Job 1", 3, true)
        );

        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(blockMock, playerMock);
        JobListener sut = new JobListener(economyMock, jobServiceMock);

        // Act
        sut.onBreakAction(blockBreakEvent);

        // Assert
        verify(jobServiceMock, times(1)).addExperience(
            playerId,
            UUID.fromString(jobRewardDto.jobId()),
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
        verify(jobServiceMock, times(0)).addExperience(
            any(UUID.class),
            any(UUID.class),
            any(Integer.class)
        );
    }

    @Test
    @Tag("Unit")
    public void onBreakAction_WithJobRewardFoundAndLevelUp_ShouldSendMessageToPlayer() {
        // Arrange
        Material material = Material.STONE;
        String materialName = material.name().toLowerCase();
        UUID playerId = UUID.randomUUID();
        JobRewardDto jobRewardDto = new JobRewardDto(
            "",
            "de8ee82d-e988-4b6e-8dfd-8768415e4a0d",
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
        when(playerMock.getUniqueId()).thenReturn(playerId);
        when(jobServiceMock.getJobReward("break", materialName)).thenReturn(jobRewardDto);
        when(jobServiceMock.addExperience(playerId, UUID.fromString(jobRewardDto.jobId()), 1)).thenReturn(
            new AddExperienceResult("Test Job 1", 3, true)
        );

        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(blockMock, playerMock);
        JobListener sut = new JobListener(economyMock, jobServiceMock);

        // Act
        sut.onBreakAction(blockBreakEvent);
        Component expected = Component.text(
            "Test Job 1",
            TextColor.fromHexString("#DADFE1"),
            TextDecoration.BOLD
        ).append(
            Component.text(
                " is now level",
                TextColor.fromHexString("#708090")
            ).decoration(TextDecoration.BOLD, false)
        ).append(
            Component.text(
                " 3",
                TextColor.fromHexString("#DADFE1"),
                TextDecoration.BOLD
            )
        );

        // Assert
        verify(playerMock, times(1)).sendMessage(expected);
    }

    @Test
    @Tag("Unit")
    public void createJobExperienceOnPlayerJoin_ShouldCallTheJobService() {
        // Arrange
        UUID playerId = UUID.randomUUID();

        Player playerMock = mock(Player.class);
        JobService jobServiceMock = mock(JobService.class);
        when(playerMock.getUniqueId()).thenReturn(playerId);

        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(playerMock, "");
        JobListener sut = new JobListener(mock(EconomyImpl.class), jobServiceMock);

        // Act
        sut.createJobExperienceOnPlayerJoin(playerJoinEvent);

        // Assert
        verify(jobServiceMock, times(1)).createJobExperienceForAccount(playerId);
    }

    @Test
    @Tag("Integration")
    public void onBreakAction_WithJobReward_ShouldRewardExperienceAndMoney() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedDefaultBalances();
        TestUtils.seedAccounts();
        TestUtils.seedJobs();
        TestUtils.seedJobActions();
        TestUtils.seedJobRewards();
        TestUtils.seedJobExperience();

        CurrencyDto currencyDto = new CurrencyDto(0, "", "", "", 0, true);
        Database databaseMock = mock(Database.class);
        Block blockMock = mock(Block.class);
        Player playerMock = mock(Player.class);
        UUID playerId = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).then(x -> TestUtils.getConnection());
        when(blockMock.getType()).thenReturn(Material.COAL_ORE);
        when(playerMock.getUniqueId()).thenReturn(playerId);

        AccountData accountData = new AccountData(databaseMock);
        BalanceData balanceData = new BalanceData(databaseMock);
        JobData jobData = new JobData(databaseMock);
        JobService jobService = new JobService(loggerMock, jobData);
        EconomyImpl economy = new EconomyImpl(
            loggerMock,
            true,
            currencyDto,
            accountData,
            balanceData
        );

        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(blockMock, playerMock);
        JobListener sut = new JobListener(economy, jobService);

        // Act
        sut.onBreakAction(blockBreakEvent);

        // Assert
        BalanceDto actualBalance = TestUtils.getBalanceForAccountId(playerId, 1);
        BalanceDto expectedBalance = new BalanceDto(
            "",
            "",
            1,
            BigDecimal.valueOf(50.50).setScale(2, RoundingMode.DOWN)
        );
        assertNotNull(actualBalance);
        assertEquals(expectedBalance.balance(), actualBalance.balance());

        JobExperienceDto actualExperience = TestUtils.getExperienceForJob(
            playerId,
            UUID.fromString("a56a5842-1351-4b73-a021-bcd531260cd1")
        );
        JobExperienceDto expectedExperience = new JobExperienceDto("", "", "", 51);
        assertNotNull(actualExperience);
        assertEquals(expectedExperience.experience(), actualExperience.experience());
    }

    @Test
    @Tag("Integration")
    public void createJobExperienceOnPlayerJoin_ShouldCreateRows() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedDefaultBalances();
        TestUtils.seedAccounts();
        TestUtils.seedJobs();

        Database databaseMock = mock(Database.class);
        Player playerMock = mock(Player.class);
        UUID playerId = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");
        when(playerMock.getUniqueId()).thenReturn(playerId);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).then(x -> TestUtils.getConnection());

        JobData jobData = new JobData(databaseMock);
        JobService jobService = new JobService(loggerMock, jobData);
        PlayerJoinEvent event = new PlayerJoinEvent(playerMock, "");
        JobListener sut = new JobListener(mock(EconomyImpl.class), jobService);

        // Act
        sut.createJobExperienceOnPlayerJoin(event);

        List<JobExperienceDto> actual = TestUtils.getExperienceForJobs(playerId);
        List<JobExperienceDto> expected = Arrays.asList(
            new JobExperienceDto(
                "",
                "62694fb0-07cc-4396-8d63-4f70646d75f0",
                "a56a5842-1351-4b73-a021-bcd531260cd1",
                0
            ),
            new JobExperienceDto(
                "",
                "62694fb0-07cc-4396-8d63-4f70646d75f0",
                "858febd0-7122-4ea4-b270-a69a4b6a53a4",
                0
            )
        );

        // Arrange
        assertEquals(2, actual.size());
        assertTrue(actual.containsAll(expected));
    }
}
