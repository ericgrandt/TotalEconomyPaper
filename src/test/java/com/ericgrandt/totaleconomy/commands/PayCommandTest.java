package com.ericgrandt.totaleconomy.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.TestUtils;
import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.BalanceData;
import com.ericgrandt.totaleconomy.data.Database;
import com.ericgrandt.totaleconomy.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.wrappers.BukkitWrapper;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.EconomyResponse;
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
public class PayCommandTest {
    @Mock
    private Logger loggerMock;

    private final CurrencyDto defaultCurrency = new CurrencyDto(
        1,
        "singular",
        "plural",
        "$",
        2,
        true
    );

    @Test
    @Tag("Unit")
    public void onCommand_WithNonPlayerSender_ShouldSendMessageAndReturnFalse() {
        // Arrange
        BukkitWrapper bukkitWrapperMock = mock(BukkitWrapper.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        CommandSender senderMock = mock(ConsoleCommandSender.class);
        PayCommand sut = new PayCommand(bukkitWrapperMock, economyMock);

        // Act
        String[] args = {"", ""};
        boolean actual = sut.onCommand(senderMock, mock(Command.class), "pay", args);

        // Assert
        verify(senderMock).sendMessage("Only players can run this command");
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void onCommand_WithLessThanTwoArgs_ShouldReturnFalse() {
        // Arrange
        BukkitWrapper bukkitWrapperMock = mock(BukkitWrapper.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);

        PayCommand sut = new PayCommand(bukkitWrapperMock, economyMock);

        // Act
        String[] args = {"playerName"};
        boolean actual = sut.onCommand(
            mock(Player.class),
            mock(Command.class),
            "pay",
            args
        );

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithAmountContainingTooManyDecimalPlaces_ShouldCallWithdrawWithScaledAmount() {
        // Arrange
        EconomyImpl economyMock = mock(EconomyImpl.class);
        Player playerMock = mock(Player.class);
        Player targetMock = mock(Player.class);
        when(economyMock.getDefaultCurrency()).thenReturn(defaultCurrency);
        when(economyMock.has(playerMock, 100.01)).thenReturn(true);
        when(economyMock.withdrawPlayer(any(Player.class), anyDouble())).thenReturn(new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, ""));
        when(economyMock.depositPlayer(any(Player.class), anyDouble())).thenReturn(new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, ""));
        when(playerMock.getUniqueId()).thenReturn(UUID.randomUUID());
        when(targetMock.getUniqueId()).thenReturn(UUID.randomUUID());

        PayCommand sut = new PayCommand(mock(BukkitWrapper.class), economyMock);

        // Act
        sut.onCommandHandler(
            playerMock,
            targetMock,
            "100.0191"
        );

        // Assert
        verify(economyMock).withdrawPlayer(playerMock, 100.01);
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithAmountContainingTooLittleDecimalPlaces_ShouldCallWithdrawWithScaledAmount() {
        // Arrange
        EconomyImpl economyMock = mock(EconomyImpl.class);
        Player playerMock = mock(Player.class);
        Player targetMock = mock(Player.class);
        when(economyMock.getDefaultCurrency()).thenReturn(defaultCurrency);
        when(economyMock.has(playerMock, 100.10)).thenReturn(true);
        when(economyMock.withdrawPlayer(any(Player.class), anyDouble())).thenReturn(new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, ""));
        when(economyMock.depositPlayer(any(Player.class), anyDouble())).thenReturn(new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, ""));
        when(playerMock.getUniqueId()).thenReturn(UUID.randomUUID());
        when(targetMock.getUniqueId()).thenReturn(UUID.randomUUID());

        PayCommand sut = new PayCommand(mock(BukkitWrapper.class), economyMock);

        // Act
        sut.onCommandHandler(
            playerMock,
            targetMock,
            "100.1"
        );

        // Assert
        verify(economyMock).withdrawPlayer(playerMock, 100.10);
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithAmountLessThanZero_ShouldReturnFalse() {
        // Arrange
        EconomyImpl economyMock = mock(EconomyImpl.class);
        Player playerMock = mock(Player.class);
        Player targetMock = mock(Player.class);
        when(economyMock.getDefaultCurrency()).thenReturn(defaultCurrency);
        when(playerMock.getUniqueId()).thenReturn(UUID.randomUUID());
        when(targetMock.getUniqueId()).thenReturn(UUID.randomUUID());

        PayCommand sut = new PayCommand(mock(BukkitWrapper.class), economyMock);

        // Act
        sut.onCommandHandler(playerMock, targetMock, "-10");

        // Assert
        verify(playerMock).sendMessage("Amount must be more than 0");
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithoutEnoughBalance_ShouldReturnFalse() {
        // Arrange
        BukkitWrapper bukkitWrapperMock = mock(BukkitWrapper.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        Player playerMock = mock(Player.class);
        Player targetMock = mock(Player.class);
        when(economyMock.getDefaultCurrency()).thenReturn(defaultCurrency);
        when(economyMock.has(playerMock, 100)).thenReturn(false);
        when(playerMock.getUniqueId()).thenReturn(UUID.randomUUID());
        when(targetMock.getUniqueId()).thenReturn(UUID.randomUUID());

        PayCommand sut = new PayCommand(bukkitWrapperMock, economyMock);

        // Act
        sut.onCommandHandler(playerMock, targetMock, "100");

        // Assert
        verify(playerMock).sendMessage("You don't have enough to pay this player");
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithInvalidPlayerArg_ShouldReturnFalse() {
        // Arrange
        Player playerMock = mock(Player.class);

        PayCommand sut = new PayCommand(
            mock(BukkitWrapper.class),
            mock(EconomyImpl.class)
        );

        // Act
        sut.onCommandHandler(playerMock, null, "100");

        // Assert
        verify(playerMock).sendMessage("Invalid player specified");
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithInvalidAmountArg_ShouldSendMessage() {
        // Arrange
        Player playerMock = mock(Player.class);

        PayCommand sut = new PayCommand(
            mock(BukkitWrapper.class),
            mock(EconomyImpl.class)
        );

        // Act
        sut.onCommandHandler(playerMock, mock(Player.class), "not a double");

        // Assert
        verify(playerMock).sendMessage("Invalid amount specified");
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithFailedWithdraw_ShouldSendMessage() {
        // Arrange
        EconomyImpl economyMock = mock(EconomyImpl.class);
        Player playerMock = mock(Player.class);
        Player targetMock = mock(Player.class);
        EconomyResponse withdrawResponse = new EconomyResponse(
            100,
            100,
            EconomyResponse.ResponseType.FAILURE,
            ""
        );
        when(economyMock.getDefaultCurrency()).thenReturn(defaultCurrency);
        when(economyMock.has(playerMock, 100)).thenReturn(true);
        when(economyMock.withdrawPlayer(playerMock, 100)).thenReturn(withdrawResponse);
        when(playerMock.getUniqueId()).thenReturn(UUID.randomUUID());
        when(targetMock.getUniqueId()).thenReturn(UUID.randomUUID());

        PayCommand sut = new PayCommand(mock(BukkitWrapper.class), economyMock);

        // Act
        sut.onCommandHandler(playerMock, targetMock, "100");

        // Assert
        verify(playerMock).sendMessage("Error executing command");
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithFailedDeposit_ShouldSendMessage() {
        // Arrange
        EconomyImpl economyMock = mock(EconomyImpl.class);
        Player playerMock = mock(Player.class);
        Player targetMock = mock(Player.class);
        EconomyResponse depositResponse = new EconomyResponse(
            100,
            100,
            EconomyResponse.ResponseType.FAILURE,
            ""
        );
        when(economyMock.getDefaultCurrency()).thenReturn(defaultCurrency);
        when(economyMock.has(playerMock, 100)).thenReturn(true);
        when(economyMock.withdrawPlayer(playerMock, 100)).thenReturn(mock(EconomyResponse.class));
        when(economyMock.depositPlayer(targetMock, 100)).thenReturn(depositResponse);
        when(playerMock.getUniqueId()).thenReturn(UUID.randomUUID());
        when(targetMock.getUniqueId()).thenReturn(UUID.randomUUID());

        PayCommand sut = new PayCommand(mock(BukkitWrapper.class), economyMock);

        // Act
        sut.onCommandHandler(playerMock, targetMock, "100");

        // Assert
        verify(playerMock).sendMessage("Error executing command");
    }

    @Test
    @Tag("Unit")
    public void onCommandHandler_WithSameSenderAndTarget_ShouldSendMessage() {
        // Arrange
        Player playerMock = mock(Player.class);
        when(playerMock.getUniqueId()).thenReturn(UUID.randomUUID());

        PayCommand sut = new PayCommand(
            mock(BukkitWrapper.class),
            mock(EconomyImpl.class)
        );

        // Act
        sut.onCommandHandler(playerMock, playerMock, "100");

        // Assert
        verify(playerMock).sendMessage("You cannot pay yourself");
    }

    @Test
    @Tag("Integration")
    public void onCommandHandler_ShouldTransferMoney() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();

        UUID playerUUID = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");
        UUID targetUUID = UUID.fromString("551fe9be-f77f-4bcb-81db-548db6e77aea");

        Player playerMock = mock(Player.class);
        String playerName = "Player 1";
        when(playerMock.getUniqueId()).thenReturn(playerUUID);
        when(playerMock.getName()).thenReturn(playerName);

        Player targetMock = mock(Player.class);
        String targetName = "Player 2";
        when(targetMock.getUniqueId()).thenReturn(targetUUID);
        when(targetMock.getName()).thenReturn(targetName);

        Database databaseMock = mock(Database.class);
        when(databaseMock.getDataSource()).thenReturn(mock(HikariDataSource.class));
        when(databaseMock.getDataSource().getConnection()).then(x -> TestUtils.getConnection());

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

        PayCommand sut = new PayCommand(
            mock(BukkitWrapper.class),
            new EconomyImpl(loggerMock, true, defaultCurrency, accountData, balanceData)
        );

        // Act
        sut.onCommandHandler(playerMock, targetMock, String.valueOf(25));

        BigDecimal actualPlayerBalance = balanceData.getBalance(playerUUID, 1);
        BigDecimal expectedPlayerBalance = BigDecimal.valueOf(25.00).setScale(2, RoundingMode.DOWN);
        BigDecimal actualTargetBalance = balanceData.getBalance(targetUUID, 1);
        BigDecimal expectedTargetBalance = BigDecimal.valueOf(125.00).setScale(2, RoundingMode.DOWN);

        // Assert
        assertEquals(expectedPlayerBalance, actualPlayerBalance);
        assertEquals(expectedTargetBalance, actualTargetBalance);

        verify(playerMock, times(1)).sendMessage(
            String.format("You sent $25.00 to %s", targetName)
        );
        verify(targetMock, times(1)).sendMessage(
            String.format("You received $25.00 from %s", playerName)
        );
    }
}
