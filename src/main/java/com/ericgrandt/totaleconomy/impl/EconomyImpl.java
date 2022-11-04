package com.ericgrandt.totaleconomy.impl;

import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.dto.CurrencyDto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.OfflinePlayer;

public class EconomyImpl implements Economy {
    private final Logger logger;
    private final boolean isEnabled;
    private final CurrencyDto defaultCurrency;
    private final AccountData accountData;

    public EconomyImpl(Logger logger, boolean isEnabled, CurrencyDto defaultCurrency, AccountData accountData) {
        this.logger = logger;
        this.isEnabled = isEnabled;
        this.defaultCurrency = defaultCurrency;
        this.accountData = accountData;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String getName() {
        return "Total Economy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return defaultCurrency.getNumFractionDigits();
    }

    @Override
    public String format(double amount) {
        BigDecimal bigDecimalAmount = BigDecimal.valueOf(amount)
            .setScale(defaultCurrency.getNumFractionDigits(), RoundingMode.DOWN);

        return String.format("%s%s", defaultCurrency.getSymbol(), bigDecimalAmount);
    }

    @Override
    public String currencyNamePlural() {
        return defaultCurrency.getNamePlural();
    }

    @Override
    public String currencyNameSingular() {
        return defaultCurrency.getNameSingular();
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        UUID playerUUID = player.getUniqueId();

        try {
            return accountData.getAccount(playerUUID) != null;
        } catch (SQLException e) {
            logger.log(
                Level.SEVERE,
                String.format("[Total Economy] Error calling getAccount (accountId: %s)", playerUUID),
                e
            );
            return false;
        }
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        throw new NotImplementedException("World specific accounts are not yet supported");
    }


    @Override
    public double getBalance(OfflinePlayer player) {
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        throw new NotImplementedException("World specific accounts are not yet supported");
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        throw new NotImplementedException("World specific accounts are not yet supported");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        throw new NotImplementedException("World specific accounts are not yet supported");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        throw new NotImplementedException("World specific accounts are not yet supported");
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        throw new NotImplementedException("World specific accounts are not yet supported");
    }

    @Override
    public boolean hasAccount(String playerName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getBalance(String playerName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getBalance(String playerName, String world) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean has(String playerName, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        throw new UnsupportedOperationException();
    }
}
