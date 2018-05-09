package emilundpixeln.what_quarry.World;

import emilundpixeln.what_quarry.util.Pair;
import net.minecraft.item.Item;

/**
 * Created by Emil_2 on 08.05.2018.
 */
public class ItemRarityPair {
    public Pair<Item, Integer> item;
    public double chance;
    ItemRarityPair(Item item, int damage, double chance)
    {
        this.item = new Pair<>(item, damage);
        this.chance = chance;
    }
}
