/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.wgtrophyflags;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.shininet.bukkit.playerheads.events.LivingEntityDropHeadEvent;

/**
 *
 * @author crash
 */
public class WGTrophyFlagsPlugin extends JavaPlugin implements Listener {
    WorldGuardPlugin wgp = null;
    WorldGuard wg = null;
    LivingEntityDropHeadEvent event;
    
    private boolean PHEnabled=false;
    private boolean MTEnabled=false;
    private boolean DHEnabled=false;
    
    
    public static final StateFlag FLAG_BEHEADTROPHY = new StateFlag("behead-trophy", true);
    public static final StateFlag FLAG_MININGTROPHY = new StateFlag("mining-trophy", true);
    public static final StateFlag FLAG_TROPHYWARNINGS = new StateFlag("trophy-warnings", true);
    
    
    
    public boolean hasDropHeads(){return DHEnabled;}
    public boolean hasPlayerheads(){return PHEnabled;}
    public boolean hasMiningtrophies(){return MTEnabled;}
    public WorldGuard getWorldGuard(){ return wg; }
    public WorldGuardPlugin getWorldGuardPlugin(){ return wgp; }
    
    private WorldGuardPlugin findWorldGuardPlugin() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldGuardPlugin) plugin;
    }
    
    private boolean isPHAvailable(){
        if(this.getServer().getPluginManager().getPlugin("PlayerHeads") != null){
            return classExists("org.shininet.bukkit.playerheads.events.MobDropHeadEvent");
        }
        return false;
    }
    private boolean isMTAvailable(){
        if(this.getServer().getPluginManager().getPlugin("MiningTrophies") != null){
            return classExists("com.github.crashdemons.miningtrophies.events.BlockDropTrophyEvent");
        }
        return false;
    }
    private boolean isDHAvailable(){
        if(this.getServer().getPluginManager().getPlugin("DropHeads") != null){
            return classExists("net.evmodder.DropHeads.events.EntityBeheadEvent");
        }
        return false;
    }
    
    
    private boolean classExists(String name){
        try {
            Class.forName( name );
            return true;
        } catch( ClassNotFoundException e ) {
            return false;
        }
    }
    
    private boolean wgInit(){
        wgp = findWorldGuardPlugin();
        wg = WorldGuard.getInstance();
        if(wgp==null || wg==null){
            return false; 
        }
        
        FlagRegistry registry = wg.getFlagRegistry();
        try {
            // register our flag with the registry
            registry.register(FLAG_BEHEADTROPHY);
            registry.register(FLAG_MININGTROPHY);
            registry.register(FLAG_TROPHYWARNINGS);
            return true;
        } catch (FlagConflictException e) {
            // some other plugin registered a flag by the same name already.
            // you may want to re-register with a different name, but this
            // could cause issues with saved flags in region files. it's better
            // to print a message to let the server admin know of the conflict
            getLogger().severe("Could not register WG flags due to a conflict with another plugin");
            return false;
        }
    }
    
    private boolean pluginInit(){
        this.PHEnabled=isPHAvailable();
        this.MTEnabled=isMTAvailable();
        this.DHEnabled=isDHAvailable();
        
        if(PHEnabled || MTEnabled || DHEnabled){
            if(PHEnabled) getLogger().info("PlayerHeads support detected");
            if(MTEnabled) getLogger().info("MiningTrophies support detected");
            if(DHEnabled) getLogger().info("DropHeads support detected");
            return true;
        }else{
            getLogger().warning("Neither PlayerHeads, MiningTrophies, nor DropHeads plugins are present - disabling plugin");
            this.getServer().getPluginManager().disablePlugin(this);
            return false;
        }
    }
    
    @Override
    public void onLoad(){
        if(!wgInit()) return;
    }
    
    @Override
    public void onEnable(){
        getLogger().info("Enabling...");
        if(!pluginInit()) return;
        if(PHEnabled){
            getServer().getPluginManager().registerEvents(new PHListener(this), this);
        }
        if(MTEnabled){
            getServer().getPluginManager().registerEvents(new MTListener(this), this);
        }
        if(DHEnabled){
            getServer().getPluginManager().registerEvents(new DHListener(this), this);
        }
        getLogger().info("Enabled.");
    }
    
    @Override
    public void onDisable(){
        getLogger().info("Disabling...");
        getLogger().info("Disabled.");
    }

}
