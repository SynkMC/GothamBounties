package cc.synkdev.gothamBounties.managers;

import cc.synkdev.gothamBounties.GothamBounties;
import cc.synkdev.gothamBounties.Util;
import cc.synkdev.gothamBounties.objects.Bounty;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;

public class EventListener implements Listener {
    private final GothamBounties core = GothamBounties.getInstance();
    @EventHandler
    public void death(PlayerDeathEvent event) {
        if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            Player origin = isPlayerDamage((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause());
            if (origin == null) return;
            Player p = event.getEntity();
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

    private Player isPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() == EntityType.PLAYER) {
            return (Player) event.getDamager();
        } else return null;
    }
}
