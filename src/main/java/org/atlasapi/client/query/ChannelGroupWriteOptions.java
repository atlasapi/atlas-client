package org.atlasapi.client.query;

public class ChannelGroupWriteOptions {

    private final boolean overwriteExisting;

    private ChannelGroupWriteOptions(Builder builder) {
        overwriteExisting = builder.overwriteExisting;
    }

    public boolean isOverwriteExisting() {
        return overwriteExisting;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private boolean overwriteExisting;

        private Builder() {
        }

        public Builder withOverwriteExisting(boolean overwriteExisting) {
            this.overwriteExisting = overwriteExisting;
            return this;
        }

        public ChannelGroupWriteOptions build() {
            return new ChannelGroupWriteOptions(this);
        }
    }
}
