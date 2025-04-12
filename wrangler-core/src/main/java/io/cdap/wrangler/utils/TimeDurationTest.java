package io.cdap.wrangler.utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class TimeDurationTest {

  @Test
  public void testTimeConversion() {
    assertEquals(5_000_000L, TimeDuration.parse("5ms").getNanos());
    assertEquals(2_100_000_000L, TimeDuration.parse("2.1s").getNanos());
    assertEquals(60_000_000_000L, TimeDuration.parse("1m").getNanos());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidTimeString() {
    TimeDuration.parse("100xyz");
  }
}
