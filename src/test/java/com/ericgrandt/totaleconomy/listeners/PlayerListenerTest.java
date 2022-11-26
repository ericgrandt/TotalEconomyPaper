package com.ericgrandt.totaleconomy.listeners;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.TestUtils;
import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.BalanceData;
import com.ericgrandt.totaleconomy.data.Database;
import com.ericgrandt.totaleconomy.data.dto.AccountDto;
import com.ericgrandt.totaleconomy.data.dto.BalanceDto;
import com.ericgrandt.totaleconomy.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PlayerListenerTest {
    @Mock
    private Logger loggerMock;

    @Test
    @Tag("Unit")
    public void onPlayerJoin_WithAccountAlreadyExisting_ShouldNotCallCreateAccount() {
        // Arrange
        Player playerMock = mock(Player.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        when(economyMock.hasAccount(playerMock)).thenReturn(true);

        PlayerJoinEvent event = new PlayerJoinEvent(playerMock, "");
        PlayerListener sut = new PlayerListener(economyMock);

        // Act
        sut.onPlayerJoin(event);

        // Assert
        verify(economyMock, times(0)).createPlayerAccount(any(Player.class));
    }

    @Test
    @Tag("Unit")
    public void onPlayerJoin_WithNoAccountAlreadyExisting_ShouldCallCreateAccount() {
        // Arrange
        Player playerMock = mock(Player.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        when(economyMock.hasAccount(playerMock)).thenReturn(false);

        PlayerJoinEvent event = new PlayerJoinEvent(playerMock, "");
        PlayerListener sut = new PlayerListener(economyMock);

        // Act
        sut.onPlayerJoin(event);

        // Assert
        verify(economyMock, times(1)).createPlayerAccount(playerMock);
    }

    @Test
    @Tag("Integration")
    public void onPlayerJoin_ShouldCreateNewAccountAndBalances() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedDefaultBalances();
        TestUtils.seedAccounts();

        CurrencyDto currencyMock = mock(CurrencyDto.class);
        BalanceData balanceDataMock = mock(BalanceData.class);
        Database databaseMock = mock(Database.class);
        Player playerMock = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(playerMock.getUniqueId()).thenReturn(playerId);
        when(databaseMock.getConnection()).then(x -> TestUtils.getConnection());

        AccountData accountData = new AccountData(databaseMock);
        EconomyImpl economy = new EconomyImpl(loggerMock, true, currencyMock, accountData, balanceDataMock);
        PlayerJoinEvent event = new PlayerJoinEvent(playerMock, "");
        PlayerListener sut = new PlayerListener(economy);

        // Act
        sut.onPlayerJoin(event);

        AccountDto actualAccount = TestUtils.getAccount(playerId);
        AccountDto expectedAccount = new AccountDto(
            playerId.toString(),
            actualAccount.getCreated()
        );

        BalanceDto actualBalance = TestUtils.getBalanceForAccountId(playerId, 1);
        BalanceDto expectedBalance = new BalanceDto(
            actualBalance.getId(),
            playerId.toString(),
            1,
            BigDecimal.valueOf(100.50).setScale(2, RoundingMode.DOWN)
        );

        // Assert
        assertEquals(expectedAccount, actualAccount);
        assertEquals(expectedBalance, actualBalance);
    }
}
