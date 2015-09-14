package org.atlasapi.client;

import org.atlasapi.media.entity.simple.Item;
import org.atlasapi.media.entity.simple.Person;


public interface AtlasWriteClient {
    
    void writePerson(Person person);
    
    void updatePerson(Person person);

    void writeItem(Item item);

    void writeItemOverwriteExisting(Item item);
}
