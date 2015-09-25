package de.rafessor.radio.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * This class was authored by Rafessor. https://twitter.com/TheRafessor
 */
public class AutoUpdater {

	private URL jarFile;
	private URL versionInfo;

	private RadioPlugin plugin;

	private String currentLocalVersion;
	private String currentOnlineVersion;

	public AutoUpdater(RadioPlugin plugin, String version) {
		this.plugin = plugin;
		this.currentLocalVersion = version;

		try {
			this.versionInfo = new URL("http://plugins.rafessor.de/plugins/radio/currentVersion.php");
			this.jarFile = new URL("http://plugins.rafessor.de/plugins/radio/radioplugin.jar");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public boolean updateCheck() {
		FileConfiguration config = plugin.getConfig();

		try {
			if (check()) {
				if (!config.contains("autoUpdate"))
					config.set("autoUpdate", true);
				if (!config.contains("shutdownAfterUpdate"))
					config.set("shutdownAfterUpdate", true);
				plugin.saveConfig();

				System.out.println("[Radio] Found a update!");
				if (config.getBoolean("autoUpdate")) {
					downloadFile(jarFile);
					if (config.getBoolean("shutdownAfterUpdate")) {
						System.out
								.println("[Radio] Server is now shutting down in cause of a new Radio update!");
						plugin.getServer().shutdown();
					} else
						System.out
								.println("[Radio] The next time you restart the server the Radio plugin will be updatet");
					return true;
				} else
					System.out
							.println("[Radio] This server is not running the newest version of the Radio plugin. I recommend you setting \"autoUpdate\" to \"true\" in the configuration file.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean check() throws IOException {
		this.currentOnlineVersion = new BufferedReader(new InputStreamReader(versionInfo.openStream())).readLine();
		if (currentLocalVersion.equals(currentOnlineVersion))
			return false;
		else
			return true;
	}

	private File downloadFile(URL url) throws Exception {
		URLConnection uc = url.openConnection();
		InputStream is = (InputStream) uc.getInputStream();
		File updateFolder = new File(plugin.getDataFolder().getParentFile(),
				"update");
		updateFolder.mkdirs();
		File jar = new File(updateFolder, "Radio.jar");
		Files.copy(is, jar.toPath());
		is.close();

		return jar;
	}
}
