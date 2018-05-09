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
    public static Item itemNetherIngot ;
    public static void init()
    {
        itemNetherIngot = new ItemNetherIngot("nether_ingot", "nether_ingot");
    }

    public static void register()
    {

    }




    public static void registerRenders()
    {
        registerRender(itemNetherIngot);
    }

    public static void registerRender(Item item)
    {
        ModelLoader.setCustomModelResourceLocation(item , 0,
                new ModelResourceLocation(new ResourceLocation(
                        Reference.MODID, item.getUnlocalizedName().substring(5)), "inventory"));
        Utils.getLogger().info("wqy: Registered render for " + item.getUnlocalizedName().substring(5));
    }
    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class RegistrationHandler
    {
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(itemNetherIngot);

        }
    }
}
