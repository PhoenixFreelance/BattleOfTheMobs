package com.phoenixx.MobBattleMod.util.handlers;

import com.phoenixx.MobBattleMod.MobBattleMod;
import com.phoenixx.MobBattleMod.packets.KillEntitiesPacket;
import com.phoenixx.MobBattleMod.packets.SpawnEntityPacket;
import com.phoenixx.MobBattleMod.util.EnumFight;
import net.minecraft.entity.Entity;
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
    public static int timer = 5 * 20;
    public static int ticker = 0;

    public static boolean started = false;

    public static EnumFight matchStatus = EnumFight.WAITING_START;

    public static void tick()
    {
        /*if (timer % 20L == 0L && timer > 0)
        {
            System.out.println("Seconds till start: " + timer / 20);
        }
*/
        if (timer > 0) {
            timer -= 1;
        } else if (timer <= 0) {
            switch (matchStatus) {
                case WAITING_START:
                    timer = 0;
                    matchStatus = EnumFight.STARTING;
                    break;

                case STARTING:
                    String teamOneDataSplit = String.join("|", teamOne);
                    String teamTwoDataSplit = String.join("|", teamTwo);
                    MobBattleMod.SIMPLE_NETWORK_INSTANCE.sendToServer(new SpawnEntityPacket(0, blockPos.getX() + "|" + blockPos.getY() + "|" + blockPos.getZ() + "|" + teamOneName + "|" + teamTwoName, teamOneDataSplit, teamTwoDataSplit));
                    matchStatus = EnumFight.STARTED;
                    break;

                case STARTED:
                    ticker++;
                    if(teamOneAlive == 0){
                        winner = teamTwoName;
                        matchStatus = EnumFight.WAITING_END;
                    } else if(teamTwoAlive == 0){
                        winner = teamOneName;
                        matchStatus = EnumFight.WAITING_END;
                    }
                    break;

                case WAITING_END:
                    matchStatus = EnumFight.END;
                    timer = 10 * 20;
                    break;

                case END:
                    MobBattleMod.SIMPLE_NETWORK_INSTANCE.sendToServer(new KillEntitiesPacket(0, blockPos.getX() + "|" + blockPos.getY() + "|" + blockPos.getZ()));
                    reset();
                    break;
            }
        }
    }

    public static void start()
    {
        started = true;

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
        teamOne.clear();
        teamTwo.clear();

        teamOneList.clear();
        teamTwoList.clear();

        teamData.clear();

        teamOneName = "Team One";
        teamTwoName = "Team Two";

        teamOneAlive = 12;
        teamTwoAlive = 12;

        winner = "NONE";

        blockPos = null;
        timer = 5 * 20;
        ticker = 0;

        started = false;

        matchStatus = EnumFight.WAITING_START;
    }

    public static void setTeamOne(ArrayList<String> givenTeamOne, String givenTeamName) {
        teamOne = givenTeamOne;
        teamOneName = givenTeamName;
    }

    public static void setTeamTwo(ArrayList<String> givenTeamTwo, String givenTeamName) {
        teamTwo = givenTeamTwo;
        teamTwoName = givenTeamName;
    }

    public static void addToTeamOne(Entity entity) {
        teamOneList.add(entity);
    }

    public static void addToTeamTwo(Entity entity) {
        teamOneList.add(entity);
    }

    public static void removeFromTeamOne(){
        teamOneAlive-=1;
    }

    public static void removeFromTeamTwo(){
        teamTwoAlive-=1;
    }

    public static void setBlockPos(BlockPos blockPos) {
        FightHandler.blockPos = blockPos;
    }
}
