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
		// We test only if the player is not OP
		boolean op = event.getPlayer().isOp();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();

			if (block.getState() instanceof Sign) {
				NBTTileEntity nbt = new NBTTileEntity(block.getState());
				if (checkForRunCommand(nbt.getString("Text1"), op)) {
					event.setCancelled(true);
				} else if (checkForRunCommand(nbt.getString("Text2"), op)) {
					event.setCancelled(true);
				} else if (checkForRunCommand(nbt.getString("Text3"), op)) {
					event.setCancelled(true);
				} else if (checkForRunCommand(nbt.getString("Text4"), op)) {
					event.setCancelled(true);
				}

			}
		}
	}

	private boolean checkForRunCommand(String json, boolean op) {
		boolean ret = false;

		try {
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
			Object clickEvent = jsonObject.get("clickEvent");
			if (clickEvent != null) {
				Map<?, ?> eventMap = ((Map<?, ?>) jsonObject.get("clickEvent"));

				// If the JSON contains an action that is running a command, the process begins
				if (eventMap.containsKey("action") && eventMap.get("action").equals("run_command")) {
					ConfigFile.Authorization let = cf.getLetAuth();
					ConfigFile.Authorization many = cf.getManyAuth();

					if (let == ConfigFile.Authorization.FORBID) {
						ret = false;
						if (many == ConfigFile.Authorization.ALL) {
							ret = true;

						} else if (many == ConfigFile.Authorization.SOME) {
							// We cancel iff the command is in the blacklist
							Object value = eventMap.get("value");
							
							List<?> commands;
							if (op)
								commands = cf.getOpCommands();
							else
								commands = cf.getAllCommands();

							for (Object command : commands) {
								if (value.toString().contains(command.toString())) {
									ret = true;
								}
							}
						}

					} else if (let == ConfigFile.Authorization.ALLOW) {
						ret = true;
						if (many == ConfigFile.Authorization.ALL) {
							ret = false;

						} else if (many == ConfigFile.Authorization.SOME) {
							// We allow iff the command is in the whitelist
							Object value = eventMap.get("value");
							
							List<?> commands;
							if (op)
								commands = cf.getOpCommands();
							else
								commands = cf.getAllCommands();

							for (Object command : commands) {
								if (value.toString().contains(command.toString())) {
									ret = false;
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
