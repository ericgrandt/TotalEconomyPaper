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

import com.ericgrandt.totaleconomy.data.CurrencyData;
import com.ericgrandt.totaleconomy.data.dto.CurrencyDto;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        EconomyImpl sut = new EconomyImpl(loggerMock, true, null);

        // Act
        boolean actual = sut.isEnabled();

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void isEnabled_WhenFalse_ShouldReturnFalse() {
        // Arrange
        EconomyImpl sut = new EconomyImpl(loggerMock, false, null);

        // Act
        boolean actual = sut.isEnabled();

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void getName_ShouldReturnName() {
        // Arrange
        EconomyImpl sut = new EconomyImpl(loggerMock, true, null);

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
        EconomyImpl sut = new EconomyImpl(loggerMock, true, null);

        // Act
        boolean actual = sut.hasBankSupport();

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void fractionalDigits_WithDefaultCurrency_ShouldReturnFractionalDigitsForDefaultCurrency() throws SQLException {
        // Arrange
        CurrencyData currencyDataMock = mock(CurrencyData.class);
        CurrencyDto defaultCurrency = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            1,
            true
        );
        when(currencyDataMock.getDefaultCurrency()).thenReturn(defaultCurrency);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, currencyDataMock);

        // Act
        int actual = sut.fractionalDigits();
        int expected = 1;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void fractionalDigits_WithNullDefaultCurrency_ShouldReturnTwo() throws SQLException {
        // Arrange
        CurrencyData currencyDataMock = mock(CurrencyData.class);
        when(currencyDataMock.getDefaultCurrency()).thenReturn(null);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, currencyDataMock);

        // Act
        int actual = sut.fractionalDigits();
        int expected = 2;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void fractionalDigits_WithCaughtSqlException_ShouldReturnTwo() throws SQLException {
        // Arrange
        CurrencyData currencyDataMock = mock(CurrencyData.class);
        when(currencyDataMock.getDefaultCurrency()).thenThrow(SQLException.class);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, currencyDataMock);

        // Act
        int actual = sut.fractionalDigits();
        int expected = 2;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void fractionalDigits_WithCaughtSqlException_ShouldLogError() throws SQLException {
        // Arrange
        CurrencyData currencyDataMock = mock(CurrencyData.class);
        when(currencyDataMock.getDefaultCurrency()).thenThrow(SQLException.class);

        EconomyImpl sut = new EconomyImpl(loggerMock, true, currencyDataMock);

        // Act
        sut.fractionalDigits();

        // Assert
        verify(loggerMock, times(1)).log(
            eq(Level.SEVERE),
            eq("Error calling getDefaultCurrency"),
            any(SQLException.class)
        );
    }
}
