package com.ericgrandt.totaleconomy.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.wrappers.BukkitWrapper;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PayCommandTest {
    @Test
    @Tag("Unit")
    public void onCommand_WithNonPlayerSender_ShouldReturnFalse() {
        // Arrange
        BukkitWrapper bukkitWrapperMock = mock(BukkitWrapper.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        PayCommand sut = new PayCommand(bukkitWrapperMock, economyMock);

        // Act
        String[] args = {"", ""};
        boolean actual = sut.onCommand(mock(ConsoleCommandSender.class), mock(Command.class), "pay", args);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void onCommand_WithSuccessfulPay_ShouldReturnTrue() {
        // Arrange
        BukkitWrapper bukkitWrapperMock = mock(BukkitWrapper.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        Player playerMock = mock(Player.class);
        Player targetMock = mock(Player.class);
        when(bukkitWrapperMock.getPlayerExact("playerName")).thenReturn(targetMock);
        when(economyMock.has(playerMock, 100)).thenReturn(true);
        when(economyMock.withdrawPlayer(playerMock, 100)).thenReturn(mock(EconomyResponse.class));
        when(economyMock.depositPlayer(targetMock, 100)).thenReturn(mock(EconomyResponse.class));

        PayCommand sut = new PayCommand(bukkitWrapperMock, economyMock);

        // Act
        String[] args = {"playerName", "100"};
        boolean actual = sut.onCommand(playerMock, mock(Command.class), "pay", args);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void onCommand_WithoutBalance_ShouldReturnFalse() {
        // Arrange
        BukkitWrapper bukkitWrapperMock = mock(BukkitWrapper.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        Player playerMock = mock(Player.class);

        PayCommand sut = new PayCommand(bukkitWrapperMock, economyMock);

        // Act
        String[] args = {"playerName", "100"};
        boolean actual = sut.onCommand(playerMock, mock(Command.class), "pay", args);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void onCommand_WithMissingArgs_ShouldReturnFalse() {
        // Arrange
        BukkitWrapper bukkitWrapperMock = mock(BukkitWrapper.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        Player playerMock = mock(Player.class);

        PayCommand sut = new PayCommand(bukkitWrapperMock, economyMock);

        // Act
        String[] args = {};
        boolean actual = sut.onCommand(playerMock, mock(Command.class), "pay", args);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void onCommand_WithNullPlayerArg_ShouldReturnFalse() {
        // Arrange
        BukkitWrapper bukkitWrapperMock = mock(BukkitWrapper.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        Player playerMock = mock(Player.class);

        PayCommand sut = new PayCommand(bukkitWrapperMock, economyMock);

        // Act
        String[] args = {null, "100"};
        boolean actual = sut.onCommand(playerMock, mock(Command.class), "pay", args);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void onCommand_WithNullAmountArg_ShouldReturnFalse() {
        // Arrange
        BukkitWrapper bukkitWrapperMock = mock(BukkitWrapper.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        Player playerMock = mock(Player.class);

        PayCommand sut = new PayCommand(bukkitWrapperMock, economyMock);

        // Act
        String[] args = {"playerName", null};
        boolean actual = sut.onCommand(playerMock, mock(Command.class), "pay", args);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void onCommand_WithInvalidPlayerArg_ShouldReturnFalse() {
        // Arrange
        BukkitWrapper bukkitWrapperMock = mock(BukkitWrapper.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        Player playerMock = mock(Player.class);
        when(bukkitWrapperMock.getPlayerExact("playerName")).thenReturn(null);

        PayCommand sut = new PayCommand(bukkitWrapperMock, economyMock);

        // Act
        String[] args = {"playerName", "100"};
        boolean actual = sut.onCommand(playerMock, mock(Command.class), "pay", args);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void onCommand_WithInvalidAmountArg_ShouldReturnFalse() {
        // Arrange
        BukkitWrapper bukkitWrapperMock = mock(BukkitWrapper.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        Player playerMock = mock(Player.class);
        Player targetMock = mock(Player.class);
        when(bukkitWrapperMock.getPlayerExact("playerName")).thenReturn(targetMock);

        PayCommand sut = new PayCommand(bukkitWrapperMock, economyMock);

        // Act
        String[] args = {"playerName", "asdf"};
        boolean actual = sut.onCommand(playerMock, mock(Command.class), "pay", args);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void onCommand_WithFailedWithdraw_ShouldReturnFalse() {
        // Arrange
        BukkitWrapper bukkitWrapperMock = mock(BukkitWrapper.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        Player playerMock = mock(Player.class);
        Player targetMock = mock(Player.class);
        EconomyResponse withdrawResponse = new EconomyResponse(
            100,
            100,
            EconomyResponse.ResponseType.FAILURE,
            ""
        );
        when(bukkitWrapperMock.getPlayerExact("playerName")).thenReturn(targetMock);
        when(economyMock.has(playerMock, 100)).thenReturn(true);
        when(economyMock.withdrawPlayer(playerMock, 100)).thenReturn(withdrawResponse);

        PayCommand sut = new PayCommand(bukkitWrapperMock, economyMock);

        // Act
        String[] args = {"playerName", "100"};
        boolean actual = sut.onCommand(playerMock, mock(Command.class), "pay", args);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void onCommand_WithFailedDeposit_ShouldReturnFalse() {
        // Arrange
        BukkitWrapper bukkitWrapperMock = mock(BukkitWrapper.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        Player playerMock = mock(Player.class);
        Player targetMock = mock(Player.class);
        EconomyResponse depositResponse = new EconomyResponse(
            100,
            100,
            EconomyResponse.ResponseType.FAILURE,
            ""
        );
        when(bukkitWrapperMock.getPlayerExact("playerName")).thenReturn(targetMock);
        when(economyMock.has(playerMock, 100)).thenReturn(true);
        when(economyMock.withdrawPlayer(playerMock, 100)).thenReturn(mock(EconomyResponse.class));
        when(economyMock.depositPlayer(targetMock, 100)).thenReturn(depositResponse);

        PayCommand sut = new PayCommand(bukkitWrapperMock, economyMock);

        // Act
        String[] args = {"playerName", "100"};
        boolean actual = sut.onCommand(playerMock, mock(Command.class), "pay", args);

        // Assert
        assertFalse(actual);
    }
}
