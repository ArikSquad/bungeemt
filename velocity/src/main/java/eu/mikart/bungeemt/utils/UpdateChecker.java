package eu.mikart.bungeemt.utils;

import com.velocitypowered.api.proxy.ProxyServer;
import eu.mikart.bungeemt.IBungeeMT;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
	private final ProxyServer proxyServer;
	private final IBungeeMT plugin;

	public UpdateChecker(IBungeeMT plugin, ProxyServer proxyServer) {
		this.plugin = plugin;
		this.proxyServer = proxyServer;
	}

	public void getLatestVersion(Consumer<String> consumer) {
		proxyServer.getScheduler().buildTask(plugin, () -> {
			try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=115841").openStream(); Scanner scanner = new Scanner(inputStream)) {
				if (scanner.hasNext()) {
					consumer.accept(scanner.next());
				}
			} catch (IOException exception) {
				plugin.getPluginLogger().warn("Unable to check for updates: " + exception.getMessage());
			}
		}).schedule();
	}

}
