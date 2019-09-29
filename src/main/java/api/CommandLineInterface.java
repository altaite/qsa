package api;

import analysis.SearchJob;
import analysis.statistics.BiwordsGenerator;
import java.io.File;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author Antonin Pavelka
 */
public class CommandLineInterface {

	private boolean analysis;
	private File home;

	private void run(String[] args) {
		parseArguments(args);
		runTask();
	}

	private void runTask() {
		if (analysis) {
			BiwordsGenerator bg = new BiwordsGenerator(home);
			bg.generate();
		} else {
			SearchJob job = new SearchJob(home);
			job.run();
		}
	}

	public void parseArguments(String[] args) {
		Options options = new Options();
		options.addOption(Option.builder("h")
			.desc("path to home directory, where all the data will be stored")
			.hasArg()
			.build());

		options.addOption(Option.builder("a")
			.desc("path to home directory, where all the data will be stored")
			.build());

		CommandLineParser parser = new DefaultParser();
		try {
			org.apache.commons.cli.CommandLine cl = parser.parse(options, args);
			if (cl.hasOption("h")) {
				home = new File(cl.getOptionValue("h").trim());
			} else {
				throw new ParseException("No -h parameter, please specify the home directory.");
			}
			if (cl.hasOption("a")) {
				analysis = true;
			}
		} catch (ParseException exp) {
			throw new RuntimeException("Parsing arguments has failed: " + exp.getMessage());
		}
	}

	public static void main(String[] args) {
		CommandLineInterface cli = new CommandLineInterface();
		cli.run(args);
	}

}
