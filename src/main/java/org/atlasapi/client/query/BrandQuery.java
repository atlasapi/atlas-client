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

class BrandQuery extends AtlasQuery<Playlist> {

	public BrandQuery(ImmutableList<AtomicQuery> conjuncts, Selection selection) {
		super(conjuncts, selection);
	}

	@Override
	public List<Playlist> extractFrom(ContentQueryResult result) {
		return result.getPlaylists();
	}

	@Override
	public String urlPrefix() {
		return "brands";
	}

	@Override
	protected Attribute<String> uriAtttribute() {
		return Attributes.BRAND_URI;
	}

	@Override
	protected Attribute<String> titleAttribute() {
		return Attributes.BRAND_TITLE;
	}

	@Override
	protected AtlasQuery<Playlist> copyWith(ImmutableList<AtomicQuery> conjuncts, Selection selection) {
		return new BrandQuery(conjuncts, selection);
	}

    @Override
    Attribute<Enum<Publisher>> publisherAttribute() {
        return Attributes.BRAND_PUBLISHER;
    }
}
