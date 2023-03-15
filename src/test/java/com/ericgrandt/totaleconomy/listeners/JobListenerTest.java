package com.ericgrandt.totaleconomy.listeners;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.bukkit.entity.Player;
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

    @Mock
    private EconomyImpl economyMock;

    @Mock
    private JobService jobServiceMock;

    @Test
    @Tag("Unit")
    public void onBreakActionHandler_WithJobRewardFound_ShouldAddRewards() {
        // Arrange
        JobRewardDto jobRewardDto = new JobRewardDto("", UUID.randomUUID().toString(), "", 1, "", BigDecimal.TEN, 1);
        AddExperienceResult addExperienceResult = new AddExperienceResult("", 1, false);

        when(jobServiceMock.getJobReward(anyString(), anyString())).thenReturn(jobRewardDto);
        when(jobServiceMock.addExperience(any(), any(), anyInt())).thenReturn(addExperienceResult);

        JobListener sut = new JobListener(economyMock, jobServiceMock);

        // Act
        sut.onBreakActionHandler("stone", mock(Player.class));

        // Assert
        verify(economyMock, times(1)).depositPlayer(any(Player.class), anyDouble());
    }

    @Test
    @Tag("Unit")
    public void onBreakActionHandler_WithNoJobRewardFound_ShouldNotAddRewards() {
        // Arrange
        when(jobServiceMock.getJobReward(anyString(), anyString())).thenReturn(null);

        JobListener sut = new JobListener(economyMock, jobServiceMock);

        // Act
        sut.onBreakActionHandler("stone", mock(Player.class));

        // Assert
        verify(economyMock, times(0)).depositPlayer(any(Player.class), anyDouble());
    }

    @Test
    @Tag("Unit")
    public void onBreakActionHandler_WithLevelUp_ShouldSendMessage() {
        // Arrange
        JobRewardDto jobRewardDto = new JobRewardDto("", UUID.randomUUID().toString(), "", 1, "", BigDecimal.TEN, 1);
        AddExperienceResult addExperienceResult = new AddExperienceResult("", 1, true);

        Player playerMock = mock(Player.class);
        when(jobServiceMock.getJobReward(anyString(), anyString())).thenReturn(jobRewardDto);
        when(jobServiceMock.addExperience(any(), any(), anyInt())).thenReturn(addExperienceResult);

        JobListener sut = new JobListener(economyMock, jobServiceMock);

        // Act
        sut.onBreakActionHandler("stone", playerMock);

        // Assert
        verify(playerMock, times(1)).sendMessage(any(Component.class));
    }

    @Test
    @Tag("Unit")
    public void onBreakActionHandler_WithNoLevelUp_ShouldNotSendMessage() {
        // Arrange
        JobRewardDto jobRewardDto = new JobRewardDto("", UUID.randomUUID().toString(), "", 1, "", BigDecimal.TEN, 1);
        AddExperienceResult addExperienceResult = new AddExperienceResult("", 1, false);

        Player playerMock = mock(Player.class);
        when(jobServiceMock.getJobReward(anyString(), anyString())).thenReturn(jobRewardDto);
        when(jobServiceMock.addExperience(any(), any(), anyInt())).thenReturn(addExperienceResult);

        JobListener sut = new JobListener(economyMock, jobServiceMock);

        // Act
        sut.onBreakActionHandler("stone", playerMock);

        // Assert
        verify(playerMock, times(0)).sendMessage(any(Component.class));
    }

    @Test
    @Tag("Unit")
    public void createJobExperienceOnPlayerJoin_ShouldCallTheJobService() {
        // Arrange
        UUID playerId = UUID.randomUUID();

        Player playerMock = mock(Player.class);
        JobService jobServiceMock = mock(JobService.class);
        when(playerMock.getUniqueId()).thenReturn(playerId);

        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(playerMock, Component.empty());
        JobListener sut = new JobListener(mock(EconomyImpl.class), jobServiceMock);

        // Act
        sut.createJobExperienceOnPlayerJoin(playerJoinEvent);

        // Assert
        verify(jobServiceMock, times(1)).createJobExperienceForAccount(playerId);
    }

    @Test
    @Tag("Integration")
    public void onBreakActionHandler_WithJobReward_ShouldRewardExperienceAndMoney() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedDefaultBalances();
        TestUtils.seedAccounts();
        TestUtils.seedJobs();
        TestUtils.seedJobActions();
        TestUtils.seedJobRewards();
        TestUtils.seedJobExperience();

        CurrencyDto currencyDto = new CurrencyDto(1, "", "", "", 0, true);
        Database databaseMock = mock(Database.class);
        Player playerMock = mock(Player.class);
        UUID playerId = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).then(x -> TestUtils.getConnection());
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

        JobListener sut = new JobListener(economy, jobService);

        // Act
        sut.onBreakActionHandler("coal_ore", playerMock);

        // Assert
        BalanceDto actualBalance = TestUtils.getBalanceForAccountId(playerId, 1);
        BalanceDto expectedBalance = new BalanceDto(
            "ab661384-11f5-41e1-a5e6-6fa93305d4d1",
            "62694fb0-07cc-4396-8d63-4f70646d75f0",
            1,
            BigDecimal.valueOf(50.50).setScale(2, RoundingMode.DOWN)
        );
        JobExperienceDto actualExperience = TestUtils.getExperienceForJob(
            playerId,
            UUID.fromString("a56a5842-1351-4b73-a021-bcd531260cd1")
        );
        JobExperienceDto expectedExperience = new JobExperienceDto(
            "748af95b-32a0-45c2-bfdc-9e87c023acdf",
            "62694fb0-07cc-4396-8d63-4f70646d75f0",
            "a56a5842-1351-4b73-a021-bcd531260cd1",
            51
        );

        assertEquals(expectedBalance, actualBalance);
        assertEquals(expectedExperience, actualExperience);
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
        PlayerJoinEvent event = new PlayerJoinEvent(playerMock, Component.empty());
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
