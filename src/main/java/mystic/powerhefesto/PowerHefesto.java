package mystic.powerhefesto;

import mystic.powerhefesto.listener.HefestoListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class PowerHefesto extends JavaPlugin {

    public Economy econ;

    @Override
    public void onEnable() {
        setupEconomy();
        Bukkit.getPluginManager().registerEvents(new HefestoListener(), this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll();
    }

    public void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        econ = rsp.getProvider();
    }
}
