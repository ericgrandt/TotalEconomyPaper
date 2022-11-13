package com.ericgrandt.totaleconomy.commands;

import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BalanceCommand implements CommandExecutor {
    private final EconomyImpl economy;

    public BalanceCommand(EconomyImpl economy) {
        this.economy = economy;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            double balance = economy.getBalance(player);
            player.sendMessage(economy.format(balance));
            return true;
        }

        return false;
    }
}
