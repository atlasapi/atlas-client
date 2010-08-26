package org.atlasapi.client.query;

import org.atlasapi.content.criteria.attribute.Attribute;
import org.atlasapi.content.criteria.operator.Operators;

import com.google.common.collect.ImmutableList;

public final class BooleanAttributeBuilder<T> {

	private final Attribute<Boolean> attribute;
	private final AtlasQuery<T> chain;

	BooleanAttributeBuilder(AtlasQuery<T> chain, Attribute<Boolean> attribute) {
		this.chain = chain;
		this.attribute = attribute;
	}
	
	public AtlasQuery<T> isTrue() {
		return chain.add(attribute.createQuery(Operators.EQUALS, ImmutableList.of(true)));
	}
	
	public AtlasQuery<T> isFalse() {
		return chain.add(attribute.createQuery(Operators.EQUALS, ImmutableList.of(false)));
	
	}
	public AtlasQuery<T> isAnything() {
		return chain.add(attribute.createQuery(Operators.EQUALS, ImmutableList.of(true, false)));
	}
}
