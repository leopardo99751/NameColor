package me.xelades.dev.namecolor;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class NameColor extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        // Setup default config
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        // Setup player data storage
        Storage.setup();

        // Set up placeholders
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new Placeholders().register();
        }
        getServer().getPluginManager().registerEvents(this, this);
    }

    //@Override
    //public void onDisable() {
    //}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration config = getConfig();

        if (command.getName().equalsIgnoreCase("namecolor")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                String playerName = player.getName();

                if (args.length == 1) {
                    String color =  args[0];
                    FileConfiguration storageFile = Storage.get();

                    if (player.hasPermission("namecolor.all") && color.matches("([" + config.get("color-character") + "][\\dabcdeflmnor])+")) {
                        setNameColor(color, player, true);
                    } else if (player.hasPermission("namecolor.color") && color.matches("([" + config.get("color-character") + "][\\dabcdef])+")) {
                        setNameColor(color, player, true);
                    } else {
                        player.sendMessage(ChatColor.RED + "Please make sure your colors are correctly formatted and that you have permission to use all of them.");
                    }

                } else if (args.length > 1) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes(config.get("color-character").toString().charAt(0), config.get("messages.too-many-arguments").toString()));
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes(config.get("color-character").toString().charAt(0), config.get("messages.no-arguments").toString()));
                }

            }

        }
        return false;
    }

    public void setNameColor(String color, Player player, Boolean tellPlayer) {
        System.out.println("h");
        FileConfiguration config = getConfig();
        FileConfiguration storageFile = Storage.get();
        if (storageFile.contains(player.getUniqueId().toString())) {
            storageFile.set(player.getUniqueId().toString(), color);
            Storage.save();
        } else {
            storageFile.addDefault(player.getUniqueId().toString(), color);
            storageFile.options().copyDefaults(true);
            Storage.save();
        }
        if (tellPlayer) {
            player.sendMessage(ChatColor.translateAlternateColorCodes(config.get("color-character").toString().charAt(0), config.get("messages.success").toString().replaceAll("%color%", color)));
        }
        if (config.get("modify-displayname").equals(true)) {
            player.setDisplayName(ChatColor.translateAlternateColorCodes('&', color) + player.getName());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FileConfiguration storageFile = Storage.get();
        if (storageFile.contains(player.getUniqueId().toString())) {
            setNameColor(storageFile.getString(player.getUniqueId().toString()), player, false);
        }
    }

}
