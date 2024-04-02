package eu.mikart.bungeemt.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.velocity.contexts.OnlinePlayer;
import com.velocitypowered.api.command.CommandSource;
import eu.mikart.bungeemt.IBungeeMT;
import eu.mikart.bungeemt.user.Player;
import lombok.AllArgsConstructor;

@CommandAlias("actionbar|broadcastactionbar|bcactionbar|bungeeactionbar|bungeeaction|bcaction")
@AllArgsConstructor
public class ActionbarCommand extends BaseCommand {
	public final IBungeeMT plugin;

	@Default
	@HelpCommand
	public void onDefault(CommandSource sender, CommandHelp help) {
		help.showHelp();
	}

	@Subcommand("player")
	@Syntax("<player> <message>")
	@CommandCompletion("@players @nothing")
	@CommandPermission("bungeeminititle.actionbar.player")
	public void onActionbar(CommandSource player, OnlinePlayer target, String message) {
		if(!plugin.getSettings().getCommands().isActionbar()) {
			plugin.getLocales().getLocale("command_disabled").ifPresent(player::sendMessage);
			return;
		}

		target.getPlayer().sendActionBar(plugin.parseLegacyAndMiniMessage(message));
	}

	@Subcommand("everyone")
	@Syntax("<message>")
	@CommandCompletion("@nothing")
	@CommandPermission("bungeeminititle.actionbar.everyone")
	public void onEveryoneActionbar(CommandSource player, String message) {
		if(!plugin.getSettings().getCommands().isActionbar()) {
			plugin.getLocales().getLocale("command_disabled").ifPresent(player::sendMessage);
			return;
		}

		for (Player p : plugin.getOnlinePlayers()) {
			if (p.getServerName() != null && plugin.getSettings().getGlobal_disabled_servers().contains(p.getServerName())) {
				continue;
			}

			p.getAudience().sendActionBar(plugin.parseLegacyAndMiniMessage(message));
		}
	}

}
