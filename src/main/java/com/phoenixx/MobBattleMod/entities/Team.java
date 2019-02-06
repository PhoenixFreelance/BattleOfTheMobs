package com.phoenixx.MobBattleMod.entities;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team.CollisionRule;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class Team {

    public static Map<TextFormatting, double[]> teamColor = Maps.newHashMap();
    static
    {
        teamColor.put(TextFormatting.AQUA, new double[] {0.01,0.9,1});
        teamColor.put(TextFormatting.BLACK, new double[] {0.01,0,0});
        teamColor.put(TextFormatting.BLUE, new double[] {0.2,0.2,1});
        teamColor.put(TextFormatting.DARK_AQUA, new double[] {0.01,0.4,0.5});
        teamColor.put(TextFormatting.DARK_BLUE, new double[] {0.01,0,0.4});
        teamColor.put(TextFormatting.DARK_GRAY, new double[] {0.2,0.2,0.2});
        teamColor.put(TextFormatting.DARK_GREEN,  new double[] {0.01,0.5,0});
        teamColor.put(TextFormatting.DARK_PURPLE, new double[] {0.3,0,0.4});
        teamColor.put(TextFormatting.DARK_RED, new double[] {0.5,0,0});
        teamColor.put(TextFormatting.GOLD, new double[] {1,0.6,0});
        teamColor.put(TextFormatting.GRAY, new double[] {0.4,0.4,0.4});
        teamColor.put(TextFormatting.GREEN, new double[] {0.01,1,0});
        teamColor.put(TextFormatting.LIGHT_PURPLE, new double[] {0.6,0,0.7});
        teamColor.put(TextFormatting.RED, new double[] {1,0.2,0.2});
        teamColor.put(TextFormatting.WHITE, new double[] {1,1,1});
        teamColor.put(TextFormatting.YELLOW, new double[] {1,1,0});
    }

    public static String getTeam(Entity entity)
    {
        return entity.getTeam()!=null?entity.getTeam().getName():"none";
    }

    public static boolean isOnSameTeam(Entity entity, Entity entity2)
    {
        if(entity.getTeam()==null)
            return true;
        if(entity2.getTeam()==null)
            return true;
        return entity.isOnSameTeam(entity2);
    }

    public static void addEntityToTeam(Entity entity, String team)
    {
        Scoreboard score = entity.world.getScoreboard();
        if(score.getTeam(team)==null)
        {
            score.createTeam(team);
            score.getTeam(team).setCollisionRule(CollisionRule.HIDE_FOR_OTHER_TEAMS);
        }
        score.getTeam(team).getMembershipCollection().size();
        score.addPlayerToTeam(entity.getCachedUniqueIdString(), team);
    }

    public static int getTeamSize(Entity entity, String team)
    {
        return entity.world.getScoreboard().getTeam(team)!=null?entity.world.getScoreboard().getTeam(team).getMembershipCollection().size():0;
    }

    public static void updateEntity(String team, EntityCreature e)
    {
        addEntityToTeam(e, team);
        e.targetTasks.taskEntries.removeIf(new Predicate<EntityAITaskEntry>() {
            @Override
            public boolean apply(EntityAITaskEntry input) {
                return input.action instanceof EntityAITarget;
            }});
        e.setAttackTarget(null);
        e.setHealth(e.getMaxHealth());
        e.targetTasks.addTask(1, new EntityAITeamTarget(e, false, true));
    }

    public static AxisAlignedBB getBoundingBoxPositions(BlockPos pos, @Nullable BlockPos pos2)
    {
        if(pos2 ==null)
        {
            return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D).offset(pos);
        }
        else
        {
            int xDiff = pos.getX()-pos2.getX();
            int yDiff = pos.getY()-pos2.getY();
            int zDiff = pos.getZ()-pos2.getZ();
            int x, y, z, x2, y2, z2;
            x=y=z=x2=y2=z2=0;
            if(xDiff<=0) x = 1;
            else x2=1;
            if(yDiff<=0) y = 1;
            else y2=1;
            if(zDiff<=0) z=1;
            else z2=1;
            AxisAlignedBB bb = new AxisAlignedBB(0+x, 0+y, 0+z, xDiff+x2, yDiff+y2, zDiff+z2).offset(pos2);
            return bb;
        }
    }

    public static EntityLiving fromUUID(World world, String uuid)
    {
        if(uuid!=null)
            for(Entity entity : world.loadedEntityList)
                if(entity.getCachedUniqueIdString()==uuid && entity instanceof EntityLiving)
                    return (EntityLiving) entity;
        return null;
    }

    public static double getYOffset(World world, BlockPos pos)
    {
        AxisAlignedBB axisalignedbb = (new AxisAlignedBB(pos)).expand(0.0D, -1.0D, 0.0D);
        List<AxisAlignedBB> list = world.getCollisionBoxes((Entity)null, axisalignedbb);

        if (list.isEmpty())
        {
            return 0.0D;
        }
        else
        {
            double d0 = axisalignedbb.minY;

            for (AxisAlignedBB axisalignedbb1 : list)
            {
                d0 = Math.max(axisalignedbb1.maxY, d0);
            }

            return d0 - (double)pos.getY();
        }
    }
}