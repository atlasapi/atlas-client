package org.atlasapi.client.query;

import java.util.List;

import org.atlasapi.content.criteria.AtomicQuery;
import org.atlasapi.content.criteria.ContentQuery;
import org.atlasapi.content.criteria.attribute.Attribute;
import org.atlasapi.content.criteria.attribute.Attributes;
import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Item;
import org.atlasapi.media.entity.simple.Playlist;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.metabroadcast.common.query.Selection;

public abstract class AtlasQuery<T> {

	final Selection selection;
	final ImmutableList<AtomicQuery> conjuncts;

	public static AtlasQuery<Playlist> brands() {
		return new BrandQuery(ImmutableList.<AtomicQuery>of(), Selection.ALL);
	}
	
	public static AtlasQuery<Item> items() {
		return new ItemQuery(ImmutableList.<AtomicQuery>of(), Selection.ALL);
	}
	
	public static AtlasQuery<Playlist> playlists() {
	    return new PlaylistQuery(ImmutableList.<AtomicQuery>of(), Selection.ALL);
	}
	
	AtlasQuery(ImmutableList<AtomicQuery> conjuncts, Selection selection) {
		this.conjuncts = conjuncts;
		this.selection = selection;
	}
	
	public final StringAttributeBuilder<T> uri() {
		return new StringAttributeBuilder<T>(this, uriAtttribute());
	}

	public StringAttributeBuilder<T> playlistUri() {
		return new StringAttributeBuilder<T>(this, Attributes.PLAYLIST_URI);
	}
	
	public StringAttributeBuilder<T> channel() {
		return new StringAttributeBuilder<T>(this, Attributes.BROADCAST_ON);
	}
	
	public StringAttributeBuilder<T> title() {
		return new StringAttributeBuilder<T>(this, titleAttribute());
	}
	
	public StringAttributeBuilder<T> itemGenres() {
		return new StringAttributeBuilder<T>(this, Attributes.ITEM_GENRE);
	}
	
	public BooleanAttributeBuilder<T> longForm() {
		return new BooleanAttributeBuilder<T>(this, Attributes.ITEM_IS_LONG_FORM);
	}
	
	public BooleanAttributeBuilder<T> available() {
		return new BooleanAttributeBuilder<T>(this, Attributes.LOCATION_AVAILABLE);
	}
	
	protected abstract Attribute<String> titleAttribute();

	abstract Attribute<String> uriAtttribute();

	public AtlasQuery<T> withSelection(Selection selection) {
		return copyWith(conjuncts, selection);
	}
	
	AtlasQuery<T> add(AtomicQuery conjunct) {
		return copyWith(ImmutableList.copyOf(Iterables.concat(conjuncts, ImmutableList.of(conjunct))), selection);
	}
	
	protected abstract AtlasQuery<T> copyWith(ImmutableList<AtomicQuery> conjuncts, Selection selection);

	public abstract List<T> extractFrom(ContentQueryResult result);

	public DateTimeAttributeBuilder<T> transmissionTime() {
		return new DateTimeAttributeBuilder<T>(this, Attributes.BROADCAST_TRANSMISSION_TIME);
	}
	
	public ContentQuery build() {
		return new ContentQuery(conjuncts, selection);
	}

	public abstract String urlPrefix();
}
