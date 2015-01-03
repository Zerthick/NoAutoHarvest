/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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