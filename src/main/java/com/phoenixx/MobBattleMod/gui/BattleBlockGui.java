package com.phoenixx.MobBattleMod.gui;

import com.phoenixx.MobBattleMod.blocks.tileEntities.BattleBlockTileEntity;
import com.phoenixx.MobBattleMod.gui.containers.BattleBlockContainer;
import com.phoenixx.MobBattleMod.util.Reference;
import com.phoenixx.MobBattleMod.util.handlers.FightHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class BattleBlockGui extends GuiContainer
{
    private static final ResourceLocation FURNACE_GUI_TEXTURES = new ResourceLocation(Reference.MOD_ID,"textures/gui/battle_block_gui.png");
    /** The player inventory bound to this GUI. */
    private final InventoryPlayer playerInventory;
    private final BattleBlockTileEntity tileBattleBlock;

    /** Chat entry fields */
    protected GuiTextField teamOneTextField;
    protected GuiTextField teamTwoTextField;

    private String teamOneName = "Team One";
    private String teamTwoName = "Team One";

    public ArrayList<String> teamOne = new ArrayList<>();
    public ArrayList<String> teamTwo = new ArrayList<>();

    public BattleBlockGui(InventoryPlayer player, BattleBlockTileEntity tileentity)
    {
        super(new BattleBlockContainer(player, tileentity));
        this.playerInventory = player;
        this.tileBattleBlock = tileentity;
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);

        ScaledResolution scaled = new ScaledResolution(mc);
        int screenWidth = scaled.getScaledWidth();
        int screenHeight = scaled.getScaledHeight();

        this.buttonList.add(new GuiButton(1, screenWidth / 2 - 30, screenHeight / 2 + 30,60,20, "Start Fight"));

        this.teamOneTextField = new GuiTextField(2, this.fontRenderer, screenWidth / 2 - 79, screenHeight / 2 + 5, 70, 12);
        this.teamOneTextField.setMaxStringLength(11);
        this.teamOneTextField.setText("Team One");
        this.teamOneTextField.setTextColor(Color.WHITE.getRGB());

        this.teamTwoTextField = new GuiTextField(3, this.fontRenderer, screenWidth / 2 + 9, screenHeight / 2 + 5, 70, 12);
        this.teamTwoTextField.setMaxStringLength(11);
        this.teamTwoTextField.setText("Team Two");
        this.teamTwoTextField.setTextColor(Color.WHITE.getRGB());
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            //TODO Start game code here

            IItemHandler inventory = tileBattleBlock.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);

            for(int x = 0; x < 24; x++){

                if(!inventory.getStackInSlot(x).isEmpty()){
                    ItemStack itemStack = inventory.getStackInSlot(x);
                    ItemMonsterPlacer itemMonsterPlacer = (ItemMonsterPlacer) itemStack.getItem();
                    //Entity entity = EntityList.createEntityByIDFromName(itemMonsterPlacer.getNamedIdFrom(itemStack), mc.world);
                    String entityString = itemMonsterPlacer.getNamedIdFrom(itemStack).toString();

                    if(x < 12){
                        teamOne.add(entityString);
                    } else {
                        teamTwo.add(entityString);
                    }
                }
            }

            FightHandler.setTeamOne(teamOne, teamOneName);
            FightHandler.setTeamTwo(teamTwo, teamTwoName);

            FightHandler.setBlockPos(tileBattleBlock.getPos());

            FightHandler.start();

            playerInventory.player.closeScreen();
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        this.teamOneTextField.drawTextBox();
        this.teamTwoTextField.drawTextBox();
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = "Mob Battle Menu";
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(FURNACE_GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        this.teamOneTextField.mouseClicked(mouseX, mouseY, mouseButton);
        this.teamTwoTextField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {

        if (keyCode == 15) {
            this.teamOneTextField.setFocused(!this.teamOneTextField.isFocused());
            this.teamTwoTextField.setFocused(!this.teamTwoTextField.isFocused());
        }

        if (keyCode == 1) {
            this.mc.displayGuiScreen((GuiScreen)null);
        }

        if (this.teamOneTextField.isFocused())
        {
            this.teamOneTextField.textboxKeyTyped(typedChar, keyCode);
            this.teamOneName = this.teamOneTextField.getText();
        }

        if (this.teamTwoTextField.isFocused())
        {
            this.teamTwoTextField.textboxKeyTyped(typedChar, keyCode);
            this.teamTwoName = this.teamTwoTextField.getText();
        }
    }
    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.teamOneTextField.updateCursorCounter();
        this.teamTwoTextField.updateCursorCounter();
    }
}