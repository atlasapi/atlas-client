package org.atlasapi.client;

import java.util.List;

import org.atlasapi.client.query.AtlasQuery;
import org.atlasapi.client.response.ContentResponse;
import org.atlasapi.media.entity.simple.ContentGroupQueryResult;
import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Description;
import org.atlasapi.media.entity.simple.DiscoverQueryResult;
import org.atlasapi.media.entity.simple.EventQueryResult;
import org.atlasapi.media.entity.simple.Item;
import org.atlasapi.media.entity.simple.PeopleQueryResult;
import org.atlasapi.media.entity.simple.Person;
import org.atlasapi.media.entity.simple.Playlist;
import org.atlasapi.media.entity.simple.ScheduleQueryResult;

import com.metabroadcast.common.url.QueryStringParameters;
import com.metabroadcast.common.url.UrlEncoding;
import com.metabroadcast.common.url.Urls;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.net.HostSpecifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public class StrictGsonAtlasClient implements AtlasClient, AtlasWriteClient {

    private final QueryStringBuilder queryStringBuilder = new QueryStringBuilder();
    private final GsonQueryClient client = new GsonQueryClient();
    private final Joiner joiner = Joiner.on(",");
    private final String baseUri;
    private final Optional<String> apiKey;
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public StrictGsonAtlasClient(HostSpecifier atlasHost, Optional<String> apiKey) {
        this.apiKey = apiKey;
        this.baseUri = String.format("http://%s/3.0", atlasHost);
    }

    @Deprecated
    public StrictGsonAtlasClient(String baseUri, String apiKey) {
        this.baseUri = baseUri;
        this.apiKey = Optional.fromNullable(apiKey);
        this.queryStringBuilder.setApiKey(apiKey);
    }

    @Override
    public ContentQueryResult content(Iterable<String> ids) {
        return client.contentQuery(baseUri + "/content.json?uri=" +  joiner.join(UrlEncoding.encode(ids)) + apiKeyQueryPart());
    }

    @Override
    public ContentQueryResult content(ContentQuery query) {
        return client.contentQuery(baseUri + "/content.json?" + withApiKey(query.toQueryStringParameters()).toQueryString());
    }

    @Override
    public EventQueryResult event(EventQuery query) {
        return client.eventQuery(baseUri + "/events.json?" + withApiKey(query.toQueryStringParameters()).toQueryString());
    }

    @Override
    @Deprecated
    public ContentGroupQueryResult contentGroup(String id) {
        return client.contentGroupQuery(baseUri + "/content_groups/" + id + ".json?" + apiKeyQueryPart());
    }

    @Override
    @Deprecated
    public ContentGroupQueryResult contentGroups() {
        return client.contentGroupQuery(baseUri + "/content_groups.json?" +  apiKeyQueryPart());
    }

    private String apiKeyQueryPart() {
        if (apiKey.isPresent()) {
            return "&apiKey=" + apiKey.get();
        }
        return "";
    }

    public QueryStringParameters withApiKey(QueryStringParameters parameters) {
        if (apiKey.isPresent()) {
            parameters.add("apiKey", apiKey.get());
        }
        return parameters;
    }

    @Override
    public DiscoverQueryResult discover(AtlasQuery query) {
        List<Description> contents = client.contentQuery(baseUri + "/discover.json?" + queryStringBuilder.build(query.build())).getContents();
        return new DiscoverQueryResult(contents);
    }

    @Override
    public ScheduleQueryResult scheduleFor(ScheduleQuery query) {
        QueryStringParameters params = query.toParams();
        if (apiKey.isPresent()) {
            params.add("apiKey", apiKey.get());
        }
        return client.scheduleQuery(baseUri + "/schedule.json?" + params.toQueryString());
    }

    @Override
    public ContentQueryResult search(SearchQuery query) {
        QueryStringParameters params = query.toParams();
        if (apiKey.isPresent()) {
            params.add("apiKey", apiKey.get());
        }
        return client.contentQuery(baseUri + "/search.json?" + params.toQueryString());
    }

    @Override
    public PeopleQueryResult people(Iterable<String> uris) {

        return client.peopleQuery(baseUri + "/people.json?uri=" + joiner.join(UrlEncoding.encode(uris)) + apiKeyQueryPart());
    }

    @Override
    public PeopleQueryResult people(PeopleQuery query) {
        return client.peopleQuery(baseUri + "/people.json?" + withApiKey(query.toQueryStringParameters()).toQueryString());
    }

    @Override
    public void writePerson(Person person) {
        checkNotNull(person.getUri(), "Cannot write Person without URI");
        checkNotNull(person.getPublisher(), "Cannot write Person without Publisher");
        client.postPerson(personResourceUri(), person);
    }

    @Override
    public void updatePerson(Person person) {
        checkNotNull(person.getUri(), "Cannot update Person without URI");
        checkNotNull(person.getPublisher(), "Cannot update Person without Publisher");
        client.putPerson(personResourceUri(), person);
    }

    @Override
    public String writeItem(Item item) {
        return writeItem(item, false);
    }

    @Override
    public ContentResponse writeItemWithResponse(Item item) {
        return writeItemWithResponse(item, false);

    }

    @Override
    public ContentResponse writeItemWithResponseAsync(Item item) {
        return writeItemWithResponse(item, true);
    }

    @Override
    public String writeItemAsync(Item item) {
        return writeItem(item, true);
    }

    @Override
    public void writeItemOverwriteExisting(Item item) {
        writeItemOverwriteExisting(item, false);
    }

    @Override
    public void writeItemOverwriteExistingAsync(Item item) {
        writeItemOverwriteExisting(item, true);
    }

    @Override
    public ContentResponse writeItemOverwriteExistingWithResponse(Item item) {
        return writeItemOverwriteExistingWithResponse(item, false);
    }

    @Override
    public ContentResponse writeItemOverwriteExistingAsyncWithResponse(Item item) {
        return writeItemOverwriteExistingWithResponse(item, true);
    }

    @Override
    public ContentResponse writePlayListWithResponse(Playlist playlist) {
        return writePlayListWithResponse(playlist, false);
    }

    @Override
    public ContentResponse writePlayListOverwriteExistingWithResponse(Playlist playlist) {
        return writePlayListOverwriteExistingWithResponse(playlist, false);
    }

    private ContentResponse writePlayListWithResponse(Playlist playlist, boolean async) {
        validatePlayList(playlist);
        return client.postPlayListWithResponse(writeItemUri(async), playlist);
    }

    private ContentResponse writePlayListOverwriteExistingWithResponse(Playlist playlist, boolean async) {
        validatePlayList(playlist);
        return client.putPlayListWithResponse(writeItemUri(async), playlist);
    }


    private ContentResponse writeItemWithResponse(Item item, boolean async) {
        validateItem(item);
        return client.postItemWithResponse(writeItemUri(async), item);
    }

    private String writeItem(Item item, boolean async) {
        validateItem(item);
        return client.postItem(writeItemUri(async), item);
    }

    private void writeItemOverwriteExisting(Item item, boolean async) {
        validateItem(item);
        client.putItem(writeItemUri(async), item);
    }

    private ContentResponse writeItemOverwriteExistingWithResponse(Item item, boolean async) {
        validateItem(item);
        return client.putItemWithResponse(writeItemUri(async), item);
    }

    private String personResourceUri() {
        String queryString = baseUri + "/people.json?";
        if (apiKey.isPresent()) {
            queryString = Urls.appendParameters(queryString, "apiKey", apiKey.get());
        }
        return queryString;
    }

    private String writeItemUri(boolean async) {
        checkNotNull(apiKey.get(), "An API key must be specified for content write queries");
        StringBuilder uriBuilder = new StringBuilder(baseUri)
                .append("/content.json?")
                .append(apiKeyQueryPart());

        if (async) {
            uriBuilder.append("&async=true");
        }
            uriBuilder.append("&strict=true");

        return uriBuilder.toString();
    }

    private void validateItem(Item item) {
        checkNotNull(item, "Cannot write a null item");
        checkNotNull(item.getPublisher(), "Cannot write an Item without a Publisher");
        checkNotNull(Strings.emptyToNull(item.getUri()), "Cannot write an Item without a URI");
        checkNotNull(Strings.emptyToNull(item.getType()), "Cannot write an Item without a type");

    }

    private void validatePlayList(Playlist playlist) {
        checkNotNull(playlist, "Cannot write a null item");
        checkNotNull(playlist.getPublisher(), "Cannot write an Item without a Publisher");
        checkNotNull(Strings.emptyToNull(playlist.getUri()), "Cannot write an Item without a URI");
        checkNotNull(Strings.emptyToNull(playlist.getType()), "Cannot write an Item without a type");
    }
}
