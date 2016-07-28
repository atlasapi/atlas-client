package org.atlasapi.client;

import org.atlasapi.client.response.ContentResponse;
import org.atlasapi.media.entity.simple.Item;
import org.atlasapi.media.entity.simple.Person;


public interface AtlasWriteClient {
    
    void writePerson(Person person);
    
    void updatePerson(Person person);

    @Deprecated
    String writeItem(Item item);

    @Deprecated
    String writeItemAsync(Item item);

    ContentResponse writeItemWithResponse(Item item);

    ContentResponse writeItemWithResponseAsync(Item item);

    @Deprecated
    void writeItemOverwriteExisting(Item item);

    @Deprecated
    void writeItemOverwriteExistingAsync(Item item);

    ContentResponse writeItemOverwriteExistingWithResponse(Item item);

    ContentResponse writeItemOverwriteExistingAsyncWithResponse(Item item);
}
