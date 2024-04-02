package eu.mikart.bungeemt.user;

import com.velocitypowered.api.proxy.ServerConnection;
import eu.mikart.bungeemt.VelocityMT;
import lombok.AllArgsConstructor;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * The user system has been inspired by <a href="https://github.com/William278/HuskTowns">HuskTowns</a>
 * which is licensed under the Apache License 2.0.
 */
@AllArgsConstructor
public class VelocityPlayer implements Player {
	private static final VelocityMT plugin = VelocityMT.getInstance();
	private final com.velocitypowered.api.proxy.Player player;

	@Override
	@NotNull
	public String getName() {
		return player.getUsername();
	}

	@NotNull
	@Override
	public UUID getUniqueId() {
		return player.getUniqueId();
	}

	@Override
	public int getPing() {
		return (int) player.getPing();
	}

	@Override
	@NotNull
	public String getServerName() {
		final Optional<ServerConnection> connection = player.getCurrentServer();
		if (connection.isPresent()) {
			return connection.get().getServerInfo().getName();
		}
		return "";
	}

	@Override
	public int getPlayersOnServer() {
		final Optional<ServerConnection> connection = player.getCurrentServer();
		return connection.map(serverConnection -> serverConnection.getServer().getPlayersConnected().size()).orElse(0);
	}

	@Override
	public boolean hasPermission(String permission) {
		return player.hasPermission(permission);
	}

	@NotNull
	@Override
	public Audience getAudience() {
		return player;
	}

	/**
	 * Adapts a cross-platform {@link Player} to a Velocity {@link com.velocitypowered.api.proxy.Player} object
	 *
	 * @param player {@link Player} to adapt
	 * @return The {@link com.velocitypowered.api.proxy.Player} object, {@code null} if they are offline
	 */
	public static Optional<com.velocitypowered.api.proxy.Player> toVelocity(@NotNull Player player) {
		return plugin.getProxy().getPlayer(player.getUniqueId());
	}

	/**
	 * Adapts a Velocity {@link com.velocitypowered.api.proxy.Player} to a cross-platform {@link Player} object
	 *
	 * @param player {@link com.velocitypowered.api.proxy.Player} to adapt
	 * @return The {@link Player} object
	 */
	@NotNull
	public static VelocityPlayer adapt(com.velocitypowered.api.proxy.Player player) {
		return new VelocityPlayer(player);
	}
}
