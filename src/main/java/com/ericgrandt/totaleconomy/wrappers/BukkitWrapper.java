package com.ericgrandt.totaleconomy.wrappers;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BukkitWrapper {
    public Player getPlayerExact(String name) {
        return Bukkit.getPlayerExact(name);
    }

    public BossBar createBossBar(
        String title,
        @NotNull BarColor color,
        @NotNull BarStyle style,
        @NotNull BarFlag... flags
    ) {
        return Bukkit.createBossBar(title, color, style, flags);
    }
}
