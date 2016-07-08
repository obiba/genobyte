package org.obiba.bitwise.util;

public final class PrintGC {
  public static long printGC() {
    runGC();
    return usedMemory();
  }

  private static void runGC() {
    // It helps to call Runtime.gc()
    // using several method calls:
    for (int r = 0; r < 4; ++r)
      _runGC();
  }

  private static void _runGC() {
    long usedMem1 = usedMemory(), usedMem2 = Long.MAX_VALUE;
    for (int i = 0; (usedMem1 < usedMem2) && (i < 500); ++i) {
      s_runtime.runFinalization();
      s_runtime.gc();
      Thread.yield();

      usedMem2 = usedMem1;
      usedMem1 = usedMemory();
    }
  }

  private static long usedMemory() {
    return s_runtime.totalMemory() - s_runtime.freeMemory();
  }

  private static final Runtime s_runtime = Runtime.getRuntime();
}
