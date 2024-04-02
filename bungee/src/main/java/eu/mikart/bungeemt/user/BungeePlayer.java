package eu.mikart.bungeemt.user;

import eu.mikart.bungeemt.BungeeMT;
import net.kyori.adventure.audience.Audience;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * The user system has been inspired by <a href="https://github.com/William278/HuskTowns">HuskTowns</a>
 * which is licensed under the Apache License 2.0.
 */
public class BungeePlayer implements Player {

	private final BungeeMT plugin = BungeeMT.getInstance();
	private final ProxiedPlayer player;

	private BungeePlayer(@NotNull ProxiedPlayer player) {
		this.player = player;
	}

	@Override
	@NotNull
	public String getName() {
		return player.getName();
	}

	@NotNull
	@Override
	public UUID getUniqueId() {
		return player.getUniqueId();
	}

	@Override
	public int getPing() {
		return player.getPing();
	}

	@Override
	@NotNull
	public String getServerName() {
		return player.getServer().getInfo().getName();
	}

	@Override
	public int getPlayersOnServer() {
		return player.getServer().getInfo().getPlayers().size();
	}

	@Override
	public boolean hasPermission(String s) {
		return player.hasPermission(s);
	}

	@NotNull
	@Override
	public Audience getAudience() {
		return plugin.getAdventure().player(player);
	}

	/**
	 * Adapts a cross-platform {@link Player} to a bungee {@link CommandSender} object
	 *
	 * @param player {@link Player} to adapt
	 * @return The {@link ProxiedPlayer} object, {@code null} if they are offline
	 */
	public static Optional<ProxiedPlayer> toBungee(@NotNull Player player) {
		ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player.getUniqueId());
		if (proxiedPlayer != null) {
			return Optional.of(proxiedPlayer);
		} else {
			return Optional.empty();
		}
	}

	/**
	 * Adapts a bungee {@link ProxiedPlayer} to a cross-platform {@link Player} object
	 *
	 * @param player {@link ProxiedPlayer} to adapt
	 * @return The {@link Player} object
	 */
	public static BungeePlayer adapt(@NotNull ProxiedPlayer player) {
		return new BungeePlayer(player);
	}

}