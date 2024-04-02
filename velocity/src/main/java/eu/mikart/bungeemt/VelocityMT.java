package eu.mikart.bungeemt;

import co.aikar.commands.VelocityCommandManager;
import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.scheduler.ScheduledTask;
import eu.mikart.bungeemt.commands.ActionbarCommand;
import eu.mikart.bungeemt.commands.BungeeMTCommand;
import eu.mikart.bungeemt.commands.TitleCommand;
import eu.mikart.bungeemt.config.Locales;
import eu.mikart.bungeemt.config.Settings;
import eu.mikart.bungeemt.user.VelocityPlayer;
import eu.mikart.bungeemt.utils.UpdateChecker;
import eu.mikart.bungeemt.utils.VelocityLogger;
import eu.mikart.bungeemt.utils.Version;
import eu.mikart.bungeemt.utils.VeryCommonUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.InputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Plugin(
		id = "velocitymt",
		name = "VelocityMT",
		version = "1.0.3",
		authors = {"ArikSquad"},
		description = "VelocityMT (BungeeMT) is a plugin that allows you to broadcast custom titles."
)
@Getter
public class VelocityMT implements IBungeeMT {

	private final ProxyServer proxy;
	private final Logger logger;
	private final Path dataDirectory;
	public VelocityCommandManager commandManager;
	public List<ScheduledTask> tasks = new ArrayList<>();

	@Getter
	private static VelocityMT instance;
	@Setter
	private Settings settings;
	@Setter
	private Locales locales;


	@Inject
	public VelocityMT(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
		instance = this;

		this.proxy = proxy;
		this.logger = logger;
		this.dataDirectory = dataDirectory;
	}

	@Subscribe
	public void onProxyInitialization(ProxyInitializeEvent event) {
		commandManager = new VelocityCommandManager(getProxy(), this);
		commandManager.enableUnstableAPI("help");
		commandManager.enableUnstableAPI("brigadier");
		this.loadConfig();

		commandManager.registerCommand(new TitleCommand(this));
		commandManager.registerCommand(new ActionbarCommand(this));
		commandManager.registerCommand(new BungeeMTCommand(this));
		registerAutotitles();

		updateCheck();
		getLogger().info("BungeeMT (Velocity) has been enabled!");
	}

	public void updateCheck() {
		if (!getSettings().isUpdateChecker()) {
			return;
		}

		new UpdateChecker(this, getProxy()).getLatestVersion(version -> {
			Version currentVersion = getVersion();
			Version latestVersion = new Version(version);

			if (getSettings().isDebug())
				getLogger().info("Current version: " + currentVersion.get() + " | Latest version: " + latestVersion.get());

			if (currentVersion.compareTo(latestVersion) < 0) {
				getLogger().warn("New release is available (" + version + ")! Download it from here: https://www.spigotmc.org/resources/bungeeminititles-bungee-title-broadcast.115841/");
			} else {
				getLogger().info("Running on the latest BungeeMT version");
			}
		});
	}

	@Subscribe
	public void onProxyShutdown(ProxyShutdownEvent event) {
		instance = null;
	}

	@Override
	public eu.mikart.bungeemt.utils.Logger getPluginLogger() {
		return new VelocityLogger(logger);
	}

	@NotNull
	@Override
	public Path getConfigDirectory() {
		return dataDirectory;
	}

	@Override
	@NotNull
	public Audience getConsole() {
		return getProxy().getConsoleCommandSource();
	}

	@NotNull
	@Override
	public String getPlatform() {
		return "Velocity";
	}

	public void registerAutotitles() {
		if (getSettings().isAutotitlesEnabled()) {
			getSettings().getTitles().forEach(title -> {
				ScheduledTask task = getProxy().getScheduler().buildTask(this, () -> {
							for (Player p : proxy.getAllPlayers()) {
								int fade = title.getFadeIn() != 0 ? title.getFadeOut() : 0;
								int stay = title.getStay() != 0 ? title.getStay() : 3;
								int fadeOut = title.getFadeOut() != 0 ? title.getFadeOut() : 0;
								List<String> sr = title.getDisabled_servers();

								Optional<ServerConnection> optionalServer = p.getCurrentServer();
								if (optionalServer.isPresent()) {
									ServerConnection serverConnection = optionalServer.get();
									String serverName = serverConnection.getServer().getServerInfo().getName();
									if (serverName != null && sr.contains(serverName)) {
										continue;
									}
								}

								p.showTitle(
										Title.title(
												parseLegacyAndMiniMessage(title.getTitle()),
												title.getSubtitle() != null ? parseLegacyAndMiniMessage(title.getSubtitle()) : Component.empty(),
												Title.Times.times(Duration.ofSeconds(fade), Duration.ofSeconds(stay), Duration.ofSeconds(fadeOut))
										)
								);
							}
						})
						.delay(VeryCommonUtils.parseTimeToSeconds(title.getInterval()), TimeUnit.SECONDS)
						.repeat(VeryCommonUtils.parseTimeToSeconds(title.getInterval()), TimeUnit.SECONDS)
						.schedule();
				tasks.add(task);
			});
		}
	}

	@Override
	public void reload() {
		tasks.forEach(ScheduledTask::cancel);
		tasks.clear();
		loadConfig();
		registerAutotitles();
	}

	@Override
	public Collection<eu.mikart.bungeemt.user.Player> getOnlinePlayers() {
		final ArrayList<eu.mikart.bungeemt.user.Player> velocityPlayers = new ArrayList<>();
		for (com.velocitypowered.api.proxy.Player player : getProxy().getAllPlayers()) {
			velocityPlayers.add(VelocityPlayer.adapt(player));
		}
		return velocityPlayers;
	}

	@Override
	public Collection<eu.mikart.bungeemt.user.Player> getOnlinePlayersOnServer(@NotNull eu.mikart.bungeemt.user.Player serverPlayer) {
		final ArrayList<eu.mikart.bungeemt.user.Player> velocityPlayers = new ArrayList<>();
		VelocityPlayer.toVelocity(serverPlayer).flatMap(com.velocitypowered.api.proxy.Player::getCurrentServer).ifPresent(serverConnection -> {
			for (com.velocitypowered.api.proxy.Player connectedPlayer : serverConnection.getServer().getPlayersConnected()) {
				velocityPlayers.add(VelocityPlayer.adapt(connectedPlayer));
			}
		});
		return velocityPlayers;
	}


	@Nullable
	@Override
	public InputStream getResource(@NotNull String path) {
		return IBungeeMT.class.getClassLoader().getResourceAsStream(path);
	}

	@Override
	public @NotNull VelocityMT getPlugin() {
		return this;
	}

	@Override
	public String getPluginVersion() {
		return "1.0.3";
	}
}
