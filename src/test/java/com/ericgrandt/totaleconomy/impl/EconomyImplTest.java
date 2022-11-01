package com.ericgrandt.totaleconomy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ericgrandt.totaleconomy.data.dto.CurrencyDto;
import java.sql.SQLException;
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
    public void fractionalDigits_WithDefaultCurrency_ShouldReturnFractionalDigits() throws SQLException {
        // Arrange
        CurrencyDto defaultCurrency = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            1,
            true
        );

        EconomyImpl sut = new EconomyImpl(loggerMock, true, defaultCurrency);

        // Act
        int actual = sut.fractionalDigits();
        int expected = 1;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void format_WithDefaultCurrency_ShouldReturnFormattedAmountWithSymbol() throws SQLException {
        // Arrange
        CurrencyDto defaultCurrency = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            2,
            true
        );

        EconomyImpl sut = new EconomyImpl(loggerMock, true, defaultCurrency);

        // Act
        String actual = sut.format(123.45);
        String expected = "$123.45";

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void format_WithDefaultCurrencyHavingOneFractionalDigit_ShouldReturnFormattedAmountWithOneDigit() throws SQLException {
        // Arrange
        CurrencyDto defaultCurrency = new CurrencyDto(
            1,
            "singular",
            "plural",
            "$",
            1,
            true
        );

        EconomyImpl sut = new EconomyImpl(loggerMock, true, defaultCurrency);

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

        EconomyImpl sut = new EconomyImpl(loggerMock, true, defaultCurrency);

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

        EconomyImpl sut = new EconomyImpl(loggerMock, true, defaultCurrency);

        // Act
        String actual = sut.currencyNameSingular();
        String expected = "singular";

        // Assert
        assertEquals(expected, actual);
    }
}
