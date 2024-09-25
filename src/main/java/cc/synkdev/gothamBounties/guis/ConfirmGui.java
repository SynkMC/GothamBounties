package cc.synkdev.gothamBounties.guis;

import cc.synkdev.gothamBounties.GothamBounties;
import cc.synkdev.gothamBounties.objects.Bounty;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ConfirmGui {
    private final static GothamBounties core = GothamBounties.getInstance();
    public static Gui gui(Bounty b) {
         Gui gui = Gui.gui()
                .rows(4)
                .disableAllInteractions()
                .title(Component.text("Please confirm the bounty"))
                .create();
         gui.setItem(2, 5, ItemBuilder.skull().owner(b.getTarget()).name(Component.text(ChatColor.RESET+""+ChatColor.GOLD+b.getTarget().getName())).asGuiItem());
         gui.setItem(4, 3, ItemBuilder.from(Material.WOOL).color(Color.GREEN).name(Component.text(ChatColor.GREEN+"Confirm")).asGuiItem(event -> {
             core.eco.withdrawPlayer(b.getOrigin(), b.getValue());
             core.bountyMap.add(b);
             if (b.getTarget().isOnline()) {
                 Player t = (Player) b.getTarget();
                 t.sendMessage(core.prefix+ChatColor.RED+b.getTarget().getName()+" added a $"+b.getValue()+" bounty to your head!");
                 t.playSound(t.getLocation(), "item.trident.thunder", 1, 1);
             }
             ((Player) b.getOrigin()).sendMessage(core.prefix+ChatColor.GREEN+"You added a $"+b.getValue()+" bounty to "+b.getTarget().getName()+"'s head!");
             ((Player) b.getOrigin()).closeInventory();
         }));
         gui.setItem(4, 7, ItemBuilder.from(Material.WOOL).color(Color.RED).name(Component.text(ChatColor.RED+"Cancel")).asGuiItem(event -> {
             event.getWhoClicked().closeInventory();
         }));

         return gui;
    }

}
