package com.zefinder;

import java.util.List;
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

	private ConfigFile cf;

	public PlayerListener(ConfigFile cf) {
		this.cf = cf;
	}

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
		boolean ret = false;

		try {
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
			Object clickEvent = jsonObject.get("clickEvent");
			if (clickEvent != null) {
				Map<?, ?> eventMap = ((Map<?, ?>) jsonObject.get("clickEvent"));

				if (eventMap.containsKey("action") && eventMap.get("action").equals("run_command")) {
					ConfigFile.Authorization let = cf.getLetAuth();
					ConfigFile.Authorization many = cf.getManyAuth();

					if (let == ConfigFile.Authorization.FORBID) {
						ret = true;
						if (many == ConfigFile.Authorization.ALL) {
							ret = false;

						} else if (many == ConfigFile.Authorization.SOME) {
							Object value = eventMap.get("value");
							List<?> commands = cf.getCommands();
							for (Object command : commands) {
								if (command.equals(value)) {
									ret = false;
								}
							}
						}

					} else if (let == ConfigFile.Authorization.ALLOW) {
						ret = false;
						if (many == ConfigFile.Authorization.ALL) {
							ret = true;

						} else if (many == ConfigFile.Authorization.SOME) {
							Object value = eventMap.get("value");
							List<?> commands = cf.getCommands();
							for (Object command : commands) {
								if (command.equals(value)) {
									ret = true;
								}
							}
						}
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		return ret;
	}

}
