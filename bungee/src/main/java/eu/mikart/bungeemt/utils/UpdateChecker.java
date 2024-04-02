package eu.mikart.bungeemt.utils;

import eu.mikart.bungeemt.BungeeMT;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
	private final BungeeMT plugin;

	public UpdateChecker(BungeeMT plugin) {
		this.plugin = plugin;
	}

	public void getLatestVersion(Consumer<String> consumer) {
		plugin.getProxy().getScheduler().runAsync(plugin, () -> {
			try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=115841").openStream(); Scanner scanner = new Scanner(inputStream)) {
				if (scanner.hasNext()) {
					consumer.accept(scanner.next());
				}
			} catch (IOException exception) {
				plugin.getLogger().info("Unable to check for updates: " + exception.getMessage());
			}
		});
	}

}
