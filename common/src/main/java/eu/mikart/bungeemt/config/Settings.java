package eu.mikart.bungeemt.config;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

import java.util.List;

@Getter
@Configuration
@SuppressWarnings("FieldMayBeFinal")
public class Settings {

	protected static final String CONFIG_HEADER = """
            ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
            ┃       BungeeMT Config        ┃
            ┃    Developed by ArikSquad    ┃
            ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
            ┗╸ Config Help: https://discord.gg/EVTWHqE4KH""";

	@Comment("Locale of the default language file to use. All available locales can be found in plugin page.")
	private String language = Locales.DEFAULT_LOCALE;

	@Comment("Whether to enable debug mode. This will print additional information to the console.")
	private boolean debug = false;

	@Comment("Whether to enable the plugin's update checker.")
	private boolean updateChecker = true;

	@Comment("All settings related to the commands.")
	private CommandSettings commands = new CommandSettings();

	@Comment("Whether to load the acf.yml file for changing internal command outputs. This is BUNGEE only since acf hasn't implemented this in Velocity")
	private boolean acfModification = false;

	@Comment("List of servers where the no titles or actionbars using `everyone` is disabled.")
	private List<String> global_disabled_servers = List.of("servername12345");

	@Comment("Whether to enable automatic titles.")
	private boolean autotitlesEnabled = false;

	@Comment("List of titles to be displayed automatically.")
	private List<TitleSettings> titles = List.of(new TitleSettings());



	@Getter
	@Configuration
	public static class CommandSettings {
		private boolean actionbar = true;
		private boolean title = true;
	}

	@Getter
	@Configuration
	public static class TitleSettings {
		private String title = "<color:#FFD700>\uD83C\uDF1F ᴛɪᴛʟᴇ";
		private String subtitle = "<color:#FFD700>\uD83C\uDF1F sᴜʙᴛɪᴛʟᴇ";
		private int fadeIn = 1;
		private int stay = 2;
		private int fadeOut = 1;
		private String interval = "30s";
		private List<String> disabled_servers = List.of("servername12345");
	}

}
