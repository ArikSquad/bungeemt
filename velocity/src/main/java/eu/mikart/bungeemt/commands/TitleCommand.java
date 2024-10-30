package eu.mikart.bungeemt.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.velocity.contexts.OnlinePlayer;
import com.velocitypowered.api.command.CommandSource;
import eu.mikart.bungeemt.IBungeeMT;
import eu.mikart.bungeemt.user.Player;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import java.time.Duration;

@CommandAlias("title|broadcasttitle|bctitle|bungeetitle|btitle")
@AllArgsConstructor
public class TitleCommand extends BaseCommand {
	public final IBungeeMT plugin;

	@Default
	@HelpCommand
	public void onDefault(CommandSource sender, CommandHelp help) {
		help.showHelp();
	}

	@Subcommand("player")
	@Syntax("<player> <fade> <stay> <fadeOut> <title> [subtitle]")
	@CommandCompletion("@players @range:20 @range:20 @range:20 @nothing @nothing")
	@CommandPermission("bungeeminititle.title.player")
	public void onTitle(CommandSource player, OnlinePlayer target, int fade, int stay, int fadeOut, String title, @Optional String subtitle) {
		if(!plugin.getSettings().getCommands().isTitle()) {
			plugin.getLocales().getLocale("command_disabled").ifPresent(player::sendMessage);
			return;
		}
		target.getPlayer().showTitle(
				Title.title(
						plugin.parseLegacyAndMiniMessage(title),
						subtitle != null ? plugin.parseLegacyAndMiniMessage(subtitle) : Component.empty(),
						Title.Times.times(Duration.ofSeconds(fade), Duration.ofSeconds(stay), Duration.ofSeconds(fadeOut))
				)
		);
	}

	@Subcommand("everyone")
	@Syntax("<fade> <stay> <fadeOut> <title> [subtitle]")
	@CommandCompletion("@range:20 @range:20 @range:20 @nothing @nothing")
	@CommandPermission("bungeeminititle.title.everyone")
	public void onTitle(CommandSource player, int fade, int stay, int fadeOut, String title, @Optional String subtitle) {
		if(!plugin.getSettings().getCommands().isTitle()) {
			plugin.getLocales().getLocale("command_disabled").ifPresent(player::sendMessage);
			return;
		}

		for (Player p : plugin.getOnlinePlayers()) {
			if (p.getServerName() != null && plugin.getSettings().getGlobal_disabled_servers().contains(p.getServerName())) {
				continue;
			}

			p.getAudience().showTitle(
					Title.title(
							plugin.parseLegacyAndMiniMessage(title),
							subtitle != null ? plugin.parseLegacyAndMiniMessage(subtitle) : Component.empty(),
							Title.Times.times(Duration.ofSeconds(fade), Duration.ofSeconds(stay), Duration.ofSeconds(fadeOut))
					)
			);
		}
	}

}
