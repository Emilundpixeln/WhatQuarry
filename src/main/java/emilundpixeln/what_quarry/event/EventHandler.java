package emilundpixeln.what_quarry.event;

import emilundpixeln.what_quarry.QuarryTable;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Emil_2 on 28.08.2018.
 */
public class EventHandler
{

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        QuarryTable.onWorldLoad(event.getWorld());
    }
}
