package org.atlasapi.client.query;

import java.util.List;

import org.atlasapi.content.criteria.AtomicQuery;
import org.atlasapi.content.criteria.attribute.Attribute;
import org.atlasapi.content.criteria.attribute.Attributes;
import org.atlasapi.media.entity.Publisher;
import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Item;

import com.google.common.collect.ImmutableList;
import com.metabroadcast.common.query.Selection;

class ItemQuery extends AtlasQuery<Item> {

	public ItemQuery(ImmutableList<AtomicQuery> conjuncts, Selection selection) {
		super(conjuncts, selection);
	}

	@Override
	public List<Item> extractFrom(ContentQueryResult result) {
		return result.getItems();
	}

	@Override
	public String urlPrefix() {
		return "items";
	}
	
	@Override
	protected Attribute<String> uriAtttribute() {
		return Attributes.ITEM_URI;
	}

	@Override
	protected AtlasQuery<Item> copyWith(ImmutableList<AtomicQuery> conjuncts, Selection selection) {
		return new ItemQuery(conjuncts, selection);
	}

	@Override
	protected Attribute<String> titleAttribute() {
		return Attributes.ITEM_TITLE;
	}

    @Override
    Attribute<Enum<Publisher>> publisherAttribute() {
        return Attributes.ITEM_PUBLISHER;
    }
}
