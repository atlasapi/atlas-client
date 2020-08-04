package org.atlasapi.client.query;

import javax.annotation.Nullable;

public class ChannelGroupWriteOptions {

    private final boolean overwriteExisting;
    private final String idFormat;

    private ChannelGroupWriteOptions(Builder builder) {
        overwriteExisting = builder.overwriteExisting;
        idFormat = builder.idFormat;
    }

    public boolean isOverwriteExisting() {
        return overwriteExisting;
    }

    @Nullable
    public String getIdFormat() {
        return idFormat;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private boolean overwriteExisting;
        private String idFormat;

        private Builder() {
        }

        public Builder withOverwriteExisting(boolean overwriteExisting) {
            this.overwriteExisting = overwriteExisting;
            return this;
        }

        /**
         *  Used by the Owl API to determine the type of IDs of the channels within the channelGroup
         *
         *  Can be set to "owl" or "deer". Optional.
         */
        public Builder withIdFormat(String idFormat) {
            this.idFormat = idFormat;
            return this;
        }

        public ChannelGroupWriteOptions build() {
            return new ChannelGroupWriteOptions(this);
        }
    }
}
