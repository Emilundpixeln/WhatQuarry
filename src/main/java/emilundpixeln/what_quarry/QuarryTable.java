package emilundpixeln.what_quarry;

import com.mojang.authlib.GameProfile;
import emilundpixeln.what_quarry.World.ItemRarityPair;
import emilundpixeln.what_quarry.World.ItemsWorldSavedData;
import emilundpixeln.what_quarry.util.MapUtil;
import emilundpixeln.what_quarry.util.Pair;
import emilundpixeln.what_quarry.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import java.util.*;

public class QuarryTable {


    private static NavigableMap<Double, Pair<Item, Integer>> m_items = null;

    public static NavigableMap<Double, Pair<Item, Integer>> getTable(World world) {
        if(m_items == null)
        {
            // fallback
            /*
            m_items = new TreeMap<>();
            m_items.put(1.0, new Pair<>(Item.getItemFromBlock(Blocks.STONE), 0));
            */
            loadData(world);
        }
        if(m_items.size() == 0)
        {
            int i = 0;
        }

        return m_items;
    }

    private static void loadData(World world)
    {
        // load from world save data
        ItemsWorldSavedData data = ItemsWorldSavedData.get(world);

        m_items = new TreeMap<>();
        for (int i = 0; i < data.getSize(); i++) {
            ItemRarityPair ir = data.getItem(i);
            m_items.put(ir.chance, ir.item);
        }
    }


    public static void onWorldLoad(World world)
    {
        loadData(world);
    }


    /*
        * scans a area to determine how likely it is for any block to be found
        * that information is than used for generating new items
        * */
    public static void regenTables(int dim, int XWidth, int ZWidth, List<EntityPlayer> inPlayer)
    {
        Utils.getLogger().info("qwy: exec command " + dim + " " + XWidth + " " + ZWidth);
        WorldServer world = DimensionManager.getWorld(dim);
        if(world == null)
            return;
        // fortune level to be applied on nonsilkable blocks
        int fortune = 0;
        Map<Pair<Item, Integer>, Integer /* Count*/> counts = new HashMap<>();
        // Creates a fake player which will break the block
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


        //scan area
        for (int zchunk = 0; zchunk < ZWidth; zchunk++) {
            for (int xchunk = 0; xchunk < XWidth; xchunk++) {
                for (int inChunkZ = 0; inChunkZ < 16; inChunkZ++) {
                    for (int inChunkX = 0; inChunkX < 16; inChunkX++) {
                        for (int inChunkY = 0; inChunkY < 16; inChunkY++) {

                            BlockPos p = new BlockPos(xchunk * 16 + inChunkX, inChunkY, zchunk * 16 + inChunkZ);
                            IBlockState state = world.getBlockState(p);

                            Block block = state.getBlock();

                            // avoid air, liquids...
                            if(!block.isAir(state, world, p) && block.getBlockHardness(state, world, p) >= 0
                                    && !(block instanceof BlockDynamicLiquid) && !(block instanceof BlockStaticLiquid))
                            {

                                Pair<Item, Integer> key;
                                boolean silk = false;
                                // test if we can silk the block
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




                                // add block
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

        // determine how likely every item is
        // first find sum
        double all = 0;
        for (Map.Entry<Pair<Item, Integer>, Integer> entry : counts.entrySet())
        {
            all += entry.getValue();

        }

        // debug
        Map<Pair<Item, Integer>, Integer> sortedrevCount = MapUtil.sortByValue(counts, true);
        for (Map.Entry<Pair<Item, Integer>, Integer> entry : sortedrevCount.entrySet()) {
            ItemStack itemstack = new ItemStack(entry.getKey().getKey());
            itemstack.setItemDamage(entry.getKey().getValue());

            Utils.getLogger().info(String.format("qwy:%-25s%13s%% | amount: %9s/%s",
                    itemstack.getDisplayName(),
                    String.format("%.8f", (double) entry.getValue() / all * 100),
                    entry.getValue(),
                    (long) all));
        }


        // put table into tree map for easy access
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
        m_items = items;

        //send information to player to inform that the command has finished
        inPlayer.forEach(entityPlayer ->
                entityPlayer.sendStatusMessage(new TextComponentTranslation("command.gen_tables.finished_gen"), true));
    }

}
