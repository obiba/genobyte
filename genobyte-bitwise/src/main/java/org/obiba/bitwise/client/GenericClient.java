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

import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.util.DefaultConfigurationPropertiesProvider;

import java.io.IOException;

public class GenericClient {

  public GenericClient() {
    super();
  }

  public static void main(String[] args) {
    String storeLocation = "./";
    String storeName = null;
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      if (arg.equalsIgnoreCase("--location")) {
        storeLocation = args[++i];
      } else if (arg.equalsIgnoreCase("--name")) {
        storeName = args[++i];
      }
    }

    DefaultConfigurationPropertiesProvider.setRoot(storeLocation);

    BitwiseStore myStore = null;
    try {
      //Bitwise client test
      BitwiseClient myClient = new BitwiseClient();
      try {
        if (storeName != null) {
          myClient.execute(storeName);
        } else {
          myClient.execute();
        }

      } catch (IOException e) {
        System.out.println("An error has occured: " + e.toString());
      }

    } finally {
      if (myStore != null) {
        myStore.endTransaction();
        myStore.close();
      }
    }

    System.out.println("Finished.");
  }
}
