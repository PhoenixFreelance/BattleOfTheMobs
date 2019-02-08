package com.phoenixx.MobBattleMod.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;

public class EntityDataPacket implements IMessage
{
    private int messageID;
    private String teamOneEntityData;
    private String teamTwoEntityData;

    public EntityDataPacket()
    {

    }

    public EntityDataPacket(int number)
    {
        this.messageID = number;
    }

    public EntityDataPacket(int number, String givenTeamOneData, String givenTeamTwoData)
    {
        this.messageID = number;
        this.teamOneEntityData = givenTeamOneData;
        this.teamTwoEntityData = givenTeamTwoData;
    }

    public void fromBytes(ByteBuf buf)
    {
        this.messageID = buf.readInt();
        this.teamOneEntityData = ByteBufUtils.readUTF8String(buf);
        this.teamTwoEntityData = ByteBufUtils.readUTF8String(buf);
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.messageID);
        ByteBufUtils.writeUTF8String(buf, teamOneEntityData);
        ByteBufUtils.writeUTF8String(buf, teamTwoEntityData);
    }

    public static class Handler implements IMessageHandler<EntityDataPacket, IMessage>
    {
        @Override
        public IMessage onMessage(EntityDataPacket message, MessageContext ctx)
        {
            if(ctx != null)
            {
                EntityPlayer player = Minecraft.getMinecraft().player;
                World world = player.world;
                if(player != null)
                {
                    if(message.messageID == 0)
                    {
                        String[] parsedTeamOneData = message.teamOneEntityData.split("\\|");
                        String[] parsedTeamTwoData = message.teamTwoEntityData.split("\\|");

                        ArrayList<EntityCreature> teamOneList = new ArrayList<>();
                        ArrayList<EntityCreature> teamTwoList = new ArrayList<>();

                        for(String entityID: parsedTeamOneData){
                            EntityCreature entity = (EntityCreature) world.getEntityByID(Integer.parseInt(entityID));
                            teamOneList.add(entity);
                        }

                        for(String entityID: parsedTeamTwoData){
                            EntityCreature entity = (EntityCreature) world.getEntityByID(Integer.parseInt(entityID));
                            teamTwoList.add(entity);
                        }

                        System.out.println("EntityDataPacket TEAM ONE: ["+ teamOneList.size() + "]" + teamOneList);
                        System.out.println("EntityDataPacket TEAM TWO: ["+ teamTwoList.size() + "]"+ teamTwoList);

                        //FightHandler.setTeamOneList(teamOneList);
                        //FightHandler.setTeamTwoList(teamTwoList);

                        /*BlockPos teamOnePos1 = new BlockPos(Integer.valueOf(parsedBlockData[0]) + 5, Integer.valueOf(parsedBlockData[1]), Integer.valueOf(parsedBlockData[2]) + 5);
                        BlockPos teamOnePos2 = new BlockPos(Integer.valueOf(parsedBlockData[0]) + 10, Integer.valueOf(parsedBlockData[1]), Integer.valueOf(parsedBlockData[2]) + 10);

                        BlockPos teamTwoPos1 = new BlockPos(Integer.valueOf(parsedBlockData[0]) - 5, Integer.valueOf(parsedBlockData[1]), Integer.valueOf(parsedBlockData[2]) - 5);
                        BlockPos teamTwoPos2 = new BlockPos(Integer.valueOf(parsedBlockData[0]) - 10, Integer.valueOf(parsedBlockData[1]), Integer.valueOf(parsedBlockData[2]) - 10);

                        AxisAlignedBB teamOneSpawnBB = Team.getBoundingBoxPositions(teamOnePos1, teamOnePos2);
                        AxisAlignedBB teamTwoSpawnBB = Team.getBoundingBoxPositions(teamTwoPos1, teamTwoPos2);

                        List<EntityCreature> teamOneList = world.getEntitiesWithinAABB(EntityCreature.class, teamOneSpawnBB);
                        List<EntityCreature> teamTwoList = world.getEntitiesWithinAABB(EntityCreature.class, teamTwoSpawnBB);

                        System.out.println("EntityDataPacket TEAM ONE: " + teamOneList);
                        System.out.println("EntityDataPacket TEAM TWO: " + teamTwoList);

                        FightHandler.setTeamOneList((ArrayList<EntityCreature>) teamOneList);
                        FightHandler.setTeamTwoList((ArrayList<EntityCreature>) teamTwoList);*/

                    }
                }
            }
            return null;
        }
    }
}