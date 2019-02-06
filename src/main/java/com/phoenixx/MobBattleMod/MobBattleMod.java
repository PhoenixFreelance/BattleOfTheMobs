package com.phoenixx.MobBattleMod;

import com.phoenixx.MobBattleMod.blocks.tileEntities.BattleBlockTileEntity;
import com.phoenixx.MobBattleMod.init.ModRecipes;
import com.phoenixx.MobBattleMod.proxy.CommonProxy;
import com.phoenixx.MobBattleMod.util.Reference;
import com.phoenixx.MobBattleMod.util.SpawnEntityPacket;
import com.phoenixx.MobBattleMod.util.handlers.EventHandler;
import com.phoenixx.MobBattleMod.util.handlers.GuiHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class MobBattleMod {
	
	@Instance
	public static MobBattleMod instance;

    public static final SimpleNetworkWrapper SIMPLE_NETWORK_INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("phoenixx");

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
	public static CommonProxy proxy;

    private EventHandler eventHandler;
	
	@Mod.EventHandler
	public void PreInit(FMLPreInitializationEvent event)
	{
        eventHandler = new EventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);

        SIMPLE_NETWORK_INSTANCE.registerMessage(SpawnEntityPacket.Handler.class, SpawnEntityPacket.class, 0, Side.SERVER);
    }
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		ModRecipes.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        GameRegistry.registerTileEntity(BattleBlockTileEntity.class, new ResourceLocation(Reference.MOD_ID + ":battle_block"));
	}
	
	@Mod.EventHandler
	public void Postinit(FMLPostInitializationEvent event)
	{

	}

}
