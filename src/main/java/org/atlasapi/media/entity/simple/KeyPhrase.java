package org.atlasapi.media.entity.simple;

public class KeyPhrase {

    private String phrase;
    @Deprecated
    private SourceDetails source;
    private Double weighting;
    
    public KeyPhrase() { }

    public KeyPhrase(String phrase, SourceDetails publisherDetails, Double weighting) {
        this.phrase = phrase;
        this.source = publisherDetails;
        this.weighting = weighting;
    }

    public String getPhrase() {
        return this.phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public SourceDetails getSource() {
        return this.source;
    }

    public void setSource(SourceDetails publisher) {
        this.source = publisher;
    }

    public Double getWeighting() {
        return this.weighting;
    }

    public void setWeighting(Double weighting) {
        this.weighting = weighting;
    }
    
}
