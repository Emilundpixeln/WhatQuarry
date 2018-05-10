package emilundpixeln.what_quarry.commands;

import emilundpixeln.what_quarry.tileentity.TileEntityQuarry;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/*
* Command that generates the table for the quarry
* just calls TileEntityQuarry.regenTables
* */

public class ComandGenTable implements ICommand
{
    @Override
    public boolean isUsernameIndex(String[] strings, int i) {
        return false;
    }

    @Override
    public String getName() {
        return "whatQuarryGenTable";
    }

    @Override
    public String getUsage(ICommandSender iCommandSender) {
        return "dimId, numChunksX, numChunksY";
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>();
    }

    @Override
    public void execute(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] strings) throws CommandException
    {
        EntityPlayer player = (EntityPlayer)iCommandSender;
        if(strings.length == 3)
        {
            try {

                TileEntityQuarry.regenTables(Integer.parseInt(strings[0]),
                        Integer.parseInt(strings[1]),
                        Integer.parseInt(strings[2]),
                        player);
            }
            catch (NumberFormatException e)
            {
                player.sendMessage(new TextComponentTranslation("command.gen_tables.usage"));
            }
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer minecraftServer, ICommandSender iCommandSender) {
        return iCommandSender instanceof EntityPlayer;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer minecraftServer, ICommandSender iCommandSender, String[] strings, @Nullable BlockPos blockPos) {
        return new ArrayList<>();
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
