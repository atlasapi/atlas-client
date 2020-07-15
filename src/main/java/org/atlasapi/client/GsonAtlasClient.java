package org.atlasapi.client;

import java.net.URISyntaxException;
import java.util.List;

import org.atlasapi.client.query.AtlasQuery;
import org.atlasapi.client.query.ContentWriteOptions;
import org.atlasapi.client.response.ChannelGroupResponse;
import org.atlasapi.client.response.ChannelResponse;
import org.atlasapi.client.response.ContentResponse;
import org.atlasapi.client.response.TopicUpdateResponse;
import org.atlasapi.media.entity.simple.Channel;
import org.atlasapi.media.entity.simple.ChannelGroup;
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
import org.atlasapi.media.entity.simple.Topic;
import org.atlasapi.media.entity.simple.TopicQueryResult;

import com.metabroadcast.common.url.QueryStringParameters;
import com.metabroadcast.common.url.UrlEncoding;
import com.metabroadcast.common.url.Urls;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.net.HostSpecifier;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public class GsonAtlasClient implements AtlasClient, AtlasWriteClient {
    
    private final QueryStringBuilder queryStringBuilder = new QueryStringBuilder();
    private final GsonQueryClient client = new GsonQueryClient();
    private final Joiner joiner = Joiner.on(",");
    private final String baseUri;
    private final Optional<String> apiKey;
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private final String URI = "uri";
    private final String ID = "id";

    public GsonAtlasClient(HostSpecifier atlasHost, Optional<String> apiKey) {
        this.apiKey = apiKey;
        this.baseUri = String.format("http://%s/3.0", atlasHost);
    }

    //

    /**
     * Use this constructor with internal hostnames as you currently can't create a HostSpecifier
     * object unless the domain used has a public suffix.
     */
    public GsonAtlasClient(String atlasHost, Optional<String> apiKey) {
        this.baseUri = String.format("http://%s/3.0", atlasHost);
        this.apiKey = apiKey;

        if (apiKey.isPresent()) {
            this.queryStringBuilder.setApiKey(apiKey.get());
        }
    }

    /**
     * Deprecated use {@link #GsonAtlasClient(String, Optional<String>)} instead.
     */
    @Deprecated
    public GsonAtlasClient(String baseUri, String apiKey) {
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

    public TopicQueryResult topic(String id) {
        return client.topicQuery(baseUri + "/topics/" + id + ".json?" + apiKeyQueryPart());
    }

    public TopicQueryResult topic(TopicQuery query) {
        return client.topicQuery(baseUri + "/topics.json?" + withApiKey(query.toQueryStringParameters()).toQueryString());
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
    public ContentResponse writeItem(Item item, ContentWriteOptions options) {
        validateItem(item);

        if (options.isOverwriteExisting()) {
            return client.putItem(writeItemUri(options), item);
        } else {
            return client.postItem(writeItemUri(options), item);
        }
    }

    @Override
    public ContentResponse writePlaylist(Playlist playlist, ContentWriteOptions options) {
        validatePlayList(playlist);

        if (options.isOverwriteExisting()) {
            return client.putPlaylist(writeItemUri(options), playlist);
        } else {
            return client.postPlaylist(writeItemUri(options), playlist);
        }
    }

    @Override
    public ChannelGroupResponse writeChannelGroup(ChannelGroup channelGroup, boolean overwriteExisting) {
        validateChannelGroup(channelGroup);

        if(overwriteExisting) {
            return client.putChannelGroup(writeChannelGroupUri(), channelGroup);
        } else {
            return client.postChannelGroup(writeChannelGroupUri(), channelGroup);
        }
    }

    @Override
    public ChannelResponse writeChannel(Channel channel, boolean overwriteExisting) {
        validateChannel(channel);

        if(overwriteExisting) {
            return client.putChannel(writeChannelUri(), channel);
        } else {
            return client.postChannel(writeChannelUri(), channel);
        }
    }

    @Override
    public String writeItem(Item item) {
        return writeItem(
                item,
                ContentWriteOptions.builder()
                        .build()
        )
                .getLocation();
    }

    @Override
    public String writeItemAsync(Item item) {
        return writeItem(
                item,
                ContentWriteOptions.builder()
                        .withAsync()
                        .build()
        )
                .getLocation();
    }

    @Override
    public ContentResponse writeItemWithResponse(Item item) {
        return writeItem(
                item,
                ContentWriteOptions.builder()
                        .build()
        );
    }

    @Override
    public ContentResponse writeItemWithResponseAsync(Item item) {
        return writeItem(
                item,
                ContentWriteOptions.builder()
                        .withAsync()
                        .build()
        );
    }

    @Override
    public void writeItemOverwriteExisting(Item item) {
        writeItem(
                item,
                ContentWriteOptions.builder()
                        .withOverwriteExisting()
                        .build()
        );
    }

    @Override
    public void writeItemOverwriteExistingAsync(Item item) {
        writeItem(
                item,
                ContentWriteOptions.builder()
                        .withAsync()
                        .withOverwriteExisting()
                        .build()
        );
    }

    @Override
    public ContentResponse writeItemOverwriteExistingWithResponse(Item item) {
        return writeItem(
                item,
                ContentWriteOptions.builder()
                        .withOverwriteExisting()
                        .build()
        );
    }

    @Override
    public ContentResponse writeItemOverwriteExistingAsyncWithResponse(Item item) {
        return writeItem(
                item,
                ContentWriteOptions.builder()
                        .withAsync()
                        .withOverwriteExisting()
                        .build()
        );
    }

    @Override
    public ContentResponse writePlayListWithResponse(Playlist playlist) {
        return writePlaylist(
                playlist,
                ContentWriteOptions.builder()
                        .build()
        );
    }

    @Override
    public ContentResponse writePlayListOverwriteExistingWithResponse(Playlist playlist) {
        return writePlaylist(
                playlist,
                ContentWriteOptions.builder()
                        .withOverwriteExisting()
                        .build()
        );
    }

    @Override
    public TopicUpdateResponse writeTopicWithResponse(Topic topic) {
        checkNotNull(apiKey.get(), "An API key must be specified for topic write queries");
        validateTopic(topic);
        String uri = baseUri + "/topics.json?uri=" + UrlEncoding.encode(topic.getUri()) + apiKeyQueryPart();
        return client.postTopic(uri, topic);
    }

    @Override
    public void unpublishContentById(String id) {
        unpublishContent(ID, id);
    }

    @Override
    public void unpublishContentByUri(String uri) {
        unpublishContent(URI, uri);
    }

    private String personResourceUri() {
        String queryString = baseUri + "/people.json?";
        if (apiKey.isPresent()) {
            queryString = Urls.appendParameters(queryString, "apiKey", apiKey.get());
        }
        return queryString;
    }

    private String writeChannelGroupUri() {
        checkNotNull(apiKey.get(), "An API key must be specified for write queries");
        String queryString = baseUri + "/channel_groups.*";
        return queryString;
    }

    private String writeChannelUri() {
        checkNotNull(apiKey.get(), "An API key must be specified for write queries");
        String queryString = baseUri + "/channel_groups.*";
        return queryString;
    }

    private String writeItemUri(ContentWriteOptions options) {
        checkNotNull(apiKey.get(), "An API key must be specified for content write queries");

        try {
            URIBuilder uriBuilder = new URIBuilder(baseUri + "/content.json")
                    .addParameter("apiKey", apiKey.get());

            if (options.isAsync()) {
                uriBuilder.addParameter("async", "true");
            }
            if (options.getBroadcastAssertions().isPresent()) {
                uriBuilder.addParameter(
                        "broadcastAssertions",
                        options.getBroadcastAssertions().get().toString()
                );
            }

            return uriBuilder.build().toString();
        } catch (URISyntaxException e) {
            throw Throwables.propagate(e);
        }
    }

    private void unpublishContent(String identifierParam, String id) {
        client.unpublishContent(
                unpublishContentUri(identifierParam, id)
        );
    }

    private String unpublishContentUri(String identifierParam, String identifier) {
        checkNotNull(apiKey.get(), "An API key must be specified for content unpublish queries");
        return new StringBuilder(baseUri)
                .append("/content.json?")
                .append(apiKeyQueryPart())
                .append(identifierParam.equals(URI) ? "&uri=" : "&id=")
                .append(identifier)
                .toString();

    }

    private void validateItem(Item item) {
        checkNotNull(item, "Cannot write a null item");
        checkNotNull(item.getPublisher(), "Cannot write an Item without a Publisher");
        checkNotNull(Strings.emptyToNull(item.getUri()), "Cannot write an Item without a URI");
        checkNotNull(Strings.emptyToNull(item.getType()), "Cannot write an Item without a type");

    }

    private void validatePlayList(Playlist playlist) {
        checkNotNull(playlist, "Cannot write a null playlist");
        checkNotNull(playlist.getPublisher(), "Cannot write a Playlist without a Publisher");
        checkNotNull(Strings.emptyToNull(playlist.getUri()), "Cannot write a Playlist without a URI");
        checkNotNull(Strings.emptyToNull(playlist.getType()), "Cannot write a Playlist without a type");
    }

    private void validateTopic(Topic topic) {
        checkNotNull(topic, "Cannot write a null topic");
        checkNotNull(topic.getPublisher(), "Cannot write a Topic without a Publisher");
        checkNotNull(Strings.emptyToNull(topic.getUri()), "Cannot write a Topic without a URI");
        checkNotNull(Strings.emptyToNull(topic.getType()), "Cannot write a Topic without a type");

    }

    private void validateChannelGroup(ChannelGroup channelGroup) {
        checkNotNull(channelGroup, "Cannot write a null ChannelGroup");
        checkNotNull(channelGroup.getPublisherDetails(), "Cannot write a ChannelGroup without a publisher");
        checkNotNull(Strings.emptyToNull(channelGroup.getUri()), "Cannot write a ChannelGroup without a URI");
        checkNotNull(Strings.emptyToNull(channelGroup.getType()), "Cannot write a ChannelGroup without a type");
    }

    private void validateChannel(Channel channel) {
        checkNotNull(channel, "Cannot write a null channel");
        checkNotNull(channel.getPublisherDetails(), "Cannot write a Channel without a publisher");
        checkNotNull(Strings.emptyToNull(channel.getUri()), "Cannot write a Channel without a URI");
        checkNotNull(Strings.emptyToNull(channel.getType()), "Cannot write a Channel without a type");
    }
}
