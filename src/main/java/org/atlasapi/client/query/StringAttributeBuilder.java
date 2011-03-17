package org.atlasapi.client.query;

import org.atlasapi.content.criteria.attribute.Attribute;
import org.atlasapi.content.criteria.operator.Operators;

import com.google.common.collect.ImmutableList;

public final class StringAttributeBuilder {

	private final Attribute<String> attribute;
	private final AtlasQuery chain;

	StringAttributeBuilder(AtlasQuery chain, Attribute<String> attribute) {
		this.chain = chain;
		this.attribute = attribute;
	}
	
	public AtlasQuery equalTo(String... values) {
		return chain.add(attribute.createQuery(Operators.EQUALS, ImmutableList.copyOf(values)));
	}
	
	public AtlasQuery in(Iterable<String> values) {
		return chain.add(attribute.createQuery(Operators.EQUALS, ImmutableList.copyOf(values)));
	}
}
