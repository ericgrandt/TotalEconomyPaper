package com.ericgrandt.totaleconomy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.dto.AccountDto;
import com.ericgrandt.totaleconomy.data.dto.CurrencyDto;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EconomyImplTest {
    @Mock
    private Logger loggerMock;

    @Test
    @Tag("Unit")
    public void isEnabled_WhenTrue_ShouldReturnTrue() {
        // Arrange
        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, null);

        // Act
        boolean actual = sut.isEnabled();

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void isEnabled_WhenFalse_ShouldReturnFalse() {
        // Arrange
        EconomyImpl sut = new EconomyImpl(loggerMock, false, null, null);

        // Act
        boolean actual = sut.isEnabled();

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void getName_ShouldReturnName() {
        // Arrange
        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, null);

        // Act
        String actual = sut.getName();
        String expected = "Total Economy";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void hasBankSupport_ShouldReturnFalse() {
        // Arrange
        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, null);

        // Act
        boolean actual = sut.hasBankSupport();

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void fractionalDigits_WithDefaultCurrency_ShouldReturnFractionalDigits() {
        // Arrange
        CurrencyDto defaultCurrency = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            1,
            true
        );

        EconomyImpl sut = new EconomyImpl(loggerMock, true, defaultCurrency, null);

        // Act
        int actual = sut.fractionalDigits();
        int expected = 1;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void format_WithDefaultCurrency_ShouldReturnFormattedAmountWithSymbol() {
        // Arrange
        CurrencyDto defaultCurrency = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            2,
            true
        );

        EconomyImpl sut = new EconomyImpl(loggerMock, true, defaultCurrency, null);

        // Act
        String actual = sut.format(123.45);
        String expected = "$123.45";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void format_WithDefaultCurrencyHavingOneFractionalDigit_ShouldReturnFormattedAmountWithOneDigit() {
        // Arrange
        CurrencyDto defaultCurrency = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            1,
            true
        );

        EconomyImpl sut = new EconomyImpl(loggerMock, true, defaultCurrency, null);

        // Act
        String actual = sut.format(123.45);
        String expected = "$123.4";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void currencyNamePlural_WithDefaultCurrency_ShouldReturnNamePlural() {
        // Arrange
        CurrencyDto defaultCurrency = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            1,
            true
        );

        EconomyImpl sut = new EconomyImpl(loggerMock, true, defaultCurrency, null);

        // Act
        String actual = sut.currencyNamePlural();
        String expected = "plural";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void currencyNameSingular_WithDefaultCurrency_ShouldReturnNameSingular() {
        // Arrange
        CurrencyDto defaultCurrency = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            1,
            true
        );

        EconomyImpl sut = new EconomyImpl(loggerMock, true, defaultCurrency, null);

        // Act
        String actual = sut.currencyNameSingular();
        String expected = "singular";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithPlayerHavingAnAccount_ShouldReturnTrue() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);

        AccountData accountDataMock = mock(AccountData.class);
        when(accountDataMock.getAccount(playerUUID)).thenReturn(mock(AccountDto.class));

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock);

        // Act
        boolean actual = sut.hasAccount(playerMock);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithPlayerNotHavingAnAccount_ShouldReturnFalse() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);

        AccountData accountDataMock = mock(AccountData.class);
        when(accountDataMock.getAccount(playerUUID)).thenReturn(null);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock);

        // Act
        boolean actual = sut.hasAccount(playerMock);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithSqlException_ShouldReturnFalse() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);

        AccountData accountDataMock = mock(AccountData.class);
        when(accountDataMock.getAccount(playerUUID)).thenThrow(SQLException.class);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock);

        // Act
        boolean actual = sut.hasAccount(playerMock);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void hasAccount_WithSqlException_ShouldLogException() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);

        AccountData accountDataMock = mock(AccountData.class);
        when(accountDataMock.getAccount(playerUUID)).thenThrow(SQLException.class);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock);

        // Act
        sut.hasAccount(playerMock);

        // Assert
        verify(loggerMock, times(1)).log(
            eq(Level.SEVERE),
            eq(String.format("[Total Economy] Error calling getAccount (accountId: %s)", playerUUID)),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void createPlayerAccount_WithSuccessfulCallToCreateAccount_ShouldReturnTrue() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);

        AccountData accountDataMock = mock(AccountData.class);
        when(accountDataMock.createAccount(playerUUID, 1)).thenReturn(true);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock);

        // Act
        boolean actual = sut.createPlayerAccount(playerMock);

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void createPlayerAccount_WithSqlException_ShouldReturnFalse() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);

        AccountData accountDataMock = mock(AccountData.class);
        when(accountDataMock.createAccount(playerUUID, 1)).thenThrow(SQLException.class);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock);

        // Act
        boolean actual = sut.createPlayerAccount(playerMock);

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void createPlayerAccount_WithSqlException_ShouldLogException() throws SQLException {
        // Arrange
        UUID playerUUID = UUID.randomUUID();
        OfflinePlayer playerMock = mock(OfflinePlayer.class);
        when(playerMock.getUniqueId()).thenReturn(playerUUID);

        AccountData accountDataMock = mock(AccountData.class);
        when(accountDataMock.createAccount(playerUUID, 1)).thenThrow(SQLException.class);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, null, accountDataMock);

        // Act
        sut.createPlayerAccount(playerMock);

        // Assert
        verify(loggerMock, times(1)).log(
            eq(Level.SEVERE),
            eq(String.format(
                "[Total Economy] Error calling createAccount (accountId: %s, currencyId: %s)",
                playerUUID,
                1
            )),
            any(SQLException.class)
        );
    }
}
