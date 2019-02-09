package com.phoenixx.MobBattleMod.util;

import com.google.common.base.Predicate;
import com.phoenixx.MobBattleMod.entities.EntityAITeamTarget;
import com.phoenixx.MobBattleMod.util.handlers.FightHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team.CollisionRule;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class Team {

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

        if(team.equals(FightHandler.teamOneName)){
            FightHandler.addToTeamOne(entity);
        } else if(team.equals(FightHandler.teamTwoName)){
            FightHandler.addToTeamTwo(entity);
        }
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
}