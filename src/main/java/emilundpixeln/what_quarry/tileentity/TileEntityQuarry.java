package emilundpixeln.what_quarry.tileentity;

import cofh.redstoneflux.api.IEnergyReceiver;
import com.mojang.authlib.GameProfile;
import emilundpixeln.what_quarry.QuarryTable;
import emilundpixeln.what_quarry.World.ItemRarityPair;
import emilundpixeln.what_quarry.World.ItemsWorldSavedData;
import emilundpixeln.what_quarry.util.MapUtil;
import emilundpixeln.what_quarry.util.Utils;
import emilundpixeln.what_quarry.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.*;

/*
* Tile entity for the Quarry
* the quarry randomly selects items instead of mining
* uses rf and outputs to inventory above the quarry
* */

public class TileEntityQuarry extends TileEntity implements ITickable, IEnergyReceiver
{

    private boolean active;
    private IItemHandler output;
    private static Random rnd;
    private EnergyStorage storage = new EnergyStorage(100000000);
    private static final int RF_PER_RUN = 20000;
    private static boolean loaded = false;


    private static Set<Pair<Item, Integer>> blacklist;


    public TileEntityQuarry()
    {
        output = null;


        if(rnd == null)
        {
            rnd = new Random();
            rnd.setSeed(System.currentTimeMillis());
        }

        if(blacklist == null)
        {
            blacklist = new HashSet<>();
            blacklist.add(new Pair<>(Item.getItemFromBlock(Blocks.STONE), 0));
        }
    }

    /*
    * loads data from WorldSavedData
    * */
    private void getData()
    {
        loaded = true;

        ItemsWorldSavedData data = ItemsWorldSavedData.get(world);
        if(data == null || !data.isReady())
        {
            loaded = false;
            return;
        }


        blacklist = new HashSet<>();
        int size = data.getBlacklistSize();
        for (int i = 0; i < size; i++) {
            blacklist.add(data.getBlacklistItem(i));
        }
    }
    /*
    * gets Handler of inventory for outputting
    * */
    private boolean getHandler()
    {

        TileEntity chestContainer = world.getTileEntity(pos.up());
        if(chestContainer != null && chestContainer.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {

            output = chestContainer.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            return true;
        }
        return false;
    }


    private ItemStack getNextItem()
    {

        double value = rnd.nextDouble();
        NavigableMap<Double, Pair<Item, Integer>> table = QuarryTable.getTable(world);
        NavigableMap.Entry<Double, Pair<Item, Integer>> entry = table.higherEntry(value);

        Pair<Item, Integer> gotItem = entry.getValue();
        ItemStack ret = new ItemStack(gotItem.getKey());
        if(gotItem.getKey() != Item.getItemFromBlock(Blocks.LAPIS_ORE))
            ret.setItemDamage(gotItem.getValue());
        if(blacklist.contains(gotItem))
            return null;
        return ret;
    }


    @Override
    public void update()
    {
        if(world.isRemote)
            return;

        active = !world.isBlockPowered(pos);
        if(!loaded)
        {
            // load
            getData();
        }
        int runs = storage.getEnergyStored() / RF_PER_RUN;
        if (active && runs > 0) {


            if(!getHandler())
                return;
            int irun;
            for (irun = 0; irun < runs; irun++) {
                ItemStack item = getNextItem();
                if(item == null)
                    continue;

                //find slot where the items can be inserted
                for (int i = 0; i < output.getSlots(); i++) {

                    item = output.insertItem(i, item, false);
                    if(item == ItemStack.EMPTY)
                        break;
                }
                //don't count run if item couldn't be inserted
                if(item != ItemStack.EMPTY)
                {
                    irun--;
                    break;
                }
            }
            irun++;
            storage.extractEnergy(irun * RF_PER_RUN,false);

        }
    }


    public boolean onBlockActivated(EntityPlayer playerIn, @Nullable ItemStack heldItem)
    {
        boolean addItem = true;
        if(heldItem == null || heldItem == ItemStack.EMPTY)
        {
            addItem = false;
        }

        // add held item to blacklist (remove if already blacklisted
        if(addItem)
        {
            Pair<Item, Integer> pair = Utils.pairFromItemStack(heldItem);
            Utils.getLogger().info("qwy: " + heldItem.getDisplayName());
            ItemsWorldSavedData data = ItemsWorldSavedData.get(world);
            int size = data.getBlacklistSize();
            if(blacklist.contains(pair))
            {

                blacklist.remove(pair);
                data.resetBlacklist();
                data.setBlacklistSize(size - 1);
                Iterator<Pair<Item, Integer>> iterator = blacklist.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    Pair<Item, Integer> it = iterator.next();
                    if(it != pair)
                        data.addBlacklistTag(i++, it);
                }
            }
            else
            {
                blacklist.add(pair);
                data.addBlacklistTag(size, pair);
                data.setBlacklistSize(size + 1);

            }
            data.markDirty();
        }

        //print some info
        playerIn.sendMessage(new TextComponentTranslation("tile.quarry.info"));
        playerIn.sendMessage(new TextComponentTranslation("tile.quarry.rf", Integer.toString(storage.getEnergyStored())));
        playerIn.sendMessage(new TextComponentTranslation("tile.quarry.blacklist"));

        Iterator<Pair<Item, Integer>> iterator = blacklist.iterator();
        while (iterator.hasNext()) {
            playerIn.sendMessage(new TextComponentTranslation("tile.quarry.item",
                    Utils.itemStackFromPair(iterator.next()).getDisplayName()));
        }


        return true;
    }


    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        active = nbt.getBoolean("active");
        storage.receiveEnergy(nbt.getInteger("energy"), false);

        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {


        nbt.setBoolean("active", active);
        nbt.setInteger("energy", storage.getEnergyStored());


        return super.writeToNBT(nbt);
    }


    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        int metadata = getBlockMetadata();
        return new SPacketUpdateTileEntity(this.pos, metadata, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound getTileData() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return nbt;
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return storage.getMaxEnergyStored();
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return storage.receiveEnergy(maxReceive, simulate);
    }
}
