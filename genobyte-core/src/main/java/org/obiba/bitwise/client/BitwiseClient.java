/*******************************************************************************
 * Copyright 2007(c) Genome Quebec. All rights reserved.
 * <p>
 * This file is part of GenoByte.
 * <p>
 * GenoByte is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * GenoByte is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.obiba.bitwise.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.BitwiseStoreUtil;
import org.obiba.bitwise.query.Query;
import org.obiba.bitwise.query.QueryParser;
import org.obiba.bitwise.query.QueryResult;
import org.obiba.bitwise.util.StringUtil;

/**
 * Provides a shell interface to run queries on a bitwise store.
 */
public class BitwiseClient {

  HelpCommand help = new HelpCommand();

  Map<String, CliCommand> commandMap = new HashMap<String, CliCommand>();

  Options options = new Options();

  public BitwiseClient() {
    registerCommand(help);
    registerCommand(new QuitCommand());
    registerCommand(new PrintCommand());
    registerCommand(new PrintRecordCommand());
    registerCommand(new OpenCommand());
  }

  /**
   * Adds a new command to the shell.
   * @param command
   */
  public void registerCommand(CliCommand command) {
    Option o = command.getOption();
    commandMap.put(o.getOpt(), command);
    options.addOption(o);
  }

  /**
   * Prints the prompt data preceding every command line.
   * @param context is the ClientContext of this client.
   */
  private void prompt(ClientContext context) {
    System.out.print(context.getStore().getName() + "> ");
  }

  /**
   * Activates the command line client without providing any store information.
   * @throws IOException
   */
  public void execute() throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    BasicParser bp = new BasicParser();
    System.out.print("Specify a Store Name: ");
    String storeName = br.readLine();
    execute(storeName);
  }

  /**
   * Activates the command line client by providing a BitwiseStore name to open.
   * @param pStoreName the name of the store to open.
   * @throws IOException
   */
  public void execute(String pStoreName) throws IOException {
    //Open the store
    BitwiseStoreUtil bwsUtil = BitwiseStoreUtil.getInstance();
    BitwiseStore store = bwsUtil.open(pStoreName);
    execute(store);
  }

  /**
   * Activates the command line client by providing a BitwiseStore object.
   * @param pStore is the BitwiseStore to be queried
   * @throws IOException
   */
  public void execute(BitwiseStore store) throws IOException {
    //Start command line client
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    BasicParser bp = new BasicParser();

    //Prepare ClientContext
    ClientContext context = new ClientContext();
    context.setStore(store);

    System.out.println("Type '-h' for help, '-q' to quit.");

    //Loop as long as there are no options asking to quit
    boolean quit = false;
    while(!quit) {
      store = context.getStore();   //Store might have been switched
      prompt(context);
      String str = br.readLine();
      CommandLine cl = null;
      try {
        cl = bp.parse(options, str.split(" "));
      } catch(ParseException e) {
        quit = help.execute(null, context);
      }

      if(cl != null) {
        Iterator<Option> commands = cl.iterator();
        while(commands.hasNext()) {
          Option o = commands.next();
          CliCommand c = commandMap.get(o.getOpt());
          if(c == null) {
            System.err.println("Cannot find command for option [" + o + "]");
            quit = help.execute(null, context);
          } else {
            try {
              quit = c.execute(o, context);
            } catch(ParseException e) {
              quit = help.execute(null, context);
            }
          }
        }

        //The given command is a query, as there are no options specified
        if(cl.getOptions() == null || cl.getOptions().length == 0) {
          try {
            QueryParser parser = new QueryParser();
            String[] args = cl.getArgs();
            String queryString = StringUtil.aggregate(args, " ");
            if(StringUtil.isEmptyString(queryString) == false) {
              long start = System.currentTimeMillis();
              Query q = parser.parse(queryString);
              QueryResult qr = q.execute(context.getStore());
              context.setLastResult(qr);
              long end = System.currentTimeMillis();

              //Prepare result display on screen
              List<String> fieldList = new Vector<String>();
              //Filter out template fields
              for(String field : store.getFieldList()) {
                if(field.matches(".*_\\d+")) {
                  continue;
                }
                fieldList.add(field);
              }
              ResultDisplay rd = new ResultDisplay(fieldList);
//              rd.setDisplayType(ResultDisplay.DisplayType.PLAIN);
              int hitIndex = qr.next(0);
              while(hitIndex != -1) {
                rd.putRecord(store, hitIndex);
                hitIndex = qr.next(hitIndex + 1);
              }

              //Display results in console
              System.out.println(rd.getOutput());
              System.out.println(qr.count() + " results in " + (end - start) + " milliseconds.\n");
            }
          } catch(org.obiba.bitwise.query.ParseException e) {
            System.err.println(e.getMessage());
          } catch(Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  /**
   * Display console help.
   */

  class HelpCommand implements CliCommand {

    private HelpFormatter hf = new HelpFormatter();

    public boolean execute(Option opt, ClientContext context) {
      PrintWriter pw = new PrintWriter(System.err);
      hf.printHelp(pw, 100, "BitwiseClient", "", options, 3, 4, "", true);
      pw.flush();
      return false;
    }

    public Option getOption() {
      return OptionBuilder.withDescription("print this help message").withLongOpt("help").create('h');
    }
  }

  /**
   * Exit client.
   */
  class QuitCommand implements CliCommand {
    public boolean execute(Option opt, ClientContext context) {
      return true;
    }

    public Option getOption() {
      return OptionBuilder.withDescription("quit").withLongOpt("quit").create('q');
    }
  }
}
