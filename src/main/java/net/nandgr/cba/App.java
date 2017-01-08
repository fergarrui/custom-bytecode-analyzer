package net.nandgr.cba;

import net.nandgr.cba.cli.CliArguments;
import net.nandgr.cba.cli.CliHelper;
import net.nandgr.cba.exception.BadArgumentsException;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

  private static final Logger logger = LoggerFactory.getLogger(App.class);

  private App() {
    throw new IllegalAccessError("Cannot instantiate this main class.");
  }

  public static void main(String[] args)  {

    try {
      CliArguments.parseArguments(args);
    } catch (BadArgumentsException e) {
      CliArguments.badArgument(e);
    }

    long startTime = System.nanoTime();
    logger.info("Starting Analyzer...");
    try {
      JarWalker jarWalker = new JarWalker(CliHelper.getPathToAnalyze(), CliHelper.getMaxThreads());
      jarWalker.walk();
      long endTime = System.nanoTime();
      logger.info("Analysis done in {} seconds.", (endTime - startTime) / 1000000000.0);
    } catch (ReflectiveOperationException e) {
      CliArguments.badArgument(e);
    } catch (IOException e) {
      logger.error("Error while analyzing provided path.",e);
    }
  }
}
