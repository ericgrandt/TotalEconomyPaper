package com.ericgrandt.totaleconomy.commands;

import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.wrappers.BukkitWrapper;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PayCommand implements CommandExecutor {
    private final BukkitWrapper bukkitWrapper;
    private final EconomyImpl economy;

    public PayCommand(BukkitWrapper bukkitWrapper, EconomyImpl economy) {
        this.bukkitWrapper = bukkitWrapper;
        this.economy = economy;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can run this command");
            return false;
        }

        Player target = bukkitWrapper.getPlayerExact(args[0]);
        if (target == null) {
            player.sendMessage("Invalid player specified");
            return false;
        }
        if (!isValidDouble(args[1])) {
            player.sendMessage("Invalid amount specified");
            return false;
        }
        if (player.getUniqueId() == target.getUniqueId()) {
            player.sendMessage("You cannot pay yourself");
            return false;
        }

        double amount = Double.parseDouble(args[1]);
        if (!economy.has(player, amount)) {
            player.sendMessage("You don't have enough to pay this player");
            return false;
        }

        EconomyResponse withdrawResponse = economy.withdrawPlayer(player, amount);
        if (withdrawResponse.type == EconomyResponse.ResponseType.FAILURE) {
            player.sendMessage("Error executing command");
            return false;
        }

        EconomyResponse depositResponse =  economy.depositPlayer(target, amount);
        if (depositResponse.type == EconomyResponse.ResponseType.FAILURE) {
            player.sendMessage("Error executing command");
            return false;
        }

        return true;
    }

    private boolean isValidDouble(String amount) {
        try {
            Double.parseDouble(amount);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
