package com.phoenixx.MobBattleMod.util;

import com.phoenixx.MobBattleMod.MobBattleMod;
import com.phoenixx.MobBattleMod.entities.Team;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
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
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Random;

public class SpawnEntityPacket implements IMessage
{
    private int messageID;
    private String blockData;
    private String teamOneData;
    private String teamTwoData;

    private static ArrayList<String> teamOneList = new ArrayList<>();
    private static ArrayList<String> teamTwoList = new ArrayList<>();

    public SpawnEntityPacket()
    {

    }

    public SpawnEntityPacket(int number)
    {
        this.messageID = number;
    }

    public SpawnEntityPacket(int number, String givenBlockData, String givenTeamOneData, String givenTeamTwoData, String givenTeamOneName, String givenTeamTwoName)
    {
        this.messageID = number;
        this.blockData = givenBlockData;
        this.teamOneData = givenTeamOneData;
        this.teamTwoData = givenTeamTwoData;
    }

    public void fromBytes(ByteBuf buf)
    {
        this.messageID = buf.readInt();
        this.blockData = ByteBufUtils.readUTF8String(buf);
        this.teamOneData = ByteBufUtils.readUTF8String(buf);
        this.teamTwoData = ByteBufUtils.readUTF8String(buf);
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.messageID);
        ByteBufUtils.writeUTF8String(buf, blockData);
        ByteBufUtils.writeUTF8String(buf, teamOneData);
        ByteBufUtils.writeUTF8String(buf, teamTwoData);
    }

    public static class Handler implements IMessageHandler<SpawnEntityPacket, IMessage>
    {
        @Override
        public IMessage onMessage(SpawnEntityPacket message, MessageContext ctx)
        {
            if(ctx != null)
            {
                EntityPlayerMP player = ctx.getServerHandler().player;
                World world = player.world;

                if(player != null)
                {
                    if(message.messageID == 0)
                    {
                        if(!world.isRemote){
                            String[] parsedBlockData = message.blockData.split("\\|");
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
                                spawnCreature(world, entityName, generateRandomCord(teamOneSpawnBB.minX, teamOneSpawnBB.maxX),Integer.valueOf(parsedBlockData[1]), generateRandomCord(teamOneSpawnBB.minZ, teamOneSpawnBB.maxZ), parsedBlockData[3], true);
                            }

                            // TEAM TWO SPAWN
                            for(String entityName: parsedTeamTwoData){
                                spawnCreature(world, entityName, generateRandomCord(teamTwoSpawnBB.minX, teamTwoSpawnBB.maxX),Integer.valueOf(parsedBlockData[1]), generateRandomCord(teamTwoSpawnBB.minZ, teamTwoSpawnBB.maxZ), parsedBlockData[4], false);
                            }

                            String teamOneDataSplit = String.join("|", teamOneList);
                            String teamTwoDataSplit = String.join("|", teamTwoList);

                            MobBattleMod.SIMPLE_NETWORK_INSTANCE.sendTo(new EntityDataPacket(0,/* parsedBlockData[0]+"|"+parsedBlockData[1]+"|"+parsedBlockData[2],*/ teamOneDataSplit, teamTwoDataSplit), player);
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
    public static Entity spawnCreature(World worldIn, String entityNameID, double x, double y, double z, String team, boolean teamOne)
    {
        try{
            Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(entityNameID), worldIn);
            if (entity instanceof EntityLiving)
            {
                System.out.println("spawnCreature METHOD: "+team+" Spawning " + entity.getName() + " WITH ID: " + entity.getEntityId() + " at X: " + x + " Y: " + y + " Z:" + z);
                EntityLiving entityliving = (EntityLiving)entity;
                entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);
                entityliving.rotationYawHead = entityliving.rotationYaw;
                entityliving.renderYawOffset = entityliving.rotationYaw;
                entityliving.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entityliving)), (IEntityLivingData)null);
                worldIn.spawnEntity(entity);
                entityliving.playLivingSound();

                if(teamOne){
                    teamOneList.add(String.valueOf(entity.getEntityId()));
                } else {
                    teamTwoList.add(String.valueOf(entity.getEntityId()));
                }

                //Team.updateEntity(team, (EntityCreature) entity);
            }
            return entity;
        } catch (ConcurrentModificationException e){
            System.out.println("CONCURRENT-MODIFICATION-ERROR in SpawnEntityPacket.spawnCreature()");
            e.printStackTrace();
        }
        return null;
    }

    public static int generateRandomCord(double min, double max) {
        Random r = new Random();
        return (int) (r.nextInt((int) ((max - min) + 1)) + min);
    }
}