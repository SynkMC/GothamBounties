package cc.synkdev.gothamBounties.managers;

import cc.synkdev.gothamBounties.GothamBounties;
import cc.synkdev.gothamBounties.Util;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PAPIManager extends PlaceholderExpansion {
    private final GothamBounties core = GothamBounties.getInstance();

    @Override
    @NotNull
    public String getAuthor() {
        return "Synk"; //
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "bounties";
    }

    @Override
    @NotNull
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    public String onRequest(Player p, @NotNull String s) {
        switch (s) {
            case "bounty":
                return Util.getPlayersBountyTotal(p)+"";
        }

        return null; //
    }
}
