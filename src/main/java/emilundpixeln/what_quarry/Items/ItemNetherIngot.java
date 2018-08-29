package emilundpixeln.what_quarry.Items;

import emilundpixeln.what_quarry.Reference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Item used for crafting
 */
public class ItemNetherIngot extends Item
{
    public ItemNetherIngot(String unlocalizedName, String registryName)
    {

         setUnlocalizedName(unlocalizedName);
         setRegistryName(Reference.MODID, registryName);

    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
