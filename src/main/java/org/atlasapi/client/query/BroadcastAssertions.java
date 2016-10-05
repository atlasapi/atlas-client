package org.atlasapi.client.query;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class BroadcastAssertions {

    private static final String SEPARATOR = ",";

    private final Optional<ImmutableList<BroadcastAssertion>> assertions;

    private BroadcastAssertions(Builder builder) {
        this.assertions = builder.assertions.map(ImmutableList::copyOf);

        if (this.assertions.isPresent()) {
            checkArgument(!this.assertions.get().isEmpty());
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        if (!assertions.isPresent()) {
            return "";
        }

        return assertions.get()
                .stream()
                .map(BroadcastAssertion::toString)
                .collect(Collectors.joining(SEPARATOR));
    }

    private static class BroadcastAssertion {

        private static final String SEPARATOR = "|";

        private final String channelUri;
        private final DateTime from;
        private final DateTime to;

        private BroadcastAssertion(String channelUri, DateTime from, DateTime to) {
            this.channelUri = checkNotNull(channelUri);
            this.from = checkNotNull(from);
            this.to = checkNotNull(to);

            checkArgument(this.from.isBefore(this.to));
        }

        public static BroadcastAssertion create(
                String channelUri,
                DateTime from,
                DateTime to
        ) {
            return new BroadcastAssertion(channelUri, from, to);
        }

        @Override
        public String toString() {
            return quote(channelUri)
                    + SEPARATOR
                    + quote(from.toString())
                    + SEPARATOR
                    + quote(to.toString());
        }

        private String quote(String input) {
            return "\"" + input + "\"";
        }
    }

    public static final class Builder {

        private Optional<List<BroadcastAssertion>> assertions = Optional.empty();

        private Builder() {
        }

        public Builder withAssertion(String channelUri, DateTime from, DateTime to) {
            if (!assertions.isPresent()) {
                assertions = Optional.of(Lists.newArrayList());
            }
            assertions.get().add(BroadcastAssertion.create(channelUri, from, to));

            return this;
        }

        public BroadcastAssertions build() {
            return new BroadcastAssertions(this);
        }
    }
}
