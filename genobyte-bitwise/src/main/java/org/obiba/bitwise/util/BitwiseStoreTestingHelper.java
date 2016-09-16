package org.obiba.bitwise.util;

import org.obiba.bitwise.BitVector;
import org.obiba.bitwise.BitwiseStore;
import org.obiba.bitwise.Dictionary;
import org.obiba.bitwise.dao.DaoKey;
import org.obiba.bitwise.dto.BitwiseStoreDto;
import org.obiba.bitwise.schema.FieldMetaData;
import org.obiba.bitwise.schema.StoreSchema;

import java.util.HashMap;
import java.util.Map;

/**
 * A unit testing fixture class that helps create tests on actual BitwiseStore instances.
 * This class does not mock the underlying store, but helps create a store in a more dynamic fashion.
 */
public class BitwiseStoreTestingHelper extends BitwiseStore {

  private StoreSchema mockSchema_ = new StoreSchema();

  private Map<String, Dictionary> dictionaries_ = new HashMap<String, Dictionary>();

  public BitwiseStoreTestingHelper(String name, int capacity) {
    super(new BitwiseStoreDto(name, capacity));
    getDto().setSchema(mockSchema_);
    getDto().setDeleted(new BitVector(capacity).setAll());
    getDto().setCleared(new BitVector(capacity).setAll());
  }

  public BitwiseStoreTestingHelper() {
    this("MOCK_STORE", 10000);
  }

  public void setFieldDict(String name, String d) {
    FieldMetaData fmd = new FieldMetaData();
    fmd.setName(name);
    fmd.setDictionary(d);
    mockSchema_.addField(fmd);
  }

  @Override
  public Dictionary getDictionary(String name) {
    return dictionaries_.get(name);
  }

  public void addDictionary(Dictionary d) {
    dictionaries_.put(d.getName(), d);
  }

  @Override
  public DaoKey getDaoKey() {
    return super.getDaoKey();
  }
}
