package emilundpixeln.what_quarry.init;


import com.google.common.base.Preconditions;
import emilundpixeln.what_quarry.Blocks.BlockQuarry;
import emilundpixeln.what_quarry.Reference;
import emilundpixeln.what_quarry.tileentity.TileEntityQuarry;
import emilundpixeln.what_quarry.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;


import java.util.ArrayList;
import java.util.Stack;


@GameRegistry.ObjectHolder(Reference.MODID)
public class ModBlocks
{


    public static Block quarry;

    public static void init()
    {
        quarry = new BlockQuarry("quarry", "quarry");
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class RegistrationHandler
    {
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
            event.getRegistry().registerAll(quarry);
            Utils.getLogger().info("wqy: Registered blocks");


            //tile entitys
            GameRegistry.registerTileEntity(TileEntityQuarry.class, Reference.MODID + ":quarry");

        }

        @SubscribeEvent
        public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
            Preconditions.checkNotNull(quarry.getRegistryName(), "wqy: Block quarry has null registry name");
            event.getRegistry().registerAll(new ItemBlock(ModBlocks.quarry).setRegistryName(ModBlocks.quarry.getRegistryName()));
            Utils.getLogger().info("wqy: Registered ItemBlocks");


        }
    }



}
