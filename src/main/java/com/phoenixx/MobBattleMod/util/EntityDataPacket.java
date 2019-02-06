package com.phoenixx.MobBattleMod.util;

import com.phoenixx.MobBattleMod.entities.Team;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;
import java.util.Random;

public class EntityDataPacket implements IMessage
{
    private int messageID;
    private String teamOneData;
    private String teamTwoData;

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
        this.teamOneData = givenTeamOneData;
        this.teamTwoData = givenTeamTwoData;
    }

    public void fromBytes(ByteBuf buf)
    {
        this.messageID = buf.readInt();
        this.teamOneData = ByteBufUtils.readUTF8String(buf);
        this.teamTwoData = ByteBufUtils.readUTF8String(buf);
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.messageID);
        ByteBufUtils.writeUTF8String(buf, teamOneData);
        ByteBufUtils.writeUTF8String(buf, teamTwoData);
    }

    public static class Handler implements IMessageHandler<EntityDataPacket, IMessage>
    {
        @Override
        public IMessage onMessage(EntityDataPacket message, MessageContext ctx)
        {
            if(ctx != null)
            {
                EntityPlayerMP player = ctx.getServerHandler().player;
                World world = player.world;

                if(player != null)
                {
                    if(message.messageID == 0)
                    {
                        String test = "";
                        String[] parsedBlockData = test.split("\\|");
                        String[] parsedTeamOneData = message.teamOneData.split("\\|");
                        String[] parsedTeamTwoData = message.teamTwoData.split("\\|");

                        BlockPos teamOnePos1 = new BlockPos(Integer.valueOf(parsedBlockData[0]) + 5, Integer.valueOf(parsedBlockData[1]), Integer.valueOf(parsedBlockData[2]) + 5);
                        BlockPos teamOnePos2 = new BlockPos(Integer.valueOf(parsedBlockData[0]) + 10, Integer.valueOf(parsedBlockData[1]), Integer.valueOf(parsedBlockData[2]) + 10);

                        BlockPos teamTwoPos1 = new BlockPos(Integer.valueOf(parsedBlockData[0]) - 5, Integer.valueOf(parsedBlockData[1]), Integer.valueOf(parsedBlockData[2]) - 5);
                        BlockPos teamTwoPos2 = new BlockPos(Integer.valueOf(parsedBlockData[0]) - 10, Integer.valueOf(parsedBlockData[1]), Integer.valueOf(parsedBlockData[2]) - 10);

                        AxisAlignedBB teamOneSpawnBB = Team.getBoundingBoxPositions(teamOnePos1, teamOnePos2);
                        AxisAlignedBB teamTwoSpawnBB = Team.getBoundingBoxPositions(teamTwoPos1, teamTwoPos2);

                        // TEAM ONE SPAWN
                        for(String entityName: parsedTeamOneData){
                            spawnCreature(world, entityName, generateRandomCord(teamOneSpawnBB.minX, teamOneSpawnBB.maxX),Integer.valueOf(parsedBlockData[1]), generateRandomCord(teamOneSpawnBB.minZ, teamOneSpawnBB.maxZ), "Team One");
                        }

                        // TEAM TWO SPAWN
                        for(String entityName: parsedTeamTwoData){
                            spawnCreature(world, entityName, generateRandomCord(teamTwoSpawnBB.minX, teamTwoSpawnBB.maxX),Integer.valueOf(parsedBlockData[1]), generateRandomCord(teamTwoSpawnBB.minZ, teamTwoSpawnBB.maxZ), "Team Two");
                        }
                    }
                }
            }
            return null;
        }
    }

    /**
     * Spawns the creature specified by the egg's type in the location specified by the last three parameters.
     * Parameters: world, entityID, x, y, z.
     */
    @Nullable
    public static Entity spawnCreature(World worldIn, String entityNameID, double x, double y, double z, String team)
    {
        Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(entityNameID), worldIn);
        if (entity instanceof EntityLiving)
        {
            System.out.println("spawnCreature METHOD: Spawning " + entity.getDisplayName() + " at X: " + x + " Y: " + y + " Z:" + z);
            EntityLiving entityliving = (EntityLiving)entity;
            entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);
            entityliving.rotationYawHead = entityliving.rotationYaw;
            entityliving.renderYawOffset = entityliving.rotationYaw;
            entityliving.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entityliving)), (IEntityLivingData)null);
            worldIn.spawnEntity(entity);
            entityliving.playLivingSound();

            Team.updateEntity(team, (EntityCreature) entity);
        }
        return entity;
    }

    public static int generateRandomCord(double min, double max) {
        Random r = new Random();
        return (int) (r.nextInt((int) ((max - min) + 1)) + min);
    }
}