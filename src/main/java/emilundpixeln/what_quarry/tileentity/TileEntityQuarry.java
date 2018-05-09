package emilundpixeln.what_quarry.tileentity;

import cofh.redstoneflux.api.IEnergyReceiver;
import com.mojang.authlib.GameProfile;
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

public class TileEntityQuarry extends TileEntity implements ITickable, IEnergyReceiver
{

    private boolean active;
    private IItemHandler output;
    private static Random rnd;
    private EnergyStorage storage = new EnergyStorage(100000000);
    private static final int RF_PER_RUN = 20000;
    private static boolean loaded = false;


    private static NavigableMap<Double, Pair<Item, Integer>> item;
    private static Set<Pair<Item, Integer>> blacklist;


    public TileEntityQuarry()
    {
        output = null;
        if(item == null)
        {

            item = new TreeMap<>();
            item.put(1.0, new Pair<>(Item.getItemFromBlock(Blocks.STONE), 0));

        }

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

    private void getData()
    {
        loaded = true;

        ItemsWorldSavedData data = ItemsWorldSavedData.get(world);
        if(data == null || !data.isReady())
        {
            loaded = false;
            return;
        }
        item = new TreeMap<>();
        for (int i = 0; i < data.getSize(); i++) {
            ItemRarityPair ir = data.getItem(i);
            item.put(ir.chance, ir.item);
        }

        blacklist = new HashSet<>();
        int size = data.getBlacklistSize();
        for (int i = 0; i < size; i++) {
            blacklist.add(data.getBlacklistItem(i));
        }
    }

    private boolean getHandler()
    {

        TileEntity chestContainer = world.getTileEntity(pos.up());
        if(chestContainer != null && chestContainer.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {

            output = chestContainer.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            return true;
        }
        return false;
    }

    public static void regenTables(int dim, int XWidth, int ZWidth)
    {
        Utils.getLogger().info("qwy: exec command " + dim + " " + XWidth + " " + ZWidth);
        WorldServer world = DimensionManager.getWorld(dim);
        if(world == null)
            return;
        int fortune = 0;
        Map<Pair<Item, Integer>, Integer /* Count*/> counts = new HashMap<>();
        // Creates a fake player which will berak the block
        EntityPlayer player = new EntityPlayer(world, new GameProfile(null, "BlockBreaker")) {

            @Override
            public boolean isSpectator() {
                return true;
            }

            @Override
            public boolean isCreative() {
                return false;
            }
        };

        for (int zchunk = 0; zchunk < ZWidth; zchunk++) {
            for (int xchunk = 0; xchunk < XWidth; xchunk++) {
                for (int inChunkZ = 0; inChunkZ < 16; inChunkZ++) {
                    for (int inChunkX = 0; inChunkX < 16; inChunkX++) {
                        for (int inChunkY = 0; inChunkY < 16; inChunkY++) {

                            BlockPos p = new BlockPos(xchunk * 16 + inChunkX, inChunkY, zchunk * 16 + inChunkZ);
                            IBlockState state = world.getBlockState(p);
                            Block block = state.getBlock();

                            if(!block.isAir(state, world, p) && block.getBlockHardness(state, world, p) >= 0
                                    && !(block instanceof BlockDynamicLiquid) && !(block instanceof BlockStaticLiquid))
                            {

                                Pair<Item, Integer> key;
                                boolean silk = false;
                                if(block.canSilkHarvest(world, p, state, player))
                                {

                                    key = new Pair<>(Item.getItemFromBlock(block),
                                            block.damageDropped(state));
                                    silk = true;
                                }
                                else
                                {
                                    Item drop = block.getItemDropped(state,  world.rand, 0);
                                    key = new Pair<>(drop, block.damageDropped(state));
                                }



                                int cur = 0;
                                if(counts.containsKey(key))
                                    cur = counts.get(key);

                                counts.put(key,
                                        cur + (silk ? 1 : block.quantityDropped(state, fortune, world.rand)));
                            }


                        }
                    }
                }
            }
        }
        double all = 0;

        for (Map.Entry<Pair<Item, Integer>, Integer> entry : counts.entrySet())
        {
            all += entry.getValue();

        }
        // debug
        Map<Pair<Item, Integer>, Integer> sortedrevCount = MapUtil.sortByValue(counts, true);
        for (Map.Entry<Pair<Item, Integer>, Integer> entry : sortedrevCount.entrySet())
        {
            ItemStack itemstack = new ItemStack(entry.getKey().getKey());
            itemstack.setItemDamage(entry.getKey().getValue());

            Utils.getLogger().info(String.format("qwy:%-25s%13s%% | amount: %9s/%s",
                    itemstack.getDisplayName(),
                    String.format("%.8f", (double)entry.getValue() / all * 100),
                    entry.getValue(),
                    (long)all));
            //debug::
            //world.playerEntities.get(0).addItemStackToInventory(itemstack);
        }

        Map<Pair<Item, Integer>, Integer> sortedCount = MapUtil.sortByValue(counts, false);
        NavigableMap<Double, Pair<Item, Integer>> items = new TreeMap<>();
        double sum = 0;
        ItemsWorldSavedData data = ItemsWorldSavedData.get(world);
        data.setItemSize(sortedCount.size());

        int j = 0;
        for (Map.Entry<Pair<Item, Integer>, Integer> entry : sortedCount.entrySet())
        {
            sum += (double)entry.getValue() / all;
            items.put(sum, entry.getKey());
            data.addItemTag(j++, entry.getKey(), sum);
        }
        data.markReady();
        data.markDirty();
        item = items;



        //check if it works
        /*
        Map<Pair<Item, Integer>, Integer > countsTested = new HashMap<>();

        for (int i = 0; i < all; i++) {
            ItemStack itemstack = getNextItem();
            Pair<Item, Integer> key = new Pair<>(itemstack.getItem(), itemstack.getItemDamage());
            int cur = 0;
            if(countsTested.containsKey(key))
                cur = countsTested.get(key);

            countsTested.put(key,
                    cur + 1);
        }
        // debug
        Utils.getLogger().info("qwy: Diff:");
        Map<Pair<Item, Integer>, Integer> sortedrevCountTested = MapUtil.sortByValue(countsTested, true);
        double sumErr = 0;
        for (Map.Entry<Pair<Item, Integer>, Integer> entry : sortedrevCountTested.entrySet())
        {
            ItemStack itemstack = new ItemStack(entry.getKey().getKey());
            itemstack.setItemDamage(entry.getKey().getValue());
            double err = (double)entry.getValue() / all * 100 - (double)(counts.get(entry.getKey())) / all * 100;
            Utils.getLogger().info(String.format("qwy: :%-25s%13s%%",
                    itemstack.getDisplayName(),
                    String.format("%.8f", err)));
            sumErr += Math.abs(err);
        }
        Utils.getLogger().info("qwy: average Error: " + sumErr / sortedrevCountTested.size());
        */
    }

    private static ItemStack getNextItem()
    {

        double value = rnd.nextDouble();
        Pair<Item, Integer> gotItem = item.higherEntry(value).getValue();
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
            getData();
        int runs = storage.getEnergyStored() / RF_PER_RUN;
        if (active && runs > 0) {


            if(!getHandler())
                return;
            int irun;
            for (irun = 0; irun < runs; irun++) {
                ItemStack item = getNextItem();
                if(item == null)
                    continue;

                for (int i = 0; i < output.getSlots(); i++) {

                    item = output.insertItem(i, item, false);
                    if(item == ItemStack.EMPTY)
                        break;
                }
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
