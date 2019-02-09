package com.phoenixx.MobBattleMod.util;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.phoenixx.MobBattleMod.util.handlers.FightHandler;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.List;

public class ForceStopMatchCommand implements ICommand {

    @Override
    public int compareTo(ICommand arg0) {
        return 0;
    }

    @Override
    public String getName() {
        return "stopMatch";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/stopMatch";
    }

    @Override
    public List<String> getAliases() {
        List<String> aliases = Lists.<String>newArrayList();
        aliases.add("/forceStopMatch");
        aliases.add("/closeMatch");
        aliases.add("/cancelMatch");
        aliases.add("/cm");
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if(FightHandler.started){
            FightHandler.matchStatus = EnumFight.END;
            sender.sendMessage(new TextComponentString(ChatFormatting.GREEN + "[Mob Battle] " + ChatFormatting.RESET + " Current mob battle has been stopped!"));
        } else {
            sender.sendMessage(new TextComponentString(ChatFormatting.GREEN + "[Mob Battle] " + ChatFormatting.RESET + " There is no current running mob battle to stop!"));
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }


}