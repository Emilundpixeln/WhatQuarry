package emilundpixeln.what_quarry.init;

import emilundpixeln.what_quarry.Items.ItemNetherIngot;
import emilundpixeln.what_quarry.Reference;
import emilundpixeln.what_quarry.util.Utils;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;


@GameRegistry.ObjectHolder(Reference.MODID)
public class ModItems
{

    @GameRegistry.ObjectHolder(Reference.MODID + ":nether_ingot")
    public static ItemNetherIngot itemNetherIngot = new ItemNetherIngot("nether_ingot", "nether_ingot");

    public static void init()
    {
        itemNetherIngot.initModel();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class RegistrationHandler
    {
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().register(itemNetherIngot);

        }
    }
}
