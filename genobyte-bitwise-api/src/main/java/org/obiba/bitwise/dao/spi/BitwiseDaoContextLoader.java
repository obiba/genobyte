/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.bitwise.dao.spi;

import java.util.*;

public class BitwiseDaoContextLoader {

  private static BitwiseDaoContextLoader loader;

  private ServiceLoader<BitwiseDaoContext> serviceLoader = ServiceLoader.load(BitwiseDaoContext.class);

  private Map<String, BitwiseDaoContext> contextMap = new HashMap<>();

  public static synchronized BitwiseDaoContextLoader getInstance() {
    if (loader == null) {
      loader = new BitwiseDaoContextLoader();
    }
    return loader;
  }

  private synchronized void init() {
    contextMap.clear();
    serviceLoader.reload();
    serviceLoader.iterator().forEachRemaining(c -> contextMap.put(c.getName(), c));
  }

  public void reload() {
    init();
  }

  public Collection<BitwiseDaoContext> getContexts() {
    if (contextMap.isEmpty()) init();
    return contextMap.values();
  }

}
