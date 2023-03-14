package com.ericgrandt.totaleconomy;

import com.ericgrandt.totaleconomy.commands.BalanceCommand;
import com.ericgrandt.totaleconomy.commands.JobCommand;
import com.ericgrandt.totaleconomy.commands.PayCommand;
import com.ericgrandt.totaleconomy.data.AccountData;
import com.ericgrandt.totaleconomy.data.BalanceData;
import com.ericgrandt.totaleconomy.data.CurrencyData;
import com.ericgrandt.totaleconomy.data.Database;
import com.ericgrandt.totaleconomy.data.JobData;
import com.ericgrandt.totaleconomy.data.dto.CurrencyDto;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.listeners.JobListener;
import com.ericgrandt.totaleconomy.listeners.PlayerListener;
import com.ericgrandt.totaleconomy.services.JobService;
import com.ericgrandt.totaleconomy.wrappers.BukkitWrapper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
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

    private Database database;
    private EconomyImpl economy;

    @Override
    public void onEnable() {
        database = new Database(
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
        economy = new EconomyImpl(logger, this.isEnabled(), defaultCurrency, accountData, balanceData);

        getServer().getServicesManager().register(
            Economy.class,
            economy,
            this,
            ServicePriority.Normal
        );

        registerCommands();
        registerListeners();
    }

    private void registerCommands() {
        Objects.requireNonNull(this.getCommand("balance")).setExecutor(new BalanceCommand(economy));
        Objects.requireNonNull(this.getCommand("pay")).setExecutor(new PayCommand(new BukkitWrapper(), economy));

        if (config.getBoolean("features.jobs")) {
            JobData jobData = new JobData(database);
            JobService jobService = new JobService(logger, jobData);
            JobCommand jobCommand = new JobCommand(logger, jobService);

            Objects.requireNonNull(this.getCommand("job")).setExecutor(jobCommand);
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(economy), this);

        if (config.getBoolean("features.jobs")) {
            JobData jobData = new JobData(database);
            JobService jobService = new JobService(logger, jobData);

            getServer().getPluginManager().registerEvents(new JobListener(economy, jobService), this);
        }
    }
}
