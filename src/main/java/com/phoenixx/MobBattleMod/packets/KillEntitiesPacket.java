package com.phoenixx.MobBattleMod.packets;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.phoenixx.MobBattleMod.util.Team;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class KillEntitiesPacket implements IMessage {
    private int messageID;
    private String data;

    public KillEntitiesPacket() {

    }

    public KillEntitiesPacket(int number) {
        this.messageID = number;
    }

    public KillEntitiesPacket(int number, String givenData) {
        this.messageID = number;
        this.data = givenData;
    }

    public void fromBytes(ByteBuf buf) {
        this.messageID = buf.readInt();
        this.data = ByteBufUtils.readUTF8String(buf);
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.messageID);
        ByteBufUtils.writeUTF8String(buf, data);
    }

    public static class Handler implements IMessageHandler<KillEntitiesPacket, IMessage> {
        @Override
        public IMessage onMessage(KillEntitiesPacket message, MessageContext ctx) {
            if (ctx != null) {
                EntityPlayerMP player = ctx.getServerHandler().player;
                World world = player.world;

                if (player != null) {
                    if (message.messageID == 0) {

                        player.sendMessage(new TextComponentString(ChatFormatting.GREEN + "[Mob Battle]" + ChatFormatting.YELLOW + " Removing remaining mobs..."));

                        String[] parsedData = message.data.split("\\|");

                        BlockPos pos1 = new BlockPos(Integer.valueOf(parsedData[0]) + 20, Integer.valueOf(parsedData[1]) + 20, Integer.valueOf(parsedData[2]) + 20);
                        BlockPos pos2 = new BlockPos(Integer.valueOf(parsedData[0]) - 20, Integer.valueOf(parsedData[1]) - 20, Integer.valueOf(parsedData[2]) - 20);

                        AxisAlignedBB mobArea = Team.getBoundingBoxPositions(pos1, pos2);

                        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, mobArea);
                        entities.remove(player);

                        for (int i = 0; i < entities.size(); i++) {
                            entities.get(i).setDead();
                        }
                    }
                }
            }
            return null;
        }
    }
}
