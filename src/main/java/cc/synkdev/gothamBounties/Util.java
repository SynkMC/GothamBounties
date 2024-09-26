package cc.synkdev.gothamBounties;

import cc.synkdev.gothamBounties.objects.Band;
import cc.synkdev.gothamBounties.objects.Bounty;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Util {
    private final static GothamBounties core = GothamBounties.getInstance();
    public static List<Bounty> getPlayersBounties(OfflinePlayer p) {
        List<Bounty> list = new ArrayList<>();
        for (Bounty b : core.bountyMap) {
            Bukkit.getLogger().info("Running on "+b.getOrigin().getName()+"'s bounty over "+b.getTarget().getName());
            if (comparePlayers(p, b.getTarget())) {
                Bukkit.getLogger().info("Added to list");
                list.add(b);
            }
        }
        return list;
    }
    public static Double getPlayersBountyTotal(OfflinePlayer p) {
        Double d = 0.0;
        for (Bounty b : core.bountyMap) {
            if (comparePlayers(p, b.getTarget())) {
                d = d+b.getValue();
            }
        }
        return d;
    }
    public static List<Bounty> getPlayersSentBounties(OfflinePlayer p) {
        List<Bounty> list = new ArrayList<>();
        for (Bounty b : core.bountyMap) {
            if (comparePlayers(p, b.getOrigin())) {
                list.add(b);
            }
        }
        return list;
    }
    public static Boolean canSendBounty(OfflinePlayer p, OfflinePlayer target) {
        for (Bounty b : getPlayersSentBounties(p)) {
            if (comparePlayers(b.getTarget(), target)) return false;
        }
        return true;
    }
    public static Boolean comparePlayers(OfflinePlayer p1, OfflinePlayer p2) {
        return p1.getUniqueId().toString().equalsIgnoreCase(p2.getUniqueId().toString());
    }
    public static void log(String s, Boolean prefix) {
        if (prefix) s = core.prefix+s;

        Bukkit.getConsoleSender().sendMessage(s);
    }
    public static Band getBand(OfflinePlayer p) {
        for (Band b : core.bandsMap) {
            if (comparePlayers(Bukkit.getOfflinePlayer(b.getLeader()), p)) return b;
            for (UUID id : b.getMembers()) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(id);
                if (comparePlayers(op, p)) return b;
            }
        }
        return null;
    }
    public static Band getBand(String name) {
        for (Band b : core.bandsMap) {
            if (b.getName().equalsIgnoreCase(name)) return b;
        }
        return null;
    }
    public static OfflinePlayer getOfflinePlayer(String name) {
        for (Map.Entry<UUID, String> entry : core.offlinePlayers.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(name)) return Bukkit.getOfflinePlayer(entry.getKey());
        }
        return null;
    }
}
