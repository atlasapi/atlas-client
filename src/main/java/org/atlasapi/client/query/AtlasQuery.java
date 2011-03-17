package org.atlasapi.client.query;

import org.atlasapi.content.criteria.AtomicQuery;
import org.atlasapi.content.criteria.ContentQuery;
import org.atlasapi.content.criteria.attribute.Attributes;
import org.atlasapi.media.entity.Publisher;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.metabroadcast.common.query.Selection;

public class AtlasQuery {

	final Selection selection;
	final ImmutableList<AtomicQuery> conjuncts;

	public static AtlasQuery filter() {
		return new AtlasQuery(ImmutableList.<AtomicQuery>of(), Selection.ALL);
	}
	
	AtlasQuery(ImmutableList<AtomicQuery> conjuncts, Selection selection) {
		this.conjuncts = conjuncts;
		this.selection = selection;
	}

	public StringAttributeBuilder channel() {
		return new StringAttributeBuilder(this, Attributes.BROADCAST_ON);
	}
	
	public StringAttributeBuilder title() {
		return new StringAttributeBuilder(this, Attributes.DESCRIPTION_TITLE);
	}
	
	public StringAttributeBuilder genres() {
		return new StringAttributeBuilder(this, Attributes.DESCRIPTION_GENRE);
	}
	
	public BooleanAttributeBuilder longForm() {
		return new BooleanAttributeBuilder(this, Attributes.ITEM_IS_LONG_FORM);
	}
	
	public BooleanAttributeBuilder available() {
		return new BooleanAttributeBuilder(this, Attributes.LOCATION_AVAILABLE);
	}
	
	public EnumAttributeBuilder<Publisher> publisher() {
		return new EnumAttributeBuilder<Publisher>(this, Attributes.DESCRIPTION_PUBLISHER);
	}
	
	public AtlasQuery withSelection(Selection selection) {
		return copyWith(conjuncts, selection);
	}
	
	AtlasQuery add(AtomicQuery conjunct) {
		return copyWith(ImmutableList.copyOf(Iterables.concat(conjuncts, ImmutableList.of(conjunct))), selection);
	}
	
	private AtlasQuery copyWith(ImmutableList<AtomicQuery> conjuncts, Selection selection) {
		return new AtlasQuery(conjuncts, selection);
	}

	public DateTimeAttributeBuilder transmissionTime() {
		return new DateTimeAttributeBuilder(this, Attributes.BROADCAST_TRANSMISSION_TIME);
	}
	
	public ContentQuery build() {
		return new ContentQuery(conjuncts, selection);
	}
}
