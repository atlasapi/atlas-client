package org.atlasapi.client.query;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ContentWriteOptionsTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testOptionDefaults() throws Exception {
        ContentWriteOptions options = ContentWriteOptions.builder().build();

        assertThat(options.isOverwriteExisting(), is(false));
        assertThat(options.isAsync(), is(false));
        assertThat(options.getBroadcastAssertions().isPresent(), is(false));
    }

    @Test
    public void overwriteExistingAndBroadcastAssertionsAreMutuallyExclusive() throws Exception {
        exception.expect(IllegalArgumentException.class);
        ContentWriteOptions.builder()
                .withOverwriteExisting()
                .withBroadcastAssertions(BroadcastAssertions.builder().build())
                .build();
    }

    @Test
    public void asyncAndBroadcastAssertionsAreMutuallyExclusive() throws Exception {
        exception.expect(IllegalArgumentException.class);
        ContentWriteOptions.builder()
                .withAsync()
                .withBroadcastAssertions(BroadcastAssertions.builder().build())
                .build();
    }
}
