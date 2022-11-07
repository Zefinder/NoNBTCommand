package com.zefinder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class ConfigFile {

	private Map<String, Object> config;
	private final File configFile = new File("./plugins/NoNBTCommands/config.yml");

	private final DumperOptions options;

	enum Authorization {
		ALLOW("allow"), FORBID("forbid"), ALL("all"), SOME("some");

		private String auth;

		private Authorization(String auth) {
			this.auth = auth;
		}

		public String getAuth() {
			return this.auth;
		}
	}

	public ConfigFile() {
		// Only to make a nice-looking yaml file
		options = new DumperOptions();
		options.setIndentWithIndicator(true);
		options.setIndent(3);
		options.setIndicatorIndent(3);
		options.setPrettyFlow(true);
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

		try {
			init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void init() throws IOException {
		// Create folder and file if they don't exist
		File configFolder = new File("./plugins/NoNBTCommands");
		if (!configFolder.exists())
			configFolder.mkdirs();

		if (!configFile.exists()) {
			configFile.createNewFile();
		}
		// Read the YAML file
		Yaml yaml = new Yaml();

		InputStream is = new FileInputStream(configFile);
		config = yaml.load(is);

		if (verifyFile())
			write();

		is.close();

	}

	public ArrayList<?> getNormalCommands() {
		return (ArrayList<?>) config.get("commands");
	}
	
	public ArrayList<?> getOpCommands() {
		return (ArrayList<?>) config.get("op");
	}
	
	public ArrayList<?> getAllCommands() {
		List<Object> commands = new ArrayList<>(getNormalCommands());
		commands.addAll(getOpCommands());
		return new ArrayList<>(commands);
	}

	public void changeAuth(Authorization auth) throws FileNotFoundException {
		Map<?, ?> authMap = (Map<?, ?>) config.get("auth");
		Map<Object, Object> finalMap = new LinkedHashMap<>(authMap);

		switch (auth) {
		case ALLOW:
		case FORBID:
			finalMap.put("let", auth.getAuth());
			break;

		case ALL:
		case SOME:
			finalMap.put("many", auth.getAuth());
			break;
		}

		config.put("auth", finalMap);
		write();
	}

	public Authorization getLetAuth() {
		Map<?, ?> authMap = (Map<?, ?>) config.get("auth");
		Object let = authMap.get("let");
		if (let.equals(Authorization.ALLOW.getAuth()))
			return Authorization.ALLOW;
		else
			return Authorization.FORBID;
	}

	public Authorization getManyAuth() {
		Map<?, ?> authMap = (Map<?, ?>) config.get("auth");
		Object let = authMap.get("many");
		if (let.equals(Authorization.SOME.getAuth()))
			return Authorization.SOME;
		else
			return Authorization.ALL;
	}

	public void addCommand(String command, boolean op) throws FileNotFoundException {
		List<?> commands;
		if (op)
			commands = (ArrayList<?>) config.get("op");
		else
			commands = (ArrayList<?>) config.get("commands");

		List<Object> finalCommands = new ArrayList<>(commands);
		finalCommands.add(command);

		if (op)
			config.put("op", finalCommands);
		else
			config.put("commands", finalCommands);

		write();
	}

	public boolean removeCommand(String command, boolean op) throws FileNotFoundException {
		List<?> commands;
		if (op)
			commands = (ArrayList<?>) config.get("op");
		else
			commands = (ArrayList<?>) config.get("commands");
		
		List<Object> finalCommands = new ArrayList<>(commands);
		boolean success = finalCommands.remove(command);

		if (success) {
			if (op)
				config.put("op", finalCommands);
			else
				config.put("commands", finalCommands);
			
			write();
		}

		return success;
	}

	public void removeAll() throws FileNotFoundException {
		config.put("commands", new ArrayList<>());
		config.put("op", new ArrayList<>());
		write();
	}

	// return true if changed
	private boolean verifyFile() {
		boolean changed = false;

		if (config == null) {
			config = new LinkedHashMap<>();
			changed = true;
		}

		if (!config.containsKey("auth")) {
			Map<String, String> auth = new LinkedHashMap<>();
			auth.put("let", Authorization.FORBID.getAuth());
			auth.put("many", Authorization.ALL.getAuth());
			config.put("auth", auth);
			changed = true;
		}

		if (!config.containsKey("commands")) {
			config.put("commands", new ArrayList<>());
			changed = true;
		}

		if (!config.containsKey("op")) {
			config.put("op", new ArrayList<>());
			changed = true;
		}

		return changed;
	}

	private void write() throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(configFile);
		Yaml yaml = new Yaml(options);
		yaml.dump(config, writer);
		writer.close();
	}

}
