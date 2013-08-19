package org.atlasapi.client;

import org.atlasapi.media.entity.simple.Person;


public interface AtlasWriteClient {
    
    void writePerson(Person person);
    
}
