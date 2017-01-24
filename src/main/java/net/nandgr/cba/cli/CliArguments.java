/*
 * Copyright (c) 2016-2017, Fernando Garcia
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.nandgr.cba.cli;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import net.nandgr.cba.custom.model.Rules;
import net.nandgr.cba.exception.BadArgumentsException;
import net.nandgr.cba.exception.BadRulesException;
import net.nandgr.cba.logging.LogHelper;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CliArguments {

  private static final Logger logger = LoggerFactory.getLogger(CliArguments.class);

  private static Options options = new Options();
  public static final String MAX_THREADS_DEFAULT = "1";
  public static final String MAX_ITEMS_IN_REPORT_DEFAULT = "50";
  public static final String OUTPUT_DEFAULT = "report";

  private CliArguments() {
    throw new IllegalAccessError("Cannot instantiate this utility class.");
  }

  static {
    // -h
    Option help = Option
            .builder("h")
            .longOpt("help")
            .desc("Print this message.")
            .build();
    // -a
    Option path = Option
            .builder("a")
            .longOpt("analyze")
            .required()
            .desc("Path of the directory to run the analysis.")
            .hasArg()
            .argName("pathToAnalyze")
            .build();
    // -t -  disabled
    Option maxThreads = Option
            .builder("t")
            .longOpt("max-threads")
            .hasArg()
            .argName("maxThreads")
            .desc("Max number of threads to run the analysis. Default: " + MAX_THREADS_DEFAULT + ".")
            .build();
    // -i
    Option maxReportItems = Option
            .builder("i")
            .longOpt("items-report")
            .hasArg()
            .argName("maxItems")
            .desc("Max number of items per report. If the number of issues found exceeds this value, the report will be split into different files. Useful if expecting too many issues in the report. Default: " + MAX_ITEMS_IN_REPORT_DEFAULT + ".")
            .build();
    // -o
    Option output = Option
            .builder("o")
            .longOpt("output")
            .hasArg()
            .argName("outputDir")
            .desc("Directory to save the report. Warning - if there are already saved reports in this directory they will be overwritten. Default is \"" + OUTPUT_DEFAULT + "\".")
            .build();
    // -c
    Option checks = Option
            .builder("c")
            .longOpt("checks")
            .hasArgs()
            .numberOfArgs(Option.UNLIMITED_VALUES)
            .argName("checks...")
            .desc("Space separated list of custom checks that are going to be run in the analysis.")
            .build();
    // -f
    Option customFile = Option
            .builder("f")
            .longOpt("custom-file")
            .hasArg()
            .argName("customFile")
            .desc("Specify a file in JSON format to run custom rules. Read more in https://github.com/fergarrui/custom-bytecode-analyzer.")
            .build();

    Option verboseDebug = Option
            .builder("v")
            .longOpt("verbose-debug")
            .desc("Increase verbosity to debug mode.")
            .build();

    Option verboseTrace = Option
            .builder("vv")
            .longOpt("verbose-trace")
            .desc("Increase verbosity to trace mode  - makes it slower, use it only when you need.")
            .build();

    options.addOption(help);
    options.addOption(path);
    /*
    disabled until thread safety is done properly.
    options.addOption(maxThreads);
    */
    options.addOption(maxReportItems);
    options.addOption(output);
    options.addOption(checks);
    options.addOption(customFile);
    options.addOption(verboseDebug);
    options.addOption(verboseTrace);
  }

  public static Options getOptions() {
    return options;
  }

  public static void parseArguments(String[] args) throws BadArgumentsException {
    try {
      CliHelper.parseCliArguments(args);
    } catch (ParseException e) {
      throw new BadArgumentsException("Error while parsing arguments", e);
    }

    CliArguments.validateArguments();

    if (CliHelper.hasHelp()) {
      CliHelper.printHelp();
      System.exit(1);
    }
    if (CliHelper.hasVerboseDebug()) {
      logger.info("Setting logger to DEBUG mode.");
      LogHelper.toDebug();
    }
    if (CliHelper.hasVerboseTrace()) {
      logger.info("Setting logger to TRACE mode.");
      LogHelper.toTrace();
    }
    if (CliHelper.hasCustomFile()) {
      String customFilePath = CliHelper.getCustomFile();
      File customFile = new File(customFilePath);
      String json = null;
      try {
        json = FileUtils.readFileToString(customFile);
      } catch (IOException e) {
        throw new BadArgumentsException("Error with provided custom file", e);
      }
      Gson gson = new Gson();
      Rules rules = gson.fromJson(json, Rules.class);
      try {
        rules.validateRules();
      } catch (BadRulesException e) {
        throw new BadArgumentsException("Error when validating custom rules", e);
      }
      CliHelper.setRules(rules);
    }
  }

  public static void validateArguments() throws BadArgumentsException {
    if (CliHelper.hasMaxItemsInReport()) {
      try {
        int number = CliHelper.getMaxItemsInReport();
        if (number < 1) {
          throw new BadArgumentsException("Argument \"-i\" cannot be lower than 1.");
        }
      } catch (NumberFormatException e) {
        throw new BadArgumentsException("Error while validating parameter \"-i\".", e);
      }
    }
    if (!CliHelper.hasCustomFile() && !CliHelper.hasChecks() && !CliHelper.hasHelp()) {
      throw new BadArgumentsException("\"-c\" or \"-f\" arguments should be provided.");
    }
    if (CliHelper.hasVerboseDebug() && CliHelper.hasVerboseTrace()) {
      throw new BadArgumentsException("\"-v\" and \"-vv\" arguments cannot be together. Choose one of them.");
    }
  }

  public static void badArgument(Exception e) {
    if (e.getCause() == null) {
      logger.error("Bad arguments ", e);
    } else {
      logger.error("Bad arguments : {}", e.getCause());
    }
    CliHelper.printHelp();
    System.exit(1);
  }
}
