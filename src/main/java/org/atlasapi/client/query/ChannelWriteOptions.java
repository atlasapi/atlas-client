package org.atlasapi.client.query;

public class ChannelWriteOptions {

    private final boolean overwriteExisting;

    private ChannelWriteOptions(Builder builder) {
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

        public ChannelWriteOptions build() {
            return new ChannelWriteOptions(this);
        }
    }
}
