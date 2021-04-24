/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.wgtrophyflags;

import com.github.crashdemons.miningtrophies.events.BlockDropTrophyEvent;
import static com.github.crashdemons.wgtrophyflags.WGTrophyFlagsPlugin.FLAG_MININGTROPHY;
import static com.github.crashdemons.wgtrophyflags.WGTrophyFlagsPlugin.FLAG_TROPHYWARNINGS;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author crashdemons <crashenator at gmail.com>
 */
public class MTListener implements Listener{
    private final WGTrophyFlagsPlugin plugin;
    MTListener(WGTrophyFlagsPlugin plugin){
        this.plugin=plugin;
    }
    
    @EventHandler
    public void onBlockDropTrophyEvent(BlockDropTrophyEvent event){
        if(!plugin.hasMiningtrophies()) return;
        if(!plugin.isEnabled()) return;
        if(!(event instanceof BlockDropTrophyEvent)) return;
        BlockDropTrophyEvent trophyevent = (BlockDropTrophyEvent) event;
        Block block = trophyevent.getBlock();
        @SuppressWarnings("unused")
        ItemStack reward = trophyevent.getDrop();
        Player rewardee = trophyevent.getPlayer();
        Location loc = block.getLocation();
        
        LocalPlayer wgRewardee = null;
        if(rewardee!=null){
            wgRewardee = plugin.getWorldGuardPlugin().wrapPlayer(rewardee);
        }
        com.sk89q.worldedit.util.Location wgLoc = BukkitAdapter.adapt(loc);
        RegionQuery query = plugin.getWorldGuard().getPlatform().getRegionContainer().createQuery();
        StateFlag.State state = query.queryState(wgLoc, wgRewardee, FLAG_MININGTROPHY);
        StateFlag.State warningstate = query.queryState(wgLoc, null, FLAG_TROPHYWARNINGS);
        if(state==StateFlag.State.DENY){
            if(rewardee!=null){
                if(warningstate!=StateFlag.State.DENY) rewardee.sendMessage(ChatColor.RED+"Hey! "+ChatColor.GRAY+"Sorry, but you can't get mining-trophies here.");
                trophyevent.setCancelled(true);
            }
        }
    }
    
}
