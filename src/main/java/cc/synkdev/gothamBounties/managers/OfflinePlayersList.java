package cc.synkdev.gothamBounties.managers;

import cc.synkdev.gothamBounties.GothamBounties;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OfflinePlayersList {
    private static GothamBounties core = GothamBounties.getInstance();
    private static final File file = new File(core.getDataFolder(), "known-players.yml");
    public static void read() {
        try {
            if (!file.exists()) {
                file.createNewFile();
                return;
            }

            Map<UUID, String> map = new HashMap<>();

            BufferedReader read = new BufferedReader(new FileReader(file));
            String line;
            while ((line = read.readLine()) != null) {
                if (!line.isEmpty()) map.put(UUID.fromString(line.split(";")[0]), line.split(";")[1]);
            }

            read.close();
            core.offlinePlayers.clear();
            core.offlinePlayers.putAll(map);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void save() {
        try {
            File temp = new File(core.getDataFolder(),"temp-players-"+System.currentTimeMillis()+".yml");
            temp.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
            for (Map.Entry<UUID, String> entry : core.offlinePlayers.entrySet()) {
                writer.write(entry.getKey().toString()+";"+entry.getValue());
                writer.newLine();
            }
            writer.close();
            temp.renameTo(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
