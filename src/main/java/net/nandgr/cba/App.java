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

  public static void main(String[] args) {

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
