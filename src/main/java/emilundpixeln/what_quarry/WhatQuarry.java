package emilundpixeln.what_quarry;

import emilundpixeln.what_quarry.commands.ComandGenTable;
import emilundpixeln.what_quarry.init.ModBlocks;
import emilundpixeln.what_quarry.init.ModItems;
import emilundpixeln.what_quarry.proxy.CommonProxy;
import emilundpixeln.what_quarry.util.Utils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;


@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class WhatQuarry
{
    @Mod.Instance(Reference.MODID)
    public static WhatQuarry instance;


    @SidedProxy(serverSide = Reference.SERVER_PROXY_CLASS, clientSide = Reference.CLIENT_PROXY_CLASS)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ModItems.init();
        ModBlocks.init();
        // now events
        //ModItems.register();
        //ModBlocks.register();
        proxy.registerRenders();

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
