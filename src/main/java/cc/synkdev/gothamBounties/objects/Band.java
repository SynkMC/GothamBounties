package cc.synkdev.gothamBounties.objects;

import cc.synkdev.gothamBounties.GothamBounties;
import cc.synkdev.gothamBounties.Util;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

@Getter @Setter
public class Band {
    private final GothamBounties core = GothamBounties.getInstance();
    private UUID leader;
    private List<UUID> members = new ArrayList<>();
    private String name;
    private UUID id;
    private int kills;
    private int deaths;
    private List<UUID> invites = new ArrayList<>();
    private Map<UUID, Integer> joinTimes = new HashMap<>();
    private int globalTop;
    private int playTime;
    public Band(String s) {
        String[] split = s.split(";");
        if (!split[0].isEmpty()) leader = UUID.fromString(split[0]);
        if (!split[1].isEmpty()) {
            for (String ss : split[1].split(",")) {
                members.add(UUID.fromString(ss));
            }
        }
        setName(split[2]);
        if (!split[3].isEmpty()) id = UUID.fromString(split[3]);
        setKills(Integer.parseInt(split[4]));
        if (!split[5].isEmpty()) {
            for (String ss : split[5].split(",")) {
                invites.add(UUID.fromString(ss));
            }
        }
        if (!split[6].isEmpty()) setPlayTime(Integer.parseInt(split[6]));
        deaths = Integer.parseInt(split[7]);
        core.updateData(this);
    }
    public Band(OfflinePlayer leader, String name) {
        setLeader(leader.getUniqueId());
        setName(name);
        setId(UUID.randomUUID());
        setKills(0);
        setDeaths(0);
        setPlayTime(0);
        getJoinTimes().put(leader.getUniqueId(), Math.toIntExact(System.currentTimeMillis()/1000));
    }
    public String getMembersString() {
        if (members.isEmpty()) return "";
        if (members.size() == 1) return members.get(0).toString();
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (int i = 0; i < members.size()-1; i++) {
            sb.append(members.get(i)).append(",");
            index = i;
        }
        sb.append(members.get(index+1));
        return sb.toString();
    }
    public String getInvitesString() {
        if (invites.isEmpty()) return "";
        if (invites.size() == 1) return invites.get(0).toString();
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (int i = 0; i < invites.size()-1; i++) {
            sb.append(invites.get(i)).append(",");
            index = i;
        }
        sb.append(invites.get(index+1));
        return sb.toString();
    }
    public Boolean isInvited(OfflinePlayer p) {
        return invites.contains(p);
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(leader).append(";");
        sb.append(getMembersString()).append(";");
        sb.append(getName()).append(";");
        sb.append(id.toString()).append(";");
        sb.append(kills).append(";");
        sb.append(getInvitesString()).append(";");
        sb.append(getPlayTime()).append(";");
        sb.append(getDeaths());
        return sb.toString();
    }
    public void broadcast(String message) {
        for (UUID uuid : members) {
            OfflinePlayer member = Bukkit.getOfflinePlayer(uuid);
            if (member.isOnline()) ((Player) member).sendMessage(message);
        }
        OfflinePlayer leader = Bukkit.getOfflinePlayer(getLeader());
        if (leader.isOnline()) ((Player) leader).sendMessage(message);
    }
    public Boolean isMember(OfflinePlayer p) {
        if (Util.comparePlayers(p, Bukkit.getOfflinePlayer(leader))) {
            return true;
        }

        for (UUID id : members) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(id);
            if (Util.comparePlayers(p, op)) {
                return true;
            }
        }
        return false;
    }
}
