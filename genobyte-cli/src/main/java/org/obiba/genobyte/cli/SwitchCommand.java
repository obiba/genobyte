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
import org.apache.commons.cli.OptionBuilder;
import org.obiba.genobyte.GenotypingRecordStore;
import org.obiba.genobyte.GenotypingStore;

/**
 * Allows switching between record stores (samples vs. assays).
 */
public class SwitchCommand implements CliCommand {

  public boolean requiresOpenStore() {
    return true;
  }

  public boolean execute(Option opt, CliContext context) {
    GenotypingStore<?, ?, ?, ?> s = context.getStore();
    GenotypingRecordStore<?, ?, ?> current = context.getActiveRecordStore();
    if(current == null || current == s.getAssayRecordStore()) {
      context.setActiveRecordStore(s.getSampleRecordStore());
    } else {
      context.setActiveRecordStore(s.getAssayRecordStore());
    }
    return false;
  }

  public Option getOption() {
    return OptionBuilder.withDescription("switch current active store").withLongOpt("switch").create('s');
  }

}
