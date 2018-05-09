package emilundpixeln.what_quarry.World;


import emilundpixeln.what_quarry.Reference;
import emilundpixeln.what_quarry.util.Pair;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class ItemsWorldSavedData extends WorldSavedData {
    private static final String DATA_NAME = Reference.MODID + "_ItemsData";
    private NBTTagCompound data = new NBTTagCompound();

    public ItemsWorldSavedData()
    {
        super(DATA_NAME);
        data.setBoolean("Is_ok", false);

        data.setTag("[Blacklist]", new NBTTagCompound());
        data.getCompoundTag("[Blacklist]").setInteger("blacklist_size", 1);
        addBlacklistTag(0, new Pair<>(Item.getItemFromBlock(Blocks.STONE), 0));
    }
    public ItemsWorldSavedData(String s)
    {
        super(s);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        data = compound.getCompoundTag(DATA_NAME + "_tag");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag(DATA_NAME + "_tag", data);
        return compound;
    }

    public NBTTagCompound getData() {
        return data;
    }

    public void setItemSize(int size)
    {
        data.setInteger("itemsSize", size);
    }

    public void markReady()
    {
        data.setBoolean("Is_ok", true);
    }

    public boolean isReady()
    {
        return data.getBoolean("Is_ok");
    }

    public int getSize()
    {
        return data.getInteger("itemsSize");
    }

    public void addItemTag(int i, Pair<Item, Integer> item, double chance) {
        String s = "[" + i + "]";
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("itemId", Item.getIdFromItem(item.getKey()));
        compound.setInteger("damage", item.getValue());
        compound.setDouble("chance", chance);
        data.setTag(s, compound);
    }

    public ItemRarityPair getItem(int i)
    {

        String s = "[" + i + "]";
        NBTTagCompound compound = (NBTTagCompound)data.getTag(s);
        return new ItemRarityPair(
                Item.getItemById(compound.getInteger("itemId")),
                        compound.getInteger("damage"),
                compound.getDouble("chance"));

    }
    public void setBlacklistSize(int size)
    {
        data.getCompoundTag("[Blacklist]").setInteger("blacklist_size", size);
    }
    public int getBlacklistSize()
    {
        return data.getCompoundTag("[Blacklist]").getInteger("blacklist_size");
    }
    public void addBlacklistTag(int i, Pair<Item, Integer> item) {
        String s = "[" + i + "]";
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("itemId", Item.getIdFromItem(item.getKey()));
        compound.setInteger("damage", item.getValue());
        data.getCompoundTag("[Blacklist]").setTag(s, compound);
    }
    public Pair<Item, Integer> getBlacklistItem(int i)
    {

        String s = "[" + i + "]";
        NBTTagCompound compound = (NBTTagCompound)data.getCompoundTag("[Blacklist]").getTag(s);
        return new Pair<>(
                Item.getItemById(compound.getInteger("itemId")),
                compound.getInteger("damage"));

    }

    public void resetBlacklist()
    {
        data.setTag("[Blacklist]", new NBTTagCompound());
        data.getCompoundTag("[Blacklist]").setInteger("blacklist_size", 0);
    }


    public static ItemsWorldSavedData get(World world) {
        MapStorage storage = world.getMapStorage();
        ItemsWorldSavedData instance;
        try {
            instance = (ItemsWorldSavedData) storage.getOrLoadData(ItemsWorldSavedData.class, DATA_NAME);
        }
        catch (NullPointerException e)
        {
            return null;
        }

        if (instance == null) {
            instance = new ItemsWorldSavedData();
            storage.setData(DATA_NAME, instance);
        }
        return instance;
    }
}
