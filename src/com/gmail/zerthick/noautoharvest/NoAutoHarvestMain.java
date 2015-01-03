/*
 * Copyright (C) 2015 Zerthick
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.gmail.zerthick.noautoharvest;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author zerthick
 */
public class NoAutoHarvestMain extends JavaPlugin implements Listener {

    private Set<String> liquidSet;
    private Set<String> pistonSet;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();  //create default config if it does not already exist
        liquidSet = new HashSet<>(this.getConfig().getStringList("liquid_protection"));
        pistonSet = new HashSet<>(this.getConfig().getStringList("piston_protection"));
        this.getLogger().log(Level.INFO, "Liquid Protection: {0}", liquidSet);
        this.getLogger().log(Level.INFO, "Piston Protection: {0}", pistonSet);
        this.getServer().getPluginManager().registerEvents(this, this);  //register events
        this.getLogger().log(Level.INFO, "NoautoHarvest Enabled!");
    }
    
    @EventHandler
    public void onWaterBreak(BlockFromToEvent event){
        if(event.getBlock().getType() == Material.WATER || event.getBlock().getType() == Material.WATER){
            if(liquidSet.contains(event.getToBlock().getType().toString())){  //if the block being destoryed is on the liquid protect list
                event.getToBlock().setType(Material.AIR);  //destory the block without drops
            }
        }
    }
    
    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event){
        
        Block source = event.getBlock();
        int numBlocks = event.getBlocks().size();
        if(!isSafeBlock(source, event.getDirection(), numBlocks+1)){
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event){
        Block source = event.getBlock().getRelative(event.getDirection());
        if(!isSafeBlock(source, event.getDirection(), 1)){
            event.setCancelled(true);
        }
    }
    
    private boolean isSafeBlock(Block source, BlockFace dir, int max){
        if(pistonSet.contains(source.getType().toString())){
            return false;
        }
        if(source.getType() == Material.AIR || max == 0){
            return true;
        }
        return isSafeBlock(source.getRelative(dir), dir, max--);
    }
}