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

import com.ibatis.dao.client.Dao;
import com.ibatis.dao.client.DaoException;
import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.engine.impl.DaoContext;
import com.ibatis.dao.engine.impl.DaoImpl;
import com.ibatis.dao.engine.impl.StandardDaoManager;
import com.ibatis.dao.engine.transaction.DaoTransactionManager;
import org.obiba.bitwise.dao.BitVectorDtoDao;
import org.obiba.bitwise.dao.BitwiseStoreDtoDao;
import org.obiba.bitwise.dao.DictionaryDtoDao;
import org.obiba.bitwise.dao.FieldDtoDao;

import java.lang.reflect.Constructor;
import java.util.Properties;

public class BitwiseDaoManagerBuilder {

  private StandardDaoManager daoManager = new StandardDaoManager();

  private Properties properties;

  private BitwiseDaoManagerBuilder(Properties props) {
    this.properties = props == null ? new Properties() : props;
  }

  public static DaoManager build(Properties props) throws DaoException {
    BitwiseDaoManagerBuilder builder = new BitwiseDaoManagerBuilder(props);
    BitwiseDaoContextLoader.getInstance().getContexts().forEach(c -> builder.addDaoContext(c));
    return builder.daoManager;
  }

  private void addDaoContext(BitwiseDaoContext context) {
    try {
      DaoContext daoContext = new DaoContext();
      daoContext.setDaoManager(daoManager);
      daoContext.setId(context.getName());
      setTransactionManager(context, daoContext);
      addDao(daoContext, BitVectorDtoDao.class, context.getBitVectorDtoDao());
      addDao(daoContext, FieldDtoDao.class, context.getFieldDtoDao());
      addDao(daoContext, DictionaryDtoDao.class, context.getDictionaryDtoDao());
      addDao(daoContext, BitwiseStoreDtoDao.class, context.getBitwiseStoreDtoDao());
      daoManager.addContext(daoContext);
    } catch (DaoException e) {
      e.printStackTrace();
    }
  }

  private void setTransactionManager(BitwiseDaoContext context, DaoContext daoContext) throws DaoException {
    try {
      DaoTransactionManager txMgr = (DaoTransactionManager)context.getTransaction().newInstance();
      txMgr.configure(properties);
      daoContext.setTransactionManager(txMgr);
    } catch (Exception e) {
      throw new DaoException("Error while configuring DaoManager.  Cause: " + e.toString(), e);
    }
  }

  private void addDao(DaoContext daoContext, Class daoInterface, Class daoImplementation) {
    try {
      DaoImpl daoImpl = new DaoImpl();
      daoImpl.setDaoManager(daoManager);
      daoImpl.setDaoContext(daoContext);
      daoImpl.setDaoInterface(daoInterface);
      daoImpl.setDaoImplementation(daoImplementation);
      Class daoClass = daoImpl.getDaoImplementation();
      Dao dao;
      try {
        Constructor constructor = daoClass.getConstructor(DaoManager.class);
        dao = (Dao) constructor.newInstance(new Object[]{daoManager});
      } catch (Exception e) {
        dao = (Dao) daoClass.newInstance();
      }
      daoImpl.setDaoInstance(dao);
      daoImpl.initProxy();
      daoContext.addDao(daoImpl);
    } catch (Exception e) {
      throw new DaoException("Error configuring DAO.  Cause: " + e, e);
    }
  }


}
