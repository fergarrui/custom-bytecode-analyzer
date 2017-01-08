package net.nandgr.cba.cli;

import net.nandgr.cba.custom.model.Rules;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

public class CliHelper {

  private static CommandLineParser commandLineParser = new DefaultParser();
  private static CommandLine commandLine;
  private static Rules rules;

  private CliHelper() {
    throw new IllegalAccessError("Cannot instantiate this utility class.");
  }

  public static void parseCliArguments(String[] args) throws ParseException {
    commandLine = commandLineParser.parse(CliArguments.getOptions(), args);
  }

  public static String getPathToAnalyze() {
    return commandLine.getOptionValue("a");
  }

  public static int getMaxThreads() {
    return Integer.parseInt(commandLine.getOptionValue("t", CliArguments.MAX_THREADS_DEFAULT));
  }

  public static boolean hasOutputDir() {
    return commandLine.hasOption("o");
  }

  public static String getOutputDir() {
    return commandLine.getOptionValue("o", CliArguments.OUTPUT_DEFAULT);
  }

  public static boolean hasMaxItemsInReport() {
    return commandLine.hasOption("i");
  }

  public static int getMaxItemsInReport() {
    return Integer.parseInt(commandLine.getOptionValue("i", CliArguments.MAX_ITEMS_IN_REPORT_DEFAULT));
  }

  public static boolean hasChecks() {
    return commandLine.hasOption("c");
  }

  public static String[] getChecks() {
    return commandLine.getOptionValues("c");
  }

  public static boolean hasCustomFile() {
    return commandLine.hasOption("f");
  }

  public static String getCustomFile() {
    return commandLine.getOptionValue("f");
  }

  public static void printHelp() {
    HelpFormatter helpFormatter = new HelpFormatter();
    helpFormatter.printHelp("java -jar cba-cli.jar [OPTIONS] -a DIRECTORY_TO_ANALYZE", CliArguments.getOptions());
  }

  public static boolean hasHelp() {
    return commandLine.hasOption("h");
  }

  public static boolean hasVerboseDebug() {
    return commandLine.hasOption("v");
  }

  public static boolean hasVerboseTrace() {
    return commandLine.hasOption("vv");
  }

  public static Rules getRules() {
    return rules;
  }

  public static void setRules(Rules rules) {
    CliHelper.rules = rules;
  }
}
