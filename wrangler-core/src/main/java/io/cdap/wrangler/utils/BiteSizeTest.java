package io.cdap.wrangler.utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class ByteSizeTest {

  @Test
  public void testBytesConversion() {
    assertEquals(10240L, ByteSize.parse("10KB").getBytes());
    assertEquals(1572864L, ByteSize.parse("1.5MB").getBytes());
    assertEquals(2147483648L, ByteSize.parse("2GB").getBytes());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidByteString() {
    ByteSize.parse("10XY");
  }
}
