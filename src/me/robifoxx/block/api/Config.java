package me.robifoxx.block.api;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import me.robifoxx.block.Main;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginAwareness;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.logging.Level;

public class Config {
	private File f;
	private FileConfiguration cfg;

	private String path_;
	private String fileName_;

	public Config(String path, String fileName) {
		path_ = path;
		fileName_ = fileName;
	}

	public void create() {
		f = new File(path_, fileName_);
		cfg = YamlConfiguration.loadConfiguration(f);
	}

	public void setDefault(String filename) {
		InputStream defConfigStream = Main.getProvidingPlugin(Main.class).getResource(filename);
		if (defConfigStream == null)
			return;
		YamlConfiguration defConfig;

		if ((isStrictlyUTF8())) {
			defConfig = YamlConfiguration
					.loadConfiguration(new InputStreamReader(defConfigStream,
							Charsets.UTF_8));
		} else {
			defConfig = new YamlConfiguration();
			byte[] contents;
			try {
				contents = ByteStreams.toByteArray(defConfigStream);
			} catch (IOException e) {
				Main.getProvidingPlugin(Main.class).getLogger().log(Level.SEVERE,
						"Unexpected failure reading " + filename, e);
				return;
			}
			String text = new String(contents, Charset.defaultCharset());
			if (!(text.equals(new String(contents, Charsets.UTF_8)))) {
				Main.getProvidingPlugin(Main.class).getLogger()
						.warning(
								"Default system encoding may have misread " + filename + " from plugin jar");
			}
			try {
				defConfig.loadFromString(text);
			} catch (InvalidConfigurationException e) {
				Main.getProvidingPlugin(Main.class).getLogger().log(Level.SEVERE,
						"Cannot load configuration from jar", e);
			}
		}

		cfg.setDefaults(defConfig);
	}

	public InputStream getResource(String filename) {
		if (filename == null) {
			throw new IllegalArgumentException("Filename cannot be null");
		}
		try {
			ClassLoader classLoader = super.getClass().getClassLoader();
			URL url = classLoader.getResource(filename);
			if (url == null) {
				return null;
			}
			URLConnection connection = url.openConnection();
			connection.setUseCaches(false);
			return connection.getInputStream();
		} catch (IOException localIOException) {
		}
		return null;
	}

	private boolean isStrictlyUTF8() {
		return Main.getProvidingPlugin(Main.class).getDescription().getAwareness().contains(
				PluginAwareness.Flags.UTF8);
	}

	public void saveConfig() {
		try {
			cfg.save(f);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void reloadConfig() {
		cfg = YamlConfiguration.loadConfiguration(f);
		InputStream defConfigStream = this.getResource(fileName_);
		if(defConfigStream != null) {
			cfg.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
		}
	}

	public FileConfiguration getConfig() {
		return cfg;
	}

	public File toFile() {
		return f;
	}

	public boolean exists() {
		if(f.exists()) {
			return true;
		} else {
			return false;
		}
	}
}
