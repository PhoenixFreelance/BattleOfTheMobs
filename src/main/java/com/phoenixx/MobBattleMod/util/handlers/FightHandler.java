package com.phoenixx.MobBattleMod.util.handlers;

import com.phoenixx.MobBattleMod.MobBattleMod;
import com.phoenixx.MobBattleMod.util.SpawnEntityPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;


public class FightHandler
{
    public static ArrayList<String> teamOne = new ArrayList<>();
    public static ArrayList<String> teamTwo = new ArrayList<>();

    public static ArrayList<Entity> teamOneList = new ArrayList<>();
    public static ArrayList<Entity> teamTwoList = new ArrayList<>();

    public static HashMap<String, String> teamData = new HashMap<>();

    public static String teamOneName = "Team One";
    public static String teamTwoName = "Team Two";

    public static int teamOneAlive = 12;
    public static int teamTwoAlive = 12;

    public static String winner = "NONE";

    public static BlockPos blockPos;

    public static int waitTime = 5;
    public static int starterTick = waitTime * 20;
    public static int ticker = 0;

    public static boolean startTimer = false;
    public static boolean started = false;
    public static boolean gameOver = false;

    public static void tick()
    {
        if(startTimer){
            starterTick--;
            System.out.println("Seconds till start: " + starterTick / 20);
        }

        if (starterTick == 0 && !started) {
            startTimer = false;
            started = true;
            starterTick = waitTime * 20;

            String teamOneDataSplit = String.join("|", teamOne);
            String teamTwoDataSplit = String.join("|", teamTwo);

            MobBattleMod.SIMPLE_NETWORK_INSTANCE.sendToServer(new SpawnEntityPacket(0, getBlockPos().getX()+"|"+getBlockPos().getY()+"|"+getBlockPos().getZ()+"|"+teamOneName+"|"+teamTwoName, teamOneDataSplit, teamTwoDataSplit/*, teamOneName, teamTwoName*/));
            ticker++;
        }

        if(started){
            ticker++;

            if(teamOneAlive == 0){
                gameOver = true;
                started = false;
                winner = teamTwoName;
                starterTick--;

                System.out.println("Seconds till reset: " + starterTick / 20);

                if(starterTick == 0){
                    reset();
                }
            } else if(teamTwoAlive == 0){
                gameOver = true;
                started = false;
                winner = teamOneName;
                starterTick--;

                System.out.println("Seconds till reset: " + starterTick / 20);

                if(starterTick == 0){
                    reset();
                }
            }
        }
    }

    public static void start()
    {
        startTimer = true;

        for(String entityType: teamOne){
            if(!teamData.containsKey(entityType)){
                teamData.put(entityType, teamOneName);
            }
        }

        for(String entityType: teamTwo){
            if(!teamData.containsKey(entityType)){
                teamData.put(entityType, teamTwoName);
            }
        }
    }

    public static void reset()
    {
        startTimer = false;
        started = false;
        gameOver = false;

        starterTick = waitTime * 20;
        ticker = 0;
        teamOneName = "Team One";
        teamTwoName = "Team Two";

        teamOne.clear();
        teamTwo.clear();

        teamOneList.clear();
        teamTwoList.clear();

        teamData.clear();
    }

    public static void setTeamOne(ArrayList<String> givenTeamOne, String givenTeamName) {
        teamOne = givenTeamOne;
        teamOneName = givenTeamName;
    }

    public static void setTeamTwo(ArrayList<String> givenTeamTwo, String givenTeamName) {
        teamTwo = givenTeamTwo;
        teamTwoName = givenTeamName;
    }

    public static void setTeamOneList(ArrayList<Entity> givenList) {
        teamOneList = givenList;
    }

    public static void addToTeamOne(Entity entity) {
        teamOneList.add(entity);
    }

    public static void addToTeamTwo(Entity entity) {
        teamOneList.add(entity);
    }

    public static void setTeamTwoList(ArrayList<Entity> givenList) {
        FightHandler.teamTwoList = givenList;
    }

    public ArrayList<String> getTeamOne() {
        return teamOne;
    }

    public ArrayList<String> getTeamTwo() {
        return teamTwo;
    }

    public static void setBlockPos(BlockPos blockPos) {
        FightHandler.blockPos = blockPos;
    }

    public static BlockPos getBlockPos() {
        return blockPos;
    }

    public static String getEntityTeamFromEntityID(String uuid, boolean teamOne){
        if(teamOne){
            for (Entity entityCreature: teamOneList){
                if(entityCreature.getUniqueID().equals(uuid)){
                    return entityCreature.getTeam().getName();
                }
            }
        } else {
            for (Entity entityCreature: teamTwoList){
                if(entityCreature.getUniqueID().equals(uuid)){
                    return entityCreature.getTeam().getName();
                }
            }
        }
        return null;
    }

    public static void removeMobFromTeamList(EntityCreature entity)
    {
        System.out.println("REMOVE MOB METHOD | ENTITY: " + entity.getName() + " | TEAM: " + entity.getTeam().getName());
        System.out.println("TEAM ONE: " + teamOneName);
        System.out.println("TEAM TWO: " + teamTwoName);
        if(entity.getTeam().getName().equals(teamOneName)){
            System.out.println("Removing entity from team one");
            if(!teamOneList.isEmpty()) {
                if(teamOneList.contains(entity)){
                    System.out.println("Team ONE LIST contains entity: " + entity.getName() + " removing now!");
                    teamOneList.remove(entity);
                } else {
                    System.out.println("Team ONE LIST DOES NOT contain entity: " + entity.getName());
                }
            }
        } else if(entity.getTeam().getName().equals(teamTwoName)){
            System.out.println("Removing entity from team two");
            if(!teamTwoList.isEmpty()) {
                if(teamTwoList.contains(entity)){
                    System.out.println("Team TWO LIST contains entity: " + entity.getName() + " removing now!");
                    teamTwoList.remove(entity);
                } else {
                    System.out.println("Team TWO LIST DOES NOT contain entity: " + entity.getName());
                }
            }
        }
    }

    public static boolean isTeamOne(String name){
        if(name.equalsIgnoreCase(teamOneName)){
            return true;
        }
        return false;
    }
}
