package com.phoenixx.MobBattleMod.util.handlers;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.phoenixx.MobBattleMod.entities.Team;
import com.phoenixx.MobBattleMod.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class EventHandler
{
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        switch(event.phase)
        {
            case START :
            {
                break;
            }
            case END :
            {
                FightHandler.tick();
                break;
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {

    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event)
    {
        if(FightHandler.started){
            if(event.getEntity() instanceof EntityCreature)
            {
                EntityCreature entityCreature = (EntityCreature) event.getEntity();
                if(entityCreature.getTeam()!=null) {
                    System.out.println(entityCreature.getName() + " died on team: " + entityCreature.getTeam().getName());
                    System.out.println("Team One List: [" + FightHandler.teamOneList.size() + "] " + FightHandler.teamOneList);
                    System.out.println("Team Two List: [" + FightHandler.teamTwoList.size() + "] " + FightHandler.teamTwoList);

                    FightHandler.removeMobFromTeamList(entityCreature);
                }
            }
        }
    }

    /*@SubscribeEvent
    public void addTeamTarget(EntityJoinWorldEvent event)
    {
        if(event.getEntity() instanceof EntityCreature)
        {
            if(event.getEntity().getTeam()!=null)
                Team.updateEntity(event.getEntity().getTeam().getName(), (EntityCreature) event.getEntity());
        }
    }*/

    @SubscribeEvent
    public void teamFriendlyFire(LivingAttackEvent event)
    {
        if(event.getEntity() instanceof EntityLivingBase && event.getEntity().getTeam()!=null)
        {
            EntityLivingBase ent = (EntityLivingBase) event.getEntity();
            if(event.getSource().getTrueSource() instanceof EntityLivingBase)
            {
                EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
                if(Team.isOnSameTeam(ent, attacker) && !ent.getTeam().getAllowFriendlyFire())
                    event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onGuiIngame(RenderGameOverlayEvent event)
    {
        int width = event.getResolution().getScaledWidth();
        int height = event.getResolution().getScaledHeight();

        Minecraft mc = ClientProxy.minecraft;
        FontRenderer fontrenderer = mc.fontRenderer;

        if(event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            if (mc.currentScreen == null || mc.currentScreen instanceof GuiChat) {
                ChatFormatting bold = ChatFormatting.BOLD;

                int startTimer = FightHandler.starterTick / 20;
                int seconds = FightHandler.ticker / 20;
                int secondsLeft = seconds % 60;
                int minutesLeft = seconds / 60;

                String displayStartUpTime = (startTimer < 10 ? ChatFormatting.RED.toString() + bold + minutesLeft + ":" + (startTimer < 10 ? "0" + startTimer : startTimer) : (bold.toString() + minutesLeft + ":" + (startTimer < 10 ? "0" + startTimer : startTimer))) ;
                String displayFightTimer = minutesLeft + ":" + (secondsLeft < 10 ? "0" + secondsLeft : secondsLeft);

                String matchStarting = "Match starts in ";
                String gameOver = "Match over!";

               if(FightHandler.startTimer){
                   if (startTimer > 0) {
                       GL11.glPushMatrix();
                       GL11.glScalef(2f, 2f, 2f);
                       mc.fontRenderer.drawStringWithShadow(matchStarting, ((width / 2) - mc.fontRenderer.getStringWidth(matchStarting)) / 2, (height / 6) / 2, Color.WHITE.getRGB());
                       mc.fontRenderer.drawStringWithShadow( ChatFormatting.YELLOW + displayStartUpTime + ChatFormatting.RESET + "'s", ((width / 2) - mc.fontRenderer.getStringWidth(ChatFormatting.YELLOW + displayStartUpTime + ChatFormatting.RESET + "s")) / 2, (height / 4) / 2, Color.WHITE.getRGB());
                       GL11.glPopMatrix();
                   }
               } else if (FightHandler.started){
                    if (startTimer == 0 && seconds >= 0){
                       Gui.drawRect((width / 2) - mc.fontRenderer.getStringWidth(displayFightTimer), 2, (width / 2) + mc.fontRenderer.getStringWidth(displayFightTimer), 15, Integer.MIN_VALUE);
                       fontrenderer.drawString(displayFightTimer, (width / 2) - (fontrenderer.getStringWidth(displayFightTimer) / 2), 5, Color.YELLOW.getRGB());
                   }
               }

               if(FightHandler.gameOver){
                   GL11.glPushMatrix();
                   GL11.glScalef(2f, 2f, 2f);
                   mc.fontRenderer.drawStringWithShadow(gameOver, ((width / 2) - mc.fontRenderer.getStringWidth(gameOver)) / 2, (height / 6) / 2, Color.WHITE.getRGB());
                   mc.fontRenderer.drawStringWithShadow( "Winner: " + ChatFormatting.GREEN + FightHandler.winner, ((width / 2) - mc.fontRenderer.getStringWidth("Winner: " + ChatFormatting.GREEN + FightHandler.winner)) / 2, (height / 4) / 2, Color.WHITE.getRGB());
                   GL11.glPopMatrix();
               }
            }
        }
    }
}
