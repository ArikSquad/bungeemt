package eu.mikart.bungeemt;

import eu.mikart.bungeemt.config.ConfigProvider;
import eu.mikart.bungeemt.user.Player;
import eu.mikart.bungeemt.utils.Version;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface IBungeeMT extends ConfigProvider {

	String getPluginVersion();

	@NotNull
	default Version getVersion() {
		return new Version(getPluginVersion());
	}

	eu.mikart.bungeemt.utils.Logger getPluginLogger();

	@NotNull
	Audience getConsole();

	Collection<eu.mikart.bungeemt.user.Player> getOnlinePlayers();

	Collection<eu.mikart.bungeemt.user.Player> getOnlinePlayersOnServer(@NotNull Player player);

	@NotNull
	String getPlatform();

	void reload();

	default Component parseLegacyAndMiniMessage(String message) {
		return MiniMessage.miniMessage().deserialize(message, Placeholder.component("space", Component.space()));
	}
}
