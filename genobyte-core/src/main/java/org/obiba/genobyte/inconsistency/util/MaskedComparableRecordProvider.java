package org.obiba.genobyte.inconsistency.util;

import org.obiba.bitwise.query.QueryResult;
import org.obiba.genobyte.inconsistency.ComparableRecordProvider;

/**
 * An implementation of {@link ComparableRecordProvider} that delegates to another implementation and applies a mask to the result.
 */
public class MaskedComparableRecordProvider implements ComparableRecordProvider {

  private ComparableRecordProvider delegate_;

  private QueryResult mask_ = null;

  /**
   * Builds a MaskedComparableRecordProvider instance that delegates the interface to <code>delegate</code> and
   * applies <code>mask</code> to the result.
   *
   * @param delegate the {@link ComparableRecordProvider} implemementation to delegate to
   * @param mask     the {@link QueryResult} used a as mask
   */
  public MaskedComparableRecordProvider(ComparableRecordProvider delegate, QueryResult mask) {
    if (delegate == null) throw new IllegalArgumentException("delegate may not be null");
    if (mask == null) throw new IllegalArgumentException("mask may not be null");
    this.delegate_ = delegate;
    this.mask_ = mask;
  }

  public QueryResult getComparableRecords(int referenceRecord) {
    return delegate_.getComparableRecords(referenceRecord).and(mask_);
  }

  public QueryResult getComparableReferenceRecords() {
    return delegate_.getComparableReferenceRecords().and(mask_);
  }

}
