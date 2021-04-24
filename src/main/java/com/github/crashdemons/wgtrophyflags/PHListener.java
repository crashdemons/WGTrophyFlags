/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.wgtrophyflags;

import static com.github.crashdemons.wgtrophyflags.WGTrophyFlagsPlugin.FLAG_BEHEADTROPHY;
import static com.github.crashdemons.wgtrophyflags.WGTrophyFlagsPlugin.FLAG_TROPHYWARNINGS;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.shininet.bukkit.playerheads.events.LivingEntityDropHeadEvent;

/**
 *
 * @author crashdemons <crashenator at gmail.com>
 */
public class PHListener implements Listener{
    private final WGTrophyFlagsPlugin plugin;
    PHListener(WGTrophyFlagsPlugin plugin){
        this.plugin=plugin;
    }
    
    @EventHandler
    public void onLivingEntityDropHeadEvent(LivingEntityDropHeadEvent event){
        if(!plugin.hasPlayerheads()) return;
        if(!plugin.isEnabled()) return;
        if(!(event instanceof LivingEntityDropHeadEvent)) return;
        LivingEntityDropHeadEvent beheading = (LivingEntityDropHeadEvent) event;
        LivingEntity beheadee = beheading.getEntity();
        Location loc = beheadee.getLocation();
        Player beheader = beheadee.getKiller();
        //BukkitAdapter.adapt(player)
        
        com.sk89q.worldedit.util.Location wgLoc = BukkitAdapter.adapt(loc);
        @SuppressWarnings("unused")
        com.sk89q.worldedit.entity.Entity wgBeheadee = BukkitAdapter.adapt(beheadee);
        LocalPlayer wgBeheader = null;
        if(beheader!=null){
            wgBeheader=plugin.getWorldGuardPlugin().wrapPlayer(beheader);
        }
        //com.sk89q.worldedit.bukkit.BukkitPlayer wgBeheader = BukkitAdapter.adapt(beheader);

        
        
        RegionQuery query = plugin.getWorldGuard().getPlatform().getRegionContainer().createQuery();
        StateFlag.State state = query.queryState(wgLoc, wgBeheader, FLAG_BEHEADTROPHY);
        StateFlag.State warningstate = query.queryState(wgLoc, null, FLAG_TROPHYWARNINGS);
        if(state==StateFlag.State.DENY){
            if(beheader!=null){
                if(warningstate!=StateFlag.State.DENY) beheader.sendMessage(ChatColor.RED+"Hey! "+ChatColor.GRAY+"Sorry, but you can't behead that here.");
                beheading.setCancelled(true);
            }
        }
    }
}
