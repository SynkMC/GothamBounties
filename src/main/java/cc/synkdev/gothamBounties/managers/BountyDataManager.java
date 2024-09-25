package cc.synkdev.gothamBounties.managers;

import cc.synkdev.gothamBounties.GothamBounties;
import cc.synkdev.gothamBounties.objects.Bounty;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.*;
import java.util.*;

public class BountyDataManager {
    private final static GothamBounties core = GothamBounties.getInstance();
    private final static File file = new File(core.getDataFolder(), "data.yml");
    public static void load() {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            List<Bounty> list = new ArrayList<>();
            Map<OfflinePlayer, Double> top = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    String[] split = line.split(";");
                    list.add(new Bounty(Bukkit.getOfflinePlayer(UUID.fromString(split[0])),Bukkit.getOfflinePlayer(UUID.fromString(split[1])), Double.parseDouble(split[2])));
                    OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(split[1]));
                    Double d = Double.parseDouble(split[2]);
                    if (top.containsKey(op)) {
                        top.replace(op, top.get(op)+d);
                    } else {
                        top.put(op, d);
                    }
                }
            }
            reader.close();
            core.bountyMap.clear();
            core.bountyMap.addAll(list);
            core.bountiesMap.putAll(top);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void save() {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            File temp = new File(core.getDataFolder(), "temp-save-"+System.currentTimeMillis());
            temp.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
            for (Bounty b : core.bountyMap) {
                writer.write(b.getOrigin().getUniqueId()+";"+b.getTarget().getUniqueId()+";"+b.getValue());
                writer.newLine();
            }
            writer.close();
            temp.renameTo(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
