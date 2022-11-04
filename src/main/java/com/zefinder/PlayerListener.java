package com.zefinder;

import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.tr7zw.nbtapi.NBTTileEntity;

public class PlayerListener implements Listener {
//
//	// TODO: à retirer !
//	@EventHandler
//	public void onPlayerJoined(PlayerJoinEvent event) {
//		event.getPlayer().sendMessage(
//				Component.text(String.format("Hello %s, welcome to the server!", event.getPlayer().getName())));
//	}
//
//	// TODO à retirer
//	@EventHandler(ignoreCancelled = true)
//	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
//		String command = event.getMessage();
//		Player player = event.getPlayer();
//		event.getHandlers();
//		player.sendMessage(
//				Component.text(String.format("Thanks %s for sending the command %s", player.getName(), command)));
//		event.setCancelled(true);
//	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteraction(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();

			if (block.getState() instanceof Sign) {
				NBTTileEntity nbt = new NBTTileEntity(block.getState());
				if (checkForRunCommand(nbt.getString("Text1"))) {
					event.setCancelled(true);
				} else if (checkForRunCommand(nbt.getString("Text2"))) {
					event.setCancelled(true);
				} else if (checkForRunCommand(nbt.getString("Text3"))) {
					event.setCancelled(true);
				} else if (checkForRunCommand(nbt.getString("Text4"))) {
					event.setCancelled(true);
				}

			}
		}
	}

	private boolean checkForRunCommand(String json) {
		try {
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
			Object clickEvent = jsonObject.get("clickEvent");
			if (clickEvent != null) {
				// clickEvent contain objects if not empty
				// Objects are represented by a HashMap
				Map<?, ?> eventMap = ((Map<?, ?>) jsonObject.get("clickEvent"));

				if (eventMap.containsKey("action")) {
					// Can add test if equals to run_coomand then check list commands
					return eventMap.get("action").equals("run_command");
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
//
//	public static void main(String[] args) {
//		String text = "{\"clickEvent\":{\"action\":\"run_command\",\"value\":\"gamemode creative\"},\"text\":\"Minecraft Tools\"}";
//		String text2 = "{\"text\":\"\"}";
//
//		PlayerListener pl = new PlayerListener();
//		boolean test = pl.checkForRunCommand(text);
//		System.out.println(test);
//	}

}
