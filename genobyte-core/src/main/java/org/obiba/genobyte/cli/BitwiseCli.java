/*******************************************************************************
 * Copyright 2007(c) Génome Québec. All rights reserved.
 * 
 * This file is part of GenoByte.
 * 
 * GenoByte is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * GenoByte is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *******************************************************************************/
package org.obiba.genobyte.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.obiba.bitwise.query.Query;
import org.obiba.bitwise.query.QueryParser;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.bitwise.util.StringUtil;
import org.obiba.genobyte.GenotypingStore;


/**
 * Command Line Interface for manipulating {@link GenotypingStore}s.
 * <p/>
 * Used as a shell for executing {@link CliCommand}s. Custom commands may be registered through 
 * the {@link BitwiseCli#registerCommand(CliCommand)} method. Commands have a long and a short option string, 
 * the short option string must be unique. Registering two commands with the same option string will
 * throw an exception. 
 */
public class BitwiseCli {

  PrintStream output = System.out;
  InputStream input = System.in;
  HelpCommand help = new HelpCommand();
  Map<String, CliCommand> commandMap = new HashMap<String, CliCommand>();
  Options options = new Options();
  Options noStoreOptions = new Options();

  public BitwiseCli() {
    registerCommand(help);
    registerCommand(new QuitCommand());
    registerCommand(new PrintCommand());
    registerCommand(new SwitchCommand());
    registerCommand(new PrintRecordCommand());
    registerCommand(new CloseCommand());
    registerCommand(new DropCommand());
    registerCommand(new StatsCommand());
    registerCommand(new InconsistenciesCommand());
    registerCommand(new PrintHistoryCommand());
  }

  public BitwiseCli(PrintStream output) {
    this();
    this.output = output;
  }

  public BitwiseCli(PrintStream output, InputStream input) {
    this(output);
    this.input = input;
  }

  /**
   * Adds a {@link CliCommand} to the set of registered commands. If a command
   * with the same short or long option string is already registered, an IllegalArgumentException is thrown.
   * @param command the command to register.
   * @throws IllegalArgumentException when a command with the same short option string already exists.
   */
  public void registerCommand(CliCommand command) {
    Option o = command.getOption();
    if(commandMap.containsKey(o.getLongOpt())) {
      throw new IllegalArgumentException("A command with key ["+o.getLongOpt()+"] is already registered.");
    }
    if(o.getOpt() != null) {
      for (CliCommand c : commandMap.values()) {
        Option commandOption = c.getOption();
        if(commandOption.getOpt() != null && commandOption.getOpt().equals(o.getOpt())) {
          throw new IllegalArgumentException("Illegal option ["+o.getLongOpt()+"] conflicts with existing option ["+commandOption.getLongOpt()+"].");
        }
      }
    }
    commandMap.put(o.getLongOpt(), command);
    options.addOption(o);
    if(command.requiresOpenStore() == false) {
      noStoreOptions.addOption(o);
    }
  }

  /**
   * Used to prompt the user for input.
   * @param context the current context of the CLI execution.
   */
  private void prompt(CliContext context) {
    StringBuilder sb = new StringBuilder();
    if(context.getActiveRecordStore() != null) {
      sb.append(context.getActiveRecordStore().getStore().getName());
    }
    sb.append("> ");
    output.print(sb.toString());
  }

  /**
   * Starts the CLI shell.
   * 
   * @throws IOException when an error occurs while reading user input.
   */
  public void execute() throws IOException {
    CliContext context = new CliContext(this.output);
    BufferedReader br = new BufferedReader(new InputStreamReader(input));
    BasicParser bp = new BasicParser();
    output.println("Type '-h' for help, '-q' to quit.");
    while(true) {
      prompt(context);
      boolean quit = false;
      String str = br.readLine();
      CommandLine cl = null;
      try {
        if(context.getStore() == null) {
          cl = bp.parse(noStoreOptions, str.split(" "));
        } else {
          cl = bp.parse(options, str.split(" "));
        }
      } catch (ParseException e) {
        quit = help.execute(null, context);
      }

      if(cl != null) {
        Iterator<Option> commands = cl.iterator();
        // We don't iterate to make sure we execute only one command
        if(commands.hasNext()) {
          Option o = commands.next();
          CliCommand c = commandMap.get(o.getLongOpt());
          if(c == null) {
            throw new IllegalStateException("No CliCommand associated with option ["+o.getOpt()+"]");
          } else {
            try {
              quit = c.execute(o, context);
            } catch (ParseException e) {
              quit = help.execute(null, context);
            } catch(Exception e) {
              output.println("An unexpected error occurred while executing the command: " + e.getMessage());
            }
          }
        }

        // Not handled by any command: it should be a query.
        String queryString = str;
        if(context.getStore() != null && (cl.getOptions() == null || cl.getOptions().length == 0)) {
          try {
            QueryParser parser = new QueryParser();
            long start = System.currentTimeMillis();
            if(StringUtil.isEmptyString(queryString) == false) {
              Query q = parser.parse(queryString);
              QueryResult qr = q.execute(context.getActiveRecordStore().getStore());
              String reference = context.addQuery(queryString, qr);
              long end = System.currentTimeMillis(); 
              output.println(reference + ": " + qr.count() + " results in " + (end - start) + " milliseconds.");
            }
          } catch (org.obiba.bitwise.query.UnknownFieldException e) {
            output.println(e.getMessage());
          } catch (org.obiba.bitwise.query.ParseException e) {
            output.println("The query ["+queryString+"] is invalid. Please refer to the query syntax for more information.");
          } catch(Exception e) {
            output.println("An unexpected error occurred while executing the query. The following data may be helpful to debug the problem.");
            e.printStackTrace(output);
          } 
        }
      }
      if(quit) break;
    }
  }

  public class HelpCommand implements CliCommand {

    private HelpFormatter hf = new HelpFormatter();
    
    public boolean requiresOpenStore() {
      return false;
    }

    public boolean execute(Option opt, CliContext context) {
      PrintWriter pw = new PrintWriter(context.getOutput());
      if(context.getStore() != null) {
        hf.printOptions(pw, 100, options, 1, 4);
      } else {
        hf.printOptions(pw, 100, noStoreOptions, 1, 4);
      }
      pw.flush();
      return false;
    }

    public Option getOption() {
      return OptionBuilder.withDescription("print this help message").withLongOpt("help").create('h');
    }
  }

  public class QuitCommand implements CliCommand {

    public boolean requiresOpenStore() {
      return false;
    }

    public boolean execute(Option opt, CliContext context) {
      if(context.getStore() != null) {
        context.getStore().close();
      }
      return true;
    }

    public Option getOption() {
      return OptionBuilder.withDescription("quit").withLongOpt("quit").create('q');
    }
  }
}
