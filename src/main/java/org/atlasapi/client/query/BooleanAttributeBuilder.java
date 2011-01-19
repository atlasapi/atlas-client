package org.atlasapi.client.query;

import org.atlasapi.content.criteria.attribute.Attribute;
import org.atlasapi.content.criteria.operator.Operators;

import com.google.common.collect.ImmutableList;

public final class BooleanAttributeBuilder {

	private final Attribute<Boolean> attribute;
	private final AtlasQuery chain;

	BooleanAttributeBuilder(AtlasQuery chain, Attribute<Boolean> attribute) {
		this.chain = chain;
		this.attribute = attribute;
	}
	
	public AtlasQuery isTrue() {
		return chain.add(attribute.createQuery(Operators.EQUALS, ImmutableList.of(true)));
	}
	
	public AtlasQuery isFalse() {
		return chain.add(attribute.createQuery(Operators.EQUALS, ImmutableList.of(false)));
	
	}
	public AtlasQuery isAnything() {
		return chain.add(attribute.createQuery(Operators.EQUALS, ImmutableList.of(true, false)));
	}
}
