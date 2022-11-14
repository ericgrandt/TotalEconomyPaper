package com.ericgrandt.totaleconomy.commands;

import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.wrappers.BukkitWrapper;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
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
        if (!hasArguments(args) || !(sender instanceof Player player)) {
            return false;
        }

        Player target = bukkitWrapper.getPlayerExact(args[0]);
        if (target == null || !isValidDouble(args[1])) {
            return false;
        }

        double amount = Double.parseDouble(args[1]);
        if (!economy.has(player, amount)) {
            return false;
        }

        EconomyResponse withdrawResponse = economy.withdrawPlayer((OfflinePlayer) sender, amount);
        if (withdrawResponse.type == EconomyResponse.ResponseType.FAILURE) {
            return false;
        }

        EconomyResponse depositResponse =  economy.depositPlayer(target, amount);
        return depositResponse.type != EconomyResponse.ResponseType.FAILURE;
    }

    private boolean hasArguments(String[] args) {
        return args.length == 2 && args[0] != null && args[1] != null;
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
