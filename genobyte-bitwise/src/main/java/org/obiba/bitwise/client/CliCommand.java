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

import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/**
 * Defines an option offered by the shell client, such as help or quit commands.
 */
public interface CliCommand {

  /**
   * Creates an Apache Common option object with its mapping to a command in the shell.
   *
   * @return the newly created <tt>Option</tt> instance.
   */
  public Option getOption();

  /**
   * Defines the routine to be execute when the option is used in the shell client.
   *
   * @param opt     the information on the option executed in the client.
   * @param context the client session contextual information.
   * @return <tt>true</tt> if the shell client session should end after this command.
   * <tt>false</tt> if it should continue.
   * @throws ParseException when an exception occurs at the parsing of the provided option parameters.
   */
  public boolean execute(Option opt, ClientContext context) throws ParseException;

}
