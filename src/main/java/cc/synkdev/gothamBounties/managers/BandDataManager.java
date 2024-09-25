package cc.synkdev.gothamBounties.managers;

import cc.synkdev.gothamBounties.GothamBounties;
import cc.synkdev.gothamBounties.objects.Band;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BandDataManager {
    private final static GothamBounties core = GothamBounties.getInstance();
    private final static File file = new File(core.getDataFolder(), "bands.yml");
    public static void load() {
        List<Band> list = new ArrayList<>();
        try {
            if (!file.exists()) {
                file.createNewFile();
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    new Band(line);
                }
            }
            reader.close();

            core.bandsMap.clear();
            core.bandsMap.addAll(list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void save() {
        try {
            File temp = new File(core.getDataFolder(), "temp-bands-"+System.currentTimeMillis()+".yml");
            temp.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
            for (Band b : core.bandsMap) {
                writer.write(b.toString());
                writer.newLine();
            }
            temp.renameTo(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
