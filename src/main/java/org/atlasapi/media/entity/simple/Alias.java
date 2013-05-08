package org.atlasapi.media.entity.simple;


public class Alias {
    private String namespace;
    private String value;
    
    public Alias() {    
    }
    
    public Alias(String namespace, String value) {
        this.namespace = namespace;
        this.value = value;
    }
    
    public String getNamespace() {
        return namespace;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
}
