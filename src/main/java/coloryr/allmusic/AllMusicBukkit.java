package coloryr.allmusic;

import Color_yr.AllMusic.side.sideBukkit.*;
import coloryr.allmusic.side.sideBukkit.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class AllMusicBukkit extends JavaPlugin {
    public static Plugin plugin;
    public static coloryr.allmusic.side.sideBukkit.PAPI PAPI;

    @Override
    public void onEnable() {
        plugin = this;
        AllMusic.log = new BukkitLog(getLogger());
        AllMusic.Side = new SideBukkit();

        new AllMusic().init(plugin.getDataFolder());
        if (!AllMusic.isRun) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PAPI = new PAPI(this);
            if (!PAPI.register()) {
                AllMusic.log.info("§2PAPI支持已启动");
            }
        } else {
            AllMusic.log.info("§2PAPI未挂钩");
        }

        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            try {
                AllMusic.Vault = new VaultHook();
                if (AllMusic.Vault.setupEconomy()) {
                    AllMusic.log.info("§2Vault支持已启动");
                } else {
                    AllMusic.log.info("§2Vault未挂钩");
                    AllMusic.Vault = null;
                }
            } catch (Exception e) {
                AllMusic.log.info("§2Vault未挂钩");
                AllMusic.Vault = null;
            }
        } else {
            AllMusic.log.info("§2Vault未挂钩");
            AllMusic.Vault = null;
        }
        getServer().getMessenger().registerOutgoingPluginChannel(this, AllMusic.channel);
        Bukkit.getPluginCommand("music").setExecutor(new CommandBukkit());
        Bukkit.getPluginCommand("music").setTabCompleter(new CommandBukkit());
        Bukkit.getPluginManager().registerEvents(new EventBukkit(), this);
        new MetricsBukkit(this, 6720);

        AllMusic.start();
    }

    @Override
    public void onDisable() {
        AllMusic.isRun = false;
        AllMusic.stop();
    }
}
