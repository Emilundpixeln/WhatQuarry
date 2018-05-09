package emilundpixeln.what_quarry.proxy;

import emilundpixeln.what_quarry.init.ModBlocks;
import emilundpixeln.what_quarry.init.ModItems;

/**
 * Created by Emil_2 on 05.05.2018.
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void registerRenders()
    {
        ModItems.registerRenders();
        ModBlocks.registerRenders();
    }
}
