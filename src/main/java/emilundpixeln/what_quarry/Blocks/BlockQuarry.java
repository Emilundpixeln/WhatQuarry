package emilundpixeln.what_quarry.Blocks;

import emilundpixeln.what_quarry.Reference;
import emilundpixeln.what_quarry.tileentity.TileEntityQuarry;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;


public class BlockQuarry extends Block implements ITileEntityProvider
{
    public BlockQuarry(String unlocalizedName, String registryName)
    {
        super(Material.IRON);
        this.setUnlocalizedName(unlocalizedName);
        this.setRegistryName(new ResourceLocation(Reference.MODID, registryName));
        this.setHardness(2);
        this.setResistance(2);
    }

    @Override
    public Item getItemDropped(IBlockState p_getItemDropped_1_, Random p_getItemDropped_2_, int p_getItemDropped_3_) {
        return Item.getItemFromBlock(this);
    }

    // pass to the tile entitys onBlockActivated
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState p_onBlockActivated_3_, EntityPlayer playerIn, EnumHand enumHand, EnumFacing p_onBlockActivated_6_, float p_onBlockActivated_7_, float p_onBlockActivated_8_, float p_onBlockActivated_9_) {
        if(worldIn.isRemote)
            return true;
        ItemStack heldItem = playerIn.getHeldItemMainhand();

        return ((TileEntityQuarry)worldIn.getTileEntity(pos)).onBlockActivated(playerIn, heldItem);
    }


    @Nullable
    @Override
    public TileEntity createTileEntity(World p_createTileEntity_1_, IBlockState p_createTileEntity_2_) {
        return new TileEntityQuarry();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntityQuarry();
    }


}
