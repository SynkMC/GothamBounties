package cc.synkdev.gothamBounties.managers;

import cc.synkdev.gothamBounties.GothamBounties;
import cc.synkdev.gothamBounties.Util;
import cc.synkdev.gothamBounties.objects.Band;
import cc.synkdev.gothamBounties.objects.Bounty;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class EventListener implements Listener {
    private final GothamBounties core = GothamBounties.getInstance();
    @EventHandler
    public void death(PlayerDeathEvent event) {
        Player p = event.getEntity();
        Band pB = Util.getBand(p);
        if (pB != null) {
            pB.setDeaths(pB.getDeaths()+1);
            core.updateData(pB);
        }
        if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            Player origin = isPlayerDamage((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause());
            if (origin == null) return;
            if (Util.comparePlayers(p, origin)) return;
            Band oriB = Util.getBand(origin);
            if (oriB != null) {
                oriB.setKills(oriB.getKills()+1);
                core.updateData(oriB);
            }
            List<Bounty> list = Util.getPlayersBounties(p);
            if (list.isEmpty()) return;

            double total = Util.getPlayersBountyTotal(p);
            for (Bounty b : list) {
                if (b.getOrigin().isOnline()) {
                    Player pp = (Player) b.getOrigin();
                    pp.sendMessage(core.prefix+ ChatColor.GOLD+p.getName()+" was killed by "+origin.getName()+", they claimed the $"+b.getValue()+" bounty you put on "+p.getName()+"'s name.");
                }
                core.bountyMap.remove(b);
            }
            core.eco.depositPlayer(origin, total);
            origin.sendMessage(core.prefix+ChatColor.RED+"Since you killed "+p.getName()+", you earned their $"+Math.round(total)+" bounty!");
            p.sendMessage(core.prefix+ChatColor.RED+"Since "+origin.getName()+" killed you, they earned your $"+Math.round(total)+" bounty!");
        }
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        Band b = Util.getBand(p);
        if (b == null) {
            return;
        }

        b.getJoinTimes().put(p.getUniqueId(), Math.toIntExact(System.currentTimeMillis()/1000));
    }
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        Band b = Util.getBand(p);
        if (b == null) {
            return;
        }

        int join = b.getJoinTimes().remove(p.getUniqueId());
        join = Math.toIntExact(System.currentTimeMillis()/1000)-join;
        b.setPlayTime(b.getPlayTime()+join);
        core.updateData(b);
    }

    private Player isPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() == EntityType.PLAYER) {
            return (Player) event.getDamager();
        } else return null;
    }
}
