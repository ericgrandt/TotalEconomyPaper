package com.ericgrandt.totaleconomy.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.TestUtils;
import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.BalanceData;
import com.ericgrandt.totaleconomy.data.Database;
import com.ericgrandt.totaleconomy.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BalanceCommandTest {
    @Mock
    private Logger loggerMock;

    @Test
    @Tag("Unit")
    public void onCommand_WithNonPlayerSender_ShouldReturnFalse() {
        // Arrange
        EconomyImpl economyMock = mock(EconomyImpl.class);
        BalanceCommand sut = new BalanceCommand(economyMock);

        // Act
        boolean actual = sut.onCommand(mock(ConsoleCommandSender.class), mock(Command.class), "", null);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void onCommand_WithPlayerSender_ShouldReturnTrue() {
        // Arrange
        double balance = 100;

        CommandSender senderMock = mock(Player.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        doNothing().when(senderMock).sendMessage(String.valueOf(balance));
        when(economyMock.getBalance((OfflinePlayer) senderMock)).thenReturn(balance);
        when(economyMock.format(any(Double.class))).thenReturn(String.valueOf(balance));

        BalanceCommand sut = new BalanceCommand(economyMock);

        // Act
        boolean actual = sut.onCommand(senderMock, mock(Command.class), "", null);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Integration")
    public void onCommand_ShouldSendMessageWithBalanceToPlayer() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();

        Database databaseMock = mock(Database.class);
        CommandSender senderMock = mock(Player.class);
        when(databaseMock.getConnection()).thenReturn(TestUtils.getConnection());
        when(((OfflinePlayer) senderMock).getUniqueId()).thenReturn(
            UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0")
        );

        CurrencyDto defaultCurrency = new CurrencyDto(
            1,
            "Dollar",
            "Dollars",
            "$",
            2,
            true
        );
        AccountData accountData = new AccountData(databaseMock);
        BalanceData balanceData = new BalanceData(databaseMock);
        EconomyImpl economy = new EconomyImpl(loggerMock, true, defaultCurrency, accountData, balanceData);

        BalanceCommand sut = new BalanceCommand(economy);

        // Act
        boolean actual = sut.onCommand(senderMock, mock(Command.class), "", null);

        // Assert
        verify(senderMock).sendMessage("$50.00");
        assertTrue(actual);
    }
}
