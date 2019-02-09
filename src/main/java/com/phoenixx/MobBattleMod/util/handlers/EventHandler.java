package com.phoenixx.MobBattleMod.util.handlers;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.phoenixx.MobBattleMod.init.ModBlocks;
import com.phoenixx.MobBattleMod.proxy.ClientProxy;
import com.phoenixx.MobBattleMod.util.EnumFight;
import com.phoenixx.MobBattleMod.util.Team;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.BlockEvent;
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
                if(FightHandler.started){
                    FightHandler.tick();
                }
                break;
            }
        }
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event)
    {
        Entity entity = event.getEntity();
        World world = entity.world;

        if(world.isRemote){
            if(entity.getTeam() != null && FightHandler.started){
                if(entity.getTeam().getName().equalsIgnoreCase(FightHandler.teamOneName)){
                    FightHandler.removeFromTeamOne();
                    //System.out.println("[FIGHT HAS STARTED] " + entity.getName() + " HAS JUST DIED ON TEAM ONE: " + FightHandler.teamOneAlive + "/12");

                } else if(entity.getTeam().getName().equalsIgnoreCase(FightHandler.teamTwoName)){
                    FightHandler.removeFromTeamTwo();
                    //System.out.println("[FIGHT HAS STARTED] " + entity.getName() + " HAS JUST DIED ON TEAM TWO: " + FightHandler.teamTwoAlive + "/12");
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event)
    {
        Block block = event.getState().getBlock();
        if(block == ModBlocks.BATTLE_BLOCK){
            if(FightHandler.started){
                if(event.getPos().equals(FightHandler.blockPos)){
                    event.getPlayer().sendMessage(new TextComponentString(ChatFormatting.GREEN + "[Mob Battle]" + ChatFormatting.RESET + " Current mob battle has been stopped!"));
                    FightHandler.matchStatus = EnumFight.END;
                }
            }
        }
    }

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
        FontRenderer fontRenderer = mc.fontRenderer;

        if(event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            if (mc.currentScreen == null || mc.currentScreen instanceof GuiChat) {
                ChatFormatting bold = ChatFormatting.BOLD;

                int startTimer = FightHandler.timer / 20;
                int seconds = FightHandler.ticker / 20;
                int secondsLeft = seconds % 60;
                int minutesLeft = seconds / 60;

                String teamOneName = FightHandler.teamOneName;
                String teamTwoName = FightHandler.teamTwoName;

                String teamOneScore = "" + FightHandler.teamOneAlive + "/" + FightHandler.teamOneMax;
                String teamTwoScore = "" + FightHandler.teamTwoAlive + "/" + FightHandler.teamTwoMax;

                String displayStartUpTime = (startTimer < 10 ? ChatFormatting.RED.toString() + bold + minutesLeft + ":" + (startTimer < 10 ? "0" + startTimer : startTimer) : (bold.toString() + minutesLeft + ":" + (startTimer < 10 ? "0" + startTimer : startTimer)));
                String displayFightTimer = minutesLeft + ":" + (secondsLeft < 10 ? "0" + secondsLeft : secondsLeft);

                String matchStarting = "Match starts in ";
                String gameOver = "Match over!";

                if (startTimer > 0 && FightHandler.started && FightHandler.matchStatus.equals(EnumFight.WAITING_START)) {
                    GL11.glPushMatrix();
                    GL11.glScalef(2f, 2f, 2f);
                    mc.fontRenderer.drawStringWithShadow(matchStarting, ((width / 2) - mc.fontRenderer.getStringWidth(matchStarting)) / 2, (height / 6) / 2, Color.WHITE.getRGB());
                    mc.fontRenderer.drawStringWithShadow(ChatFormatting.YELLOW + displayStartUpTime + ChatFormatting.RESET + "'s", ((width / 2) - mc.fontRenderer.getStringWidth(ChatFormatting.YELLOW + displayStartUpTime + ChatFormatting.RESET + "s")) / 2, (height / 4) / 2, Color.WHITE.getRGB());
                    GL11.glPopMatrix();
                } else if (FightHandler.matchStatus.equals(EnumFight.STARTED)) {
                    Gui.drawRect((width / 2) - mc.fontRenderer.getStringWidth(displayFightTimer), 2, (width / 2) + mc.fontRenderer.getStringWidth(displayFightTimer), 15, Integer.MIN_VALUE);
                    drawString(fontRenderer, displayFightTimer, (width / 2) - (fontRenderer.getStringWidth(displayFightTimer) / 2), 5, 1, Color.YELLOW.getRGB());

                    drawString(fontRenderer, ChatFormatting.GREEN + teamOneName, 30, 10, 1.5F, Color.WHITE.getRGB());
                    drawString(fontRenderer, teamOneScore, 31 + ((fontRenderer.getStringWidth(ChatFormatting.GREEN + teamOneName) / 2) - (fontRenderer.getStringWidth(teamOneScore) / 2)), 25, 1.5F, Color.WHITE.getRGB());

                    drawString(fontRenderer, ChatFormatting.GREEN + teamTwoName, width - 103, 10, 1.5F, Color.WHITE.getRGB());
                    drawString(fontRenderer, teamTwoScore, (width - 103) + ((fontRenderer.getStringWidth(ChatFormatting.GREEN + teamTwoName) / 2) - (fontRenderer.getStringWidth(teamTwoScore) / 2)), 25, 1.5F, Color.WHITE.getRGB());

                } else if (FightHandler.matchStatus.equals(EnumFight.END) && startTimer > 0) {
                    GL11.glPushMatrix();
                    GL11.glScalef(2f, 2f, 2f);
                    mc.fontRenderer.drawStringWithShadow(gameOver, ((width / 2) - mc.fontRenderer.getStringWidth(gameOver)) / 2, (height / 6) / 2, Color.WHITE.getRGB());
                    mc.fontRenderer.drawStringWithShadow("Winner: " + ChatFormatting.GREEN + FightHandler.winner, ((width / 2) - mc.fontRenderer.getStringWidth("Winner: " + ChatFormatting.GREEN + FightHandler.winner)) / 2, (height / 4) / 2, Color.WHITE.getRGB());
                    GL11.glPopMatrix();
                }
            }
        }
    }

    public void drawString(FontRenderer fontRendererIn, String text, int x, int y, float size, int color) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0.0D);
        GL11.glScaled(size, size, size);
        fontRendererIn.drawStringWithShadow(text,  0, 0, color);
        GL11.glPopMatrix();
    }
}
