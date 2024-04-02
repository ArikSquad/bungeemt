package eu.mikart.bungeemt;

import co.aikar.commands.BungeeCommandManager;
import eu.mikart.bungeemt.commands.ActionbarCommand;
import eu.mikart.bungeemt.commands.BungeeMTCommand;
import eu.mikart.bungeemt.commands.TitleCommand;
import eu.mikart.bungeemt.config.Locales;
import eu.mikart.bungeemt.config.Settings;
import eu.mikart.bungeemt.user.BungeePlayer;
import eu.mikart.bungeemt.user.Player;
import eu.mikart.bungeemt.utils.UpdateChecker;
import eu.mikart.bungeemt.utils.Version;
import eu.mikart.bungeemt.utils.VeryCommonUtils;
import eu.mikart.bungeemt.utils.WaterfallLogger;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
public final class BungeeMT extends Plugin implements IBungeeMT {

	@Getter
	private static BungeeMT instance;
	public BungeeAudiences adventure;
	public BungeeCommandManager commandManager;
	public List<ScheduledTask> tasks = new ArrayList<>();

	@Setter
	private Settings settings;
	@Setter
	private Locales locales;

	@Override
	public void onEnable() {
		instance = this;
		adventure = BungeeAudiences.create(this);
		commandManager = new BungeeCommandManager(this);
		commandManager.enableUnstableAPI("help");
		commandManager.enableUnstableAPI("brigadier");
		this.loadConfig();

		commandManager.registerCommand(new TitleCommand(this));
		commandManager.registerCommand(new ActionbarCommand(this));
		commandManager.registerCommand(new BungeeMTCommand(this));
		registerAutotitles();

		updateCheck();
		getLogger().info("BungeeMT has been enabled!");
	}

	@Override
	public void onDisable() {
		instance = null;
		if (this.adventure != null) {
			this.adventure.close();
			this.adventure = null;
		}
		commandManager.unregisterCommands();
	}

	public void registerAutotitles() {
		if (getSettings().isAutotitlesEnabled()) {
			getSettings().getTitles().forEach(title -> {
				ScheduledTask task = getProxy().getScheduler().schedule(this, () -> {
					for (ProxiedPlayer p : getProxy().getPlayers()) {
						Audience audience = adventure.player(p);

						int fade = title.getFadeIn() != 0 ? title.getFadeOut() : 0;
						int stay = title.getStay() != 0 ? title.getStay() : 3;
						int fadeOut = title.getFadeOut() != 0 ? title.getFadeOut() : 0;
						List<String> sr = title.getDisabled_servers();

						if (p.getServer().getInfo().getName() != null && sr.contains(p.getServer().getInfo().getName())) {
							continue;
						}

						audience.showTitle(
								Title.title(
										parseLegacyAndMiniMessage(title.getTitle()),
										title.getSubtitle() != null ? parseLegacyAndMiniMessage(title.getSubtitle()) : Component.empty(),
										Title.Times.times(Duration.ofSeconds(fade), Duration.ofSeconds(stay), Duration.ofSeconds(fadeOut))
								)
						);
					}
				}, 1, VeryCommonUtils.parseTimeToSeconds(title.getInterval()), TimeUnit.SECONDS);
				tasks.add(task);
			});
		}
	}

	@Override
	@NotNull
	public Audience getConsole() {
		return adventure.console();
	}

	@Override
	public void reload() {
		tasks.forEach(ScheduledTask::cancel);
		tasks.clear();
		loadConfig();
		registerAutotitles();
	}

	@Override
	public InputStream getResource(@NotNull String path) {
		return getResourceAsStream(path);
	}

	@NotNull
	@Override
	public String getPlatform() {
		return ProxyServer.getInstance().getName();
	}

	@Override
	public Collection<Player> getOnlinePlayers() {
		ArrayList<Player> crossPlatform = new ArrayList<>();
		for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
			crossPlatform.add(BungeePlayer.adapt(player));
		}
		return crossPlatform;
	}

	@Override
	public Collection<Player> getOnlinePlayersOnServer(@NotNull Player player) {
		ArrayList<Player> crossPlatform = new ArrayList<>();
		BungeePlayer.toBungee(player).ifPresent(bungeePlayer -> {
			for (ProxiedPlayer playerOnServer : bungeePlayer.getServer().getInfo().getPlayers()) {
				crossPlatform.add(BungeePlayer.adapt(playerOnServer));
			}
		});
		return crossPlatform;
	}

	@Override
	@NotNull
	public Path getConfigDirectory() {
		return getDataFolder().toPath();
	}

	@Override
	@NotNull
	public BungeeMT getPlugin() {
		return this;
	}

	@Override
	public String getPluginVersion() {
		return super.getDescription().getVersion();
	}

	public void updateCheck() {
		if (!getSettings().isUpdateChecker()) {
			return;
		}

		new UpdateChecker(this).getLatestVersion(version -> {
			Version currentVersion = getVersion();
			Version latestVersion = new Version(version);

			if (getSettings().isDebug()) getLogger().info("Current version: " + currentVersion.get() + " | Latest version: " + latestVersion.get());

			if (currentVersion.compareTo(latestVersion) < 0) {
				getLogger().warning("New release is available (" + version + ")! Download it from here: https://www.spigotmc.org/resources/bungeeminititles-bungee-title-broadcast.115841/");
			} else {
				getLogger().info("Running on the latest BungeeMT version");
			}
		});
	}

	@Override
	public eu.mikart.bungeemt.utils.Logger getPluginLogger() {
		return new WaterfallLogger(super.getLogger());
	}
}
