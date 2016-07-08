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
package org.obiba.genobyte.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/**
 * Commands used in a {@link BitwiseCli} instance.
 * <p/>
 * Implementations should provide an instance of <tt>Option</tt> that is unique for this command.
 * The {@link CliCommand#execute(Option, CliContext)} command is called and should return true if the shell
 * should exit upon completion.
 */
public interface CliCommand {

  /**
   * Returns true if this command requires an opened store to execute.
   *
   * @return true if the command requires an opened store to execute.
   */
  public boolean requiresOpenStore();

  /**
   * Returns the Option instance associated with this command.
   * @return the option instance associated with this command.
   */
  public Option getOption();

  /**
   * Execute the command
   *
   * @param opt the instance of Option from which arguments may be fetched.
   * @param context the current command line interface context.
   * @return true if the shell should exit once the command has been executed.
   * @throws ParseException when an error occurs while parsing arguments.
   */
  public boolean execute(Option opt, CliContext context) throws ParseException;

}
