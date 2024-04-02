package eu.mikart.bungeemt.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import eu.mikart.bungeemt.IBungeeMT;
import eu.mikart.bungeemt.user.BungeePlayer;
import lombok.AllArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.william278.desertwell.about.AboutMenu;
import net.william278.desertwell.util.Version;

@CommandAlias("bungeemt")
@AllArgsConstructor
public class BungeeMTCommand extends BaseCommand {
	public final IBungeeMT plugin;

	@Default
	public void onCommand(CommandSender sender) {
		if (sender instanceof ProxiedPlayer player) {
			BungeePlayer bungeePlayer = BungeePlayer.adapt(player);
			Audience audience = bungeePlayer.getAudience();
			final AboutMenu menu = AboutMenu.builder()
					.title(Component.text("BungeeMT"))
					.description(Component.text("BungeeCord Title and Actionbar plugin!"))
					.version(Version.fromString(plugin.getPluginVersion()))
					.credits("Author", AboutMenu.Credit.of("ArikSquad").description("Click to visit website").url("https://github.com/ArikSquad"))
					.buttons(AboutMenu.Link.of("https://www.mikart.eu/").text("Website").icon("⛏"),
							AboutMenu.Link.of("https://discord.gg/xh9WAvGdVF").text("Discord").icon("⭐").color(TextColor.color(0x6773f5)))
					.build();

			audience.sendMessage(menu.toComponent());
		} else {
			plugin.getConsole().sendMessage(Component.text("BungeeMT v" + plugin.getPluginVersion()));
		}
	}

	@Subcommand("reload")
	@CommandPermission("bungeeminititle.reload")
	public void onReload(CommandSender sender) {
		Audience audience;
		if (sender instanceof ProxiedPlayer proxiedPlayer) {
			audience = BungeePlayer.adapt(proxiedPlayer).getAudience();
		} else {
			audience = plugin.getConsole();
		}

		plugin.reload();
		plugin.getLocales().getLocale("reloaded").ifPresent(audience::sendMessage);
	}
}
