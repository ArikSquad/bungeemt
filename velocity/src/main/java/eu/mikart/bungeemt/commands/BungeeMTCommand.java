package eu.mikart.bungeemt.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.velocitypowered.api.command.CommandSource;
import eu.mikart.bungeemt.IBungeeMT;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.william278.desertwell.about.AboutMenu;
import net.william278.desertwell.util.Version;

@CommandAlias("bungeemt")
@AllArgsConstructor
public class BungeeMTCommand extends BaseCommand {
	public final IBungeeMT plugin;

	@Default
	public void onCommand(CommandSource sender) {
		final AboutMenu menu = AboutMenu.builder()
				.title(Component.text("BungeeMT"))
				.description(Component.text("BungeeCord Title and Actionbar plugin!"))
				.version(Version.fromString(plugin.getPluginVersion()))
				.credits("Author", AboutMenu.Credit.of("ArikSquad").description("Click to visit website").url("https://github.com/ArikSquad"))
				.buttons(AboutMenu.Link.of("https://www.mikart.eu/").text("Website").icon("⛏"),
						AboutMenu.Link.of("https://discord.gg/xh9WAvGdVF").text("Discord").icon("⭐").color(TextColor.color(0x6773f5)))
				.build();

		sender.sendMessage(menu.toComponent());
	}

	@Subcommand("reload")
	@CommandPermission("bungeeminititle.reload")
	public void onReload(CommandSource sender) {
		plugin.reload();
		plugin.getLocales().getLocale("reloaded").ifPresent(sender::sendMessage);
	}
}
