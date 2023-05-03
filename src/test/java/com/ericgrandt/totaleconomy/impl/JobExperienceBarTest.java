package com.ericgrandt.totaleconomy.impl;

import com.ericgrandt.totaleconomy.wrappers.BukkitWrapper;
import net.kyori.adventure.audience.Audience;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

public class JobExperienceBarTest {
    private final BukkitWrapper bukkitWrapper = new BukkitWrapper();

    @Test
    @Tag("Unit")
    public void show_ShouldMakeBarVisible() {
        // Arrange
        Player player = mock(Player.class);
        Audience blah = player;

        // Act

        // Assert
    }
}
