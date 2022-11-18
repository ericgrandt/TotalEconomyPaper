package com.ericgrandt.totaleconomy.listeners;

import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlayerListenerTest {
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
}
