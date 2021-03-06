Atlas Java Client Library
=========================

This is an attempt to fully document the "Atlas":http://docs.atlasapi.org Java Client.  The client is still in development, so please bear with us while the API evolves.

## Getting started with Maven 2

Use maven, add the following to your dependencies:

    <dependency>
       <groupId>org.atlasapi</groupId>
        <artifactId>atlas-client</artifactId>
        <version>5.0-SNAPSHOT</version>
    </dependency>

The artifacts can be found in the MetaBroadcast public repository, add it to your project using the following snippet:

    <repositories>
      <repository>
        <id>metabroadcast-mvn</id>
        <name>MetaBroadcast</name>
        <url>http://mvn.metabroadcast.com/all</url>
        <layout>default</layout>
      </repository>
    </repositories>

## Using the client

Create yourself a client like so:

    import org.atlasapi.client.CachingJaxbAtlasClient;
    import org.atlasapi.client.AtlasClient;

    AtlasClient client = new CachingJaxbAtlasClient();

The client is thread safe and can be safely shared.  To use the client first build a query using the ContentQueryBuilder.  Static imports make this process easy:

    import static org.atlasapi.content.criteria.ContentQueryBuilder.query;
    import static org.atlasapi.content.criteria.attribute.Attributes.*;

Next, build a query using the query() method:

    ContentQuery queryForNewsnight = query().equalTo(BRAND_TITLE, "newsnight").build();

Finally execute the query using the client:

    List<Playlist> matchingNewsnight = client.brands(queryForNewsnight);

You can use the query() method to build more complicated queries, the following query finds the programme being currently being shown on the BBC News channel:

    ContentQuery currentlyOnBbcNews = query()
      .equalTo(BROADCAST_TRANSMISSION_TIME, new DateTime())
      .equalTo(BROADCAST_ON, "http://www.bbc.co.uk/services/bbcnews")
    .build();






