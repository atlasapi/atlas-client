package org.atlasapi.client.query;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ContentWriteOptions {

    private final boolean overwriteExisting;
    private final boolean async;
    private final Optional<BroadcastAssertions> broadcastAssertions;

    private ContentWriteOptions(
            boolean overwriteExisting,
            boolean async,
            Optional<BroadcastAssertions> broadcastAssertions
    ) {
        this.overwriteExisting = overwriteExisting;
        this.async = async;
        this.broadcastAssertions = checkNotNull(broadcastAssertions);

        checkArgument(!(overwriteExisting && broadcastAssertions.isPresent()),
                "Overwrite existing and broadcast assertions are mutually exclusive");

        checkArgument(!(async && broadcastAssertions.isPresent()),
                "Async and broadcast assertions are mutually exclusive");
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isOverwriteExisting() {
        return overwriteExisting;
    }

    public boolean isAsync() {
        return async;
    }

    public Optional<BroadcastAssertions> getBroadcastAssertions() {
        return broadcastAssertions;
    }

    public static final class Builder {

        private boolean overwriteExisting = false;
        private boolean async = false;
        private Optional<BroadcastAssertions> broadcastAssertions = Optional.empty();

        private Builder() {
        }

        /**
         * Specify that the item written should overwrite any existing item rather than
         * get merged with it
         */
        public Builder withOverwriteExisting() {
            this.overwriteExisting = true;
            return this;
        }

        /**
         * Request that the write be executed asynchronously by the server
         */
        public Builder withAsync() {
            this.async = true;
            return this;
        }

        /**
         * The broadcast assertions define date-time ranges for the provided channels and they are
         * an assertion that if any broadcasts exist for the item being written in the given ranges
         * then these broadcasts will have been provided in the given item.
         * <p>
         * Effectively this means that if the given item is merged with an existing one while it is
         * being written then any existing broadcasts that fall in these ranges will be deleted and
         * replaced with the broadcasts of the written item.
         * <p>
         * If assertions are provided, then any broadcasts in the given item <b>must</b> be fully
         * contained by one of those assertions.
         * <p>
         * If no broadcast assertions are provided the default behaviour is to delete all existing
         * broadcasts and replace them with the ones provided.
         * <p>
         * This option is mutually exclusive with `async` and `overwriteExisting`.
         */
        public Builder withBroadcastAssertions(BroadcastAssertions broadcastAssertions) {
            this.broadcastAssertions = Optional.of(broadcastAssertions);
            return this;
        }

        public ContentWriteOptions build() {
            return new ContentWriteOptions(
                    this.overwriteExisting,
                    this.async,
                    this.broadcastAssertions
            );
        }
    }
}
