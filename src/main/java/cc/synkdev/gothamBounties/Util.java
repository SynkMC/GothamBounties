package cc.synkdev.gothamBounties;

import cc.synkdev.gothamBounties.objects.Band;
import cc.synkdev.gothamBounties.objects.Bounty;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Util {
    private final static GothamBounties core = GothamBounties.getInstance();
    public static List<Bounty> getPlayersBounties(OfflinePlayer p) {
        List<Bounty> list = new ArrayList<>();
        for (Bounty b : core.bountyMap) {
            if (comparePlayers(p, b.getTarget())) {
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
    public static Bounty getPlayersSentBounty(OfflinePlayer p) {
        for (Bounty b : core.bountyMap) {
            if (comparePlayers(p, b.getOrigin())) {
                return b;
            }
        }
        return null;
    }
    public static Boolean canSendBounty(OfflinePlayer p) {
        return getPlayersSentBounty(p) == null;
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
}
