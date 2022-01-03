package coloryr.allmusic.side.sideBukkit;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {
    private Economy econ = null;

    public VaultHook() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        econ = rsp.getProvider();
    }

    public boolean setupEconomy() {
        return econ != null;
    }

    public boolean check(String name, int cost) {
        Player player = Bukkit.getPlayer(name);
        if (player == null)
            return false;
        return econ.has(player, cost);
    }

    public void cost(String name, int cost, String message) {
        Player player = Bukkit.getPlayer(name);
        if (player == null)
            return;
        EconomyResponse r = econ.withdrawPlayer(player, cost);
        if (r.transactionSuccess()) {
            player.sendMessage(message);
        }
    }
}
