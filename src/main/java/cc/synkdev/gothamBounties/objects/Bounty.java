package cc.synkdev.gothamBounties.objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;

@Getter @Setter
public class Bounty {
    private OfflinePlayer origin;
    private OfflinePlayer target;
    private double value;
    public Bounty (OfflinePlayer origin, OfflinePlayer target, double value) {
        setOrigin(origin);
        setTarget(target);
        setValue(value);
    }
}
