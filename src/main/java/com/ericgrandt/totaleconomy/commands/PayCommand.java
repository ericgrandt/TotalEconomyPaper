package com.ericgrandt.totaleconomy.commands;

import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.wrappers.BukkitWrapper;
import java.util.concurrent.CompletableFuture;
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

        Player targetPlayer = bukkitWrapper.getPlayerExact(args[0]);
        CompletableFuture.runAsync(() -> onCommandHandler(player, targetPlayer, args[1]));

        return true;
    }

    public void onCommandHandler(Player player, Player targetPlayer, String amountArg) {
        if (targetPlayer == null) {
            player.sendMessage("Invalid player specified");
            return;
        }
        if (!isValidDouble(amountArg)) {
            player.sendMessage("Invalid amount specified");
            return;
        }
        if (player.getUniqueId() == targetPlayer.getUniqueId()) {
            player.sendMessage("You cannot pay yourself");
            return;
        }

        double amount = Double.parseDouble(amountArg);
        if (!economy.has(player, amount)) {
            player.sendMessage("You don't have enough to pay this player");
            return;
        }

        EconomyResponse withdrawResponse = economy.withdrawPlayer(player, amount);
        if (withdrawResponse.type == EconomyResponse.ResponseType.FAILURE) {
            player.sendMessage("Error executing command");
            return;
        }

        EconomyResponse depositResponse =  economy.depositPlayer(targetPlayer, amount);
        if (depositResponse.type == EconomyResponse.ResponseType.FAILURE) {
            player.sendMessage("Error executing command");
            return;
        }

        String formattedAmount = economy.format(amount);
        player.sendMessage(String.format("You sent %s to %s", formattedAmount, targetPlayer.getName()));
        targetPlayer.sendMessage(String.format("You received %s from %s", formattedAmount, player.getName()));
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
