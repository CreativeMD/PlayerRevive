package com.creativemd.playerrevive.client;

import com.creativemd.playerrevive.Revival;
import com.creativemd.playerrevive.server.PlayerReviveServer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PlayerReviveClient extends PlayerReviveServer {
	
	public static Revival playerRevive = null;
	
	public static boolean customEffects = false;
	
	@Override
	public void initSide()
	{
		MinecraftForge.EVENT_BUS.register(new ReviveEventClient());
	}
	
}
