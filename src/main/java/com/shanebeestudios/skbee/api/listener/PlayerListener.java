package com.shanebeestudios.skbee.api.listener;

import com.shanebeestudios.skbee.SkBee;
import com.shanebeestudios.skbee.api.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings({"FieldCanBeLocal", "deprecation"})
public class PlayerListener implements Listener {

    private final SkBee plugin;
    private final String[] colorCodes = new String[]{"&a", "&b", "&c", "&d", "&e", "&f", "&2", "&3", "&4", "&4", "&5", "&6", "&9"};
    private final List<String> messages = new ArrayList<>();
    private final Random random = new Random();
    private final int messagePeriod = 20 * 60 * 2; // 2 minutes
    private BukkitTask bukkitTask;

    public PlayerListener(SkBee plugin) {
        this.plugin = plugin;
        setupMessages();
        startTimer();
    }

    private void setupMessages() {
        addMessage("You can remove these messages by negating the permission 'skbee.april.fools'!!!");
        addMessage("Try SkBee Pro for the low price of $29.99usd a month!!");
        addMessage("Try Skript Pro for the low price of $53.99usd a month!");
        addMessage("Did you know Minecraft is completely getting rid of NBT in 1.20.5?");
        addMessage("Use code 'SKBEE_BONUS_50' to get 50% off at Amazon.");
        addMessage("Use code 'SKBEE_MUSIC_FOR_LIFE' to get 25% off for 3 months at Spotify.");
        addMessage("Don't forget to donate at 'https://ko-fi.com/shanebee'");
        addMessage("Don't forget to donate at skUnity!");
        addMessage("Don't forget to tell BaeFell how much you love PHP!");
        addMessage("Did you check the docs?");
        addMessage("Don't forget to check the docs at 'https://docs.skriptlang.org'");
        addMessage("Don't forget to check the docs at 'https://skripthub.net/docs/?addon=SkBee'");
        addMessage("You should try SkBriggy for all your Brigadier Command needs!");
        addMessage("You should try SkMaze for all your procedurally generated maze needs!");
        addMessage("You should try skript-reflect for all your java reflection needs!");
        addMessage("You should try Umbaska for... um.... actually I have no clue what it does, don't get it!");
        addMessage("Subscribe to Netflix for $16.99usd a month!");
        addMessage("Subscribe to Amazon Prime for $22.99usd a month!");
        addMessage("Subscribe to Hulu for $12.99usd a month!");
        addMessage("Subscribe to YouTube Music for $69.69usd a month!");
        addMessage("Send 'free diamonds' in chat to get some free diamonds!");
        addMessage("What is the answer to '5 * 6 + 1 / 7 ^ 52'?");
        addMessage("Watch an all new season of 'Vanderpump Rules' on Hayu!");
        addMessage("Watch an all new season of 'Below Deck' on Hayu!");
        addMessage("Tune in this Friday for your favorite sports game! GO SPORTS!!!!");
        addMessage("Tune in this Thursday for an all new 'Spongebob Square Pants'!");
        addMessage("Tune in this Wednesday for an all new Minecraft Snapshot!");
        addMessage("Tune in tomorrow for the biggest Minecraft update you've ever seen!!!!");
        addMessage("Did you know SkBee has BossBars? You should check em out!");
        addMessage("Did you know SkBee has NBT? You should check it out!");
        addMessage("Did you know SkBee has TextComponents? You should check em out!");
        addMessage("Did you know SkBee has GameEvents? You should check em out!");
        addMessage("Did you know SkBee has Objectives? You should check em out!");
        addMessage("Did you know SkBee has Scoreboards? You should check em out!");
        addMessage("Did you know SkBee has Teams? You should check em out!");
        addMessage("Did you know SkBee has Minecraft Tags? You should check em out!");
        addMessage("Did you know SkBee has TickManager? You should check it out!");
        addMessage("Did you know SkBee has WorldBorders? You should check em out!");
        addMessage("Did you know SkBee has WorldCreator? You should check it out!");
        addMessage("Did you know Skript has Villagers? You should check em out!");
        addMessage("Did you know Skript has DisplayEntities? You should check em out!");
        addMessage("Did you know Skript has Sheep? You should check em out!");
        addMessage("Did you know Skript has Wolves? You should check em out!");
        for (Plugin plug : Bukkit.getPluginManager().getPlugins()) {
            String name = plug.getName();
            addMessage("You're current running " + name + " version " + plug.getDescription().getVersion());
            if (!name.equalsIgnoreCase("Skript") && !name.equalsIgnoreCase("SkBee")) {
                addMessage("Do you really need " + name + "???");
            }
        }
    }

    private void addMessage(String message) {
        messages.add(message);
    }

    private void startTimer() {
        LocalDate date = LocalDate.now();
        // If it's after April fools and we're still running this build, get out
        if (isAfterAprilFools()) return;

        // If it's April fools or before, we start our timer
        // This is incase someone starts their server before the first
        // and lets it run for a few days
        this.bukkitTask = Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            // If April fools has passed while the timer is running, let's exit
            if (isAfterAprilFools()) {
                PlayerListener.this.bukkitTask.cancel();
            }
            // If it's April fools let's send our messages
            else if (isAprilFools()) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (player.hasPermission("skbee.april.fools")) {
                        sendRandomMessage(player);
                    }

                });
            }
        }, messagePeriod, messagePeriod);
    }

    private void sendRandomMessage(Player player) {
        String colorCode = this.colorCodes[random.nextInt(this.colorCodes.length)];
        String message = this.messages.get(random.nextInt(this.messages.size()));
        String string = Util.getColString("&7[&bSk&3Bee&7] " + colorCode + message);
        player.sendMessage(string);
    }

    private boolean isAprilFools() {
        LocalDate date = LocalDate.now();
        return date.getMonth() == Month.APRIL && date.getDayOfMonth() == 1;
    }

    private boolean isAfterAprilFools() {
        LocalDate date = LocalDate.now();
        // It's april, but after the first
        if (date.getMonthValue() == 4 && date.getDayOfMonth() > 1) return true;
        // It's after april
        return date.getMonthValue() > 4;

    }

}
