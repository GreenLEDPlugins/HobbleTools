package com.greenled.hobbler.hobbletools;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Hobbletools extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic with listener👂
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new hobbler(this), this);
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("hobble").setExecutor(new hobbler(this));
        this.saveDefaultConfig();
        registerCmds();


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    //All the commands in this plugin :)
    public void registerCmds() {
        this.getCommand("hobble").setExecutor(new hobbler(this));
    }

}
