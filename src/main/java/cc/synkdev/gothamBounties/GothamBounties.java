package cc.synkdev.gothamBounties;

import cc.synkdev.gothamBounties.commands.BountyCommand;
import cc.synkdev.gothamBounties.managers.DataManager;
import cc.synkdev.gothamBounties.managers.EventListener;
import cc.synkdev.gothamBounties.objects.Bounty;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GothamBounties extends JavaPlugin {
    @Getter private static GothamBounties instance;
    public List<Bounty> bountyMap = new ArrayList<>();
    public String prefix = ChatColor.RESET+""+ChatColor.DARK_GRAY+"["+ChatColor.GOLD+"GothamBounties"+ChatColor.DARK_GRAY+"] » "+ChatColor.RESET;
    private Boolean isCrashing = true;
    public Economy eco;

    @Override
    public void onEnable() {
        instance = this;

        if (!this.getDataFolder().exists()) this.getDataFolder().mkdirs();

        if (!setupEconomy()) {
            Util.log(ChatColor.DARK_RED+"Vault isn't installed on the server!", true);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        DataManager.load();

        getCommand("bounty").setTabCompleter(new BountyCommand());
        getCommand("bounty").setExecutor(new BountyCommand());

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        isCrashing = false;

        BukkitRunnable save = new BukkitRunnable() {
            @Override
            public void run() {
                DataManager.save();
            }
        };
        save.runTaskTimer(this, 1200, 1200);
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        eco = rsp.getProvider();
        return eco != null;
    }

    @Override
    public void onDisable() {
        if (!isCrashing) DataManager.save();
    }
}
