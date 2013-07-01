package com.floyd.bukkit.petition;

import java.io.File;
import java.util.HashMap;
import org.bukkit.entity.Player;

public class NotifierThread extends Thread {
    private Boolean stop = false;
    private Long interval = 300000L; // 5 minutes
    private HashMap<String, Integer> count = new HashMap<String, Integer>();
    private PetitionPlugin plugin = null;
    private String baseDir = "plugins/PetitionPlugin";

    public NotifierThread(PetitionPlugin owner) {
        plugin = owner;
    }

    // This method is called when the thread runs
    @Override
	public void run() {
        System.out.println("[Pe] NotifierThread started");

        while (stop == false) {
            // Go to sleep
            try {
                sleep(interval);
            } catch (InterruptedException e) {
                System.out.println("[Pe] NotifierThread sleep interrupted");
            }
            // Count open petitions per player
            count.clear();
            File dir = new File(baseDir);
            for (String filename : dir.list()) {
                if (stop) { return; }
                if (filename.endsWith(".ticket")) {
                    String[] parts = filename.split("['.']");
                    Integer id = Integer.valueOf(parts[0]);
                    try {
                        while (!plugin.SetPetitionLock(id, "*NotifierThread*", false)) {
                            try {
                                Thread.sleep(100);
                            }
                            catch (InterruptedException e) {
                            }
                        }
                        PetitionObject petition = new PetitionObject(id);
                        if (petition.isValid()) {
                            Integer found = count.get(petition.Owner());
                            if (found == null) { found = 0; }
                            count.put(petition.Owner(), found + 1);
                        }
                    }
                    finally {
                           plugin.SetPetitionLock(id, "*NotifierThread*", true);
                    }
                }
            }

            // Notify each player and get total
            Integer total = 0;
            for (String name : count.keySet()) {
                Integer found = count.get(name);
                total = total + found;
                Player p = plugin.getServer().getPlayer(name);
                if (p != null) {
                    if (found == 1) {
                        p.sendMessage("[Pe] §7Du hast 1 offenes " + plugin.settings.get("single") + ", benutze '/pe list' zum ansehen.");
                    } else {
                        p.sendMessage("[Pe] §7Du hast " + found + " offene " + plugin.settings.get("plural") + ", benutze '/pe list' zum ansehen.");
                    }
                }
            }

            // Notify admins about the total
            String[] except = new String[0];
            if (total > 0) {
                if (total == 1) {
                    plugin.notifyModerators("[Pe] §7Es ist 1 " + plugin.settings.get("single") + " offen, benutze '/pe list' zum ansehen.", except);
                } else {
                    plugin.notifyModerators("[Pe] §7Es sind " + total + " " + plugin.settings.get("plural") + " offen, benutze '/pe list' zum ansehen.", except);
                }
            }
        }
        System.out.println("[Pe] NotifierThread stopped");
    }

    public void signalStop() {
        stop = true;
        System.out.println("[Pe] NotifierThread set to stop");
    }

    public void setInterval(Integer sec) {
        Long ms = sec * 1000L;        
        if (ms < 30000) { ms = 30000L; }
        interval = ms;
        System.out.println("[Pe] NotifierThread interval set to " + sec + " seconds");
    }
}
