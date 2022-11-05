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
	private final File configFile = new File("./NoNBTCommands/config.yml");

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
		options = new DumperOptions();
		options.setIndentWithIndicator(true);
		options.setIndent(3);
		options.setIndicatorIndent(3);
		options.setPrettyFlow(true);
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

		init();
	}

	private void init() {
		// Create folder and file if they don't exist
		File configFolder = new File("./NoNBTCommands");
		if (!configFolder.exists())
			configFolder.mkdir();

		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			config = new LinkedHashMap<>();
			Map<String, String> auth = new LinkedHashMap<>();
			auth.put("let", Authorization.FORBID.getAuth());
			auth.put("many", Authorization.ALL.getAuth());
			config.put("auth", auth);

			List<String> commands = new ArrayList<>();
			config.put("commands", commands);

			try {
				write();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			// Read the YAML file
			Yaml yaml = new Yaml();
			try {
				InputStream is = new FileInputStream(configFile);
				config = yaml.load(is);
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public ArrayList<?> getCommands() {
		return (ArrayList<?>) config.get("commands");
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

	public void addCommand(String command) throws FileNotFoundException {
		List<?> commands = (ArrayList<?>) config.get("commands");
		List<Object> finalCommands = new ArrayList<>(commands);
		finalCommands.add(command);
		config.put("commands", finalCommands);

		write();
	}

	public boolean removeCommand(String command) throws FileNotFoundException {
		List<?> commands = (ArrayList<?>) config.get("commands");
		List<Object> finalCommands = new ArrayList<>(commands);
		boolean success = finalCommands.remove(command);

		if (success) {
			config.put("commands", finalCommands);
			write();
		}

		return success;
	}

	public void removeAll() throws FileNotFoundException {
		config.put("commands", new ArrayList<>());
		write();
	}

	private void write() throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(configFile);
		Yaml yaml = new Yaml(options);
		yaml.dump(config, writer);
		writer.close();
	}

}
