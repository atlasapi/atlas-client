package org.atlasapi.client;

import org.atlasapi.media.entity.simple.Item;
import org.atlasapi.media.entity.simple.Person;


public interface AtlasWriteClient {
    
    void writePerson(Person person);
    
    void updatePerson(Person person);

    String writeItem(Item item);

    String writeItemAsync(Item item);

    void writeItemOverwriteExisting(Item item);

    void writeItemOverwriteExistingAsync(Item item);
}
