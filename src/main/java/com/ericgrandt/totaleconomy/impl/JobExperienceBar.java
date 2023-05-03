package com.ericgrandt.totaleconomy.impl;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class JobExperienceBar {
    private final Player player;
    private final BossBar bossBar;

    public JobExperienceBar(Player player) {
        this.player = player;
        this.bossBar = BossBar.bossBar(Component.text("Hello"), 0.0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
    }

    public void show() {
        player.showBossBar(bossBar);
    }

    // public void show() {
    //     experienceBar.setVisible(true);
    //     // TODO: Schedule to close bar using hide()
    // }
    //
    // public void hide() {
    //     experienceBar.setVisible(false);
    // }
}
