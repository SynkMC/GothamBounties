package cc.synkdev.gothamBounties.guis;

import cc.synkdev.gothamBounties.GothamBounties;
import cc.synkdev.gothamBounties.objects.Bounty;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TopGui {
    private final static GothamBounties core = GothamBounties.getInstance();
    public static Gui gui() {
        Gui gui = Gui.gui()
                .title(Component.text(ChatColor.YELLOW+"Top bounties"))
                .rows(4)
                .disableAllInteractions()
                .create();
        List<Bounty> list = new ArrayList<>(core.bountyMap);
        list.sort(Comparator.comparingDouble(Bounty::getValue));
        int[] slots = {10,11,12,13,14,15,16,21,22,23};
        int index = 0;
        for (int i : slots) {
            gui.setItem(i, playerHead(index));
            index++;
        }
        return gui;
    }
    private static GuiItem playerHead(int index) {
        List<Map.Entry<OfflinePlayer, Double>> list = new ArrayList<>(core.bountiesMap.entrySet());
        list.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        GuiItem item;
        if (list.size() > index) {
             item = ItemBuilder.skull()
                     .owner(list.get(index).getKey())
                     .name(Component.text(ChatColor.YELLOW+"#"+(index+1)+" - "+list.get(index).getKey().getName()))
                     .lore(Component.text(ChatColor.GOLD+"$"+list.get(index).getValue()))
                     .asGuiItem();
        } else {
            item = ItemBuilder.skull()
                    .texture("ewogICJ0aW1lc3RhbXAiIDogMTYyMjgzMTk1Nzc1MywKICAicHJvZmlsZUlkIiA6ICIwZjczMDA3NjEyNGU0NGM3YWYxMTE1NDY5YzQ5OTY3OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJPcmVfTWluZXIxMjMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTliNDUyOWIxMTI5ZWU5ZDliZmRkNDYwY2ZmMDQwOGMwMzJlOTY2NWZhZTMzYTViM2ZlMDNjNWE5YTVhMzE4MCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9")
                    .name(Component.text(ChatColor.YELLOW+"#"+(index+1)+" - ???"))
                    .lore(Component.text(ChatColor.GOLD+"No player on this spot of the leaderboard!"))
                    .asGuiItem();
        }

        return item;
    }
}
