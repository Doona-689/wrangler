package io.cdap.wrangler.steps.aggregate;

import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.utils.TestingRig;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class AggregateStatsTest {

  @Test
  public void testAggregateStatsTotal() throws Exception {
    List<Row> rows = Arrays.asList(
      new Row("data_transfer_size", "1KB").add("response_time", "2s"),
      new Row("data_transfer_size", "2048B").add("response_time", "500ms"),
      new Row("data_transfer_size", "1MB").add("response_time", "1.5s")
    );

    String[] recipe = new String[] {
      "aggregate-stats :data_transfer_size :response_time total_size_mb total_time_sec"
    };

    List<Row> results = TestingRig.execute(recipe, rows);

    // Verify result size
    Assert.assertEquals(1, results.size());

    // Calculation
    double expectedTotalSizeInBytes = 1024 + 2048 + 1_048_576; // bytes
    double expectedTotalSizeInMB = expectedTotalSizeInBytes / (1024.0 * 1024.0);

    double expectedTotalTimeInMillis = 2000 + 500 + 1500;
    double expectedTotalTimeInSeconds = expectedTotalTimeInMillis / 1000.0;

    // Verify values with tolerance
    Assert.assertEquals(expectedTotalSizeInMB,
      (Double) results.get(0).getValue("total_size_mb"), 0.001);

    Assert.assertEquals(expectedTotalTimeInSeconds,
      (Double) results.get(0).getValue("total_time_sec"), 0.001);
  }
}
