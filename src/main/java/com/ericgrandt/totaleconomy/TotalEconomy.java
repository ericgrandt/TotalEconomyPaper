package com.ericgrandt.totaleconomy;

import com.ericgrandt.totaleconomy.commands.BalanceCommand;
import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.BalanceData;
import com.ericgrandt.totaleconomy.data.CurrencyData;
import com.ericgrandt.totaleconomy.data.Database;
import com.ericgrandt.totaleconomy.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class TotalEconomy extends JavaPlugin implements Listener {
    private final FileConfiguration config = getConfig();
    private final Logger logger = Logger.getLogger("Minecraft");

    @Override
    public void onEnable() {
        Database database = new Database(
            config.getString("database.url"),
            config.getString("database.user"),
            config.getString("database.password")
        );
        CurrencyData currencyData = new CurrencyData(database);
        CurrencyDto defaultCurrency;

        try {
            defaultCurrency = currencyData.getDefaultCurrency();
        } catch (SQLException e) {
            logger.log(
                Level.SEVERE,
                "[Total Economy] Unable to load default currency",
                e
            );
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        AccountData accountData = new AccountData(database);
        BalanceData balanceData = new BalanceData(database);
        EconomyImpl economy = new EconomyImpl(logger, this.isEnabled(), defaultCurrency, accountData, balanceData);

        getServer().getServicesManager().register(
            Economy.class,
            economy,
            this,
            ServicePriority.Normal
        );
        this.getCommand("balance").setExecutor(new BalanceCommand(economy));
    }
}
