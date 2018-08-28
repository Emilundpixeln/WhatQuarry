package emilundpixeln.what_quarry;

import emilundpixeln.what_quarry.commands.ComandGenTable;
import emilundpixeln.what_quarry.event.EventHandler;
import emilundpixeln.what_quarry.init.ModBlocks;
import emilundpixeln.what_quarry.init.ModItems;
import emilundpixeln.what_quarry.util.Utils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class WhatQuarry
{
    @Mod.Instance(Reference.MODID)
    public static WhatQuarry instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ModItems.init();
        ModBlocks.init();

        if(event.getSide() == Side.SERVER)
            MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        Utils.getLogger().info("wqy: Init");

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        Utils.getLogger().info("wqy: Post init");
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new ComandGenTable());
        Utils.getLogger().info("wqy: registered command");
    }

}
