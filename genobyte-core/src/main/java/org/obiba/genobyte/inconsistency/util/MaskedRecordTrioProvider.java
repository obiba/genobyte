package org.obiba.genobyte.inconsistency.util;

import org.obiba.bitwise.query.QueryResult;
import org.obiba.genobyte.inconsistency.MendelianRecordTrioProvider;

/**
 * An implementation of {@link MendelianRecordTrioProvider} that delegates to another 
 * implementation and applies a mask to the resulting vectors.
 */
public class MaskedRecordTrioProvider implements MendelianRecordTrioProvider {

  private MendelianRecordTrioProvider delegate_;

  private QueryResult mask_ = null;

  /**
   * Builds a new instance of MaskedRecordTrioProvider for the specified delegate and with the specified mask
   *
   * @param delegate the implementation of {@link MendelianRecordTrioProvider} to which calls are delegated
   * @param mask the {@link QueryResult} used to mask the result vectors
   */
  public MaskedRecordTrioProvider(MendelianRecordTrioProvider delegate, QueryResult mask) {
    if(delegate == null) throw new IllegalArgumentException("delegate may not be null");
    if(mask == null) throw new IllegalArgumentException("mask may not be null");
    this.delegate_ = delegate;
    this.mask_ = mask;
  }

  public QueryResult getChildRecords() {
    return delegate_.getChildRecords().and(mask_);
  }

  public QueryResult getFatherRecords(int childRecord) {
    return delegate_.getFatherRecords(childRecord).and(mask_);
  }

  public QueryResult getMotherRecords(int childRecord) {
    return delegate_.getMotherRecords(childRecord).and(mask_);
  }

}
