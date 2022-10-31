package com.ericgrandt.totaleconomy;

import com.ericgrandt.totaleconomy.data.Database;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class TotalEconomy extends JavaPlugin implements Listener {
    private final FileConfiguration config = getConfig();
    private final Logger log = Logger.getLogger("Minecraft");

    @Override
    public void onEnable() {
        registerVaultIfPresent();

        Database database = new Database(
            config.getString("database.url"),
            config.getString("database.user"),
            config.getString("database.password")
        );
    }

    private void registerVaultIfPresent() {
        Economy economy = new EconomyImpl(this.isEnabled());
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            getServer().getServicesManager().register(
                Economy.class,
                economy,
                this,
                ServicePriority.Highest
            );

            log.info(String.format(
                "[%s] Running with Vault dependency",
                getDescription().getName()
            ));
        }
    }
}
