package cc.synkdev.gothamBounties;

import cc.synkdev.gothamBounties.commands.BandCommand;
import cc.synkdev.gothamBounties.commands.BountyCommand;
import cc.synkdev.gothamBounties.managers.*;
import cc.synkdev.gothamBounties.managers.EventListener;
import cc.synkdev.gothamBounties.objects.Band;
import cc.synkdev.gothamBounties.objects.Bounty;
import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public final class GothamBounties extends JavaPlugin {
    @Getter private static GothamBounties instance;
    public List<Bounty> bountyMap = new ArrayList<>();
    public Map<OfflinePlayer, Double> bountiesMap = new HashMap<>();
    public Map<UUID, String> offlinePlayers = new HashMap<>();
    public String prefix = ChatColor.RESET+""+ChatColor.DARK_GRAY+"["+ChatColor.GOLD+"GothamBounties"+ChatColor.DARK_GRAY+"] » "+ChatColor.RESET;
    private Boolean isCrashing = true;
    public Economy eco;
    public PaperCommandManager manager;
    public List<Band> bandsMap = new ArrayList<>();
    public List<Band> topKills = new ArrayList<>();
    public List<Band> topPT = new ArrayList<>();
    public List<Band> globalTop = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;

        if (!this.getDataFolder().exists()) this.getDataFolder().mkdirs();

        if (!setupEconomy()) {
            Util.log(ChatColor.DARK_RED+"Vault isn't installed on the server!", true);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        BandDataManager.load();
        BountyDataManager.load();
        OfflinePlayersList.read();

        getCommand("bounty").setTabCompleter(new BountyCommand());
        getCommand("bounty").setExecutor(new BountyCommand());

        manager = new PaperCommandManager(this);
        manager.enableUnstableAPI("brigadier");
        manager.enableUnstableAPI("help");

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        manager.registerCommand(new BandCommand());

        new PAPIManager().register();

        isCrashing = false;

        BukkitRunnable save = new BukkitRunnable() {
            @Override
            public void run() {
                OfflinePlayersList.save();
                OfflinePlayersList.read();
                BountyDataManager.save();
                BountyDataManager.load();
                BandDataManager.save();
                BandDataManager.load();
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

    public void updateData(Band b) {
        Iterator<Band> iter = bandsMap.iterator();
        while (iter.hasNext()) {
            Band next = iter.next();
            if (next.getId().equals(b.getId())) {
                iter.remove();
                break;
            }
        }
        bandsMap.add(b);
        topKills.clear();
        topKills.addAll(bandsMap);
        topKills.sort(Comparator.comparingInt(Band::getKills).reversed());

        topPT.clear();
        topPT.addAll(bandsMap);
        topPT.sort(Comparator.comparingInt(Band::getPlayTime).reversed());

        bandsMap.get(bandsMap.indexOf(b)).setGlobalTop(topKills.indexOf(b)+topPT.indexOf(b));

        globalTop.clear();
        globalTop.addAll(bandsMap);
        globalTop.sort(Comparator.comparingInt(Band::getGlobalTop));
    }

    @Override
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.kickPlayer("Server is restarting!");
        }
        if (!isCrashing) BountyDataManager.save();
    }
}
