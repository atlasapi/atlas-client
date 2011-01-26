package org.atlasapi.client.query;

import java.util.List;

import org.atlasapi.content.criteria.AtomicQuery;
import org.atlasapi.content.criteria.attribute.Attribute;
import org.atlasapi.content.criteria.attribute.Attributes;
import org.atlasapi.media.entity.Publisher;
import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Playlist;

import com.google.common.collect.ImmutableList;
import com.metabroadcast.common.query.Selection;


public class PlaylistQuery extends AtlasQuery<Playlist> {
    public PlaylistQuery(ImmutableList<AtomicQuery> conjuncts, Selection selection) {
        super(conjuncts, selection);
    }

    @Override
    public List<Playlist> extractFrom(ContentQueryResult result) {
        return result.getPlaylists();
    }

    @Override
    public String urlPrefix() {
        return "playlists";
    }

    @Override
    protected Attribute<String> uriAtttribute() {
        return Attributes.PLAYLIST_URI;
    }

    @Override
    protected Attribute<String> titleAttribute() {
        return Attributes.PLAYLIST_TITLE;
    }

    @Override
    protected AtlasQuery<Playlist> copyWith(ImmutableList<AtomicQuery> conjuncts, Selection selection) {
        return new PlaylistQuery(conjuncts, selection);
    }

    @Override
    Attribute<Enum<Publisher>> publisherAttribute() {
        return Attributes.PLAYLIST_PUBLISHER;
    }
}
