package br.com.sinerji.comprascrawler;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class CommandOptions {
	public static final String HELP = "h";
	
	private static Options OPTIONS;
	
	public static Options getOptions() {
		if (OPTIONS == null) {
			OPTIONS = new Options();
			OPTIONS.addOption(HELP, "help", false, "Usage help");
		}
		
		return OPTIONS;
	}

	public static void printCommandsHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("compras-crawler", "\n", OPTIONS, "", true);
	}
}
