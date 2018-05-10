package emilundpixeln.what_quarry.client.model;

import emilundpixeln.what_quarry.Reference;
import emilundpixeln.what_quarry.init.ModBlocks;
import emilundpixeln.what_quarry.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * for registering models
 */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Reference.MODID)
public class ModModelManager
{

    @SubscribeEvent
    public static void registerModels(final ModelRegistryEvent  evt) {
        Block block = ModBlocks.quarry;

        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
                new ModelResourceLocation(new ResourceLocation(
                        Reference.MODID, block.getUnlocalizedName().substring(5)), "inventory"));
        Utils.getLogger().info("wqy: Registered render for " + block.getUnlocalizedName().substring(5));
        Utils.getLogger().info("wqy: Item: " + Item.getItemFromBlock(block).getUnlocalizedName().substring(5));
    }
}