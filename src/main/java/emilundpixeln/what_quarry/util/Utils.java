package emilundpixeln.what_quarry.util;


import emilundpixeln.what_quarry.Reference;
import emilundpixeln.what_quarry.util.Pair;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Utils {
    private static Logger logger;

    public static org.apache.logging.log4j.Logger getLogger() {
        if(logger == null)
            logger = LogManager.getFormatterLogger(Reference.MODID);
        return logger;
    }

    public static ItemStack itemStackFromPair(Pair<Item, Integer> item)
    {
        ItemStack itemStack = new ItemStack(item.getKey());
        itemStack.setItemDamage(item.getValue());
        return itemStack;
    }

    public static Pair<Item, Integer> pairFromItemStack(ItemStack item)
    {
        return new Pair<>(item.getItem(), item.getItemDamage());
    }
}
