package com.phoenixx.MobBattleMod.objects;

import net.minecraft.entity.EntityCreature;

import java.util.ArrayList;

public class MobTeam
{
    private ArrayList<EntityCreature> teamMembers = new ArrayList<>();

    public MobTeam(){

    }

    public void addTeamMember(EntityCreature teamMember) {
        this.teamMembers.add(teamMember);
    }

    public ArrayList<EntityCreature> getTeamMembers() {
        return teamMembers;
    }
}
