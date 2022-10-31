package com.ericgrandt.totaleconomy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EconomyImplTest {
    @Test
    @Tag("Unit")
    public void isEnabled_WhenTrue_ShouldReturnTrue() {
        // Arrange
        EconomyImpl sut = new EconomyImpl(true);

        // Act
        boolean actual = sut.isEnabled();

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void isEnabled_WhenFalse_ShouldReturnFalse() {
        // Arrange
        EconomyImpl sut = new EconomyImpl(false);

        // Act
        boolean actual = sut.isEnabled();

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void getName_ShouldReturnName() {
        // Arrange
        EconomyImpl sut = new EconomyImpl(true);

        // Act
        String actual = sut.getName();
        String expected = "Total Economy";

        // Assert
        assertEquals(expected, actual);
    }
}
