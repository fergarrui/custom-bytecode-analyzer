package net.nandgr.cba.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public class LogHelper {

  private LogHelper() {
    throw new IllegalAccessError("Cannot instantiate this utility class.");
  }
  public static void toDebug() {
    Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    root.setLevel(Level.DEBUG);
  }

  public static void toTrace() {
    Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    root.setLevel(Level.TRACE);
  }
}
