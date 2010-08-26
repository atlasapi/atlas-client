package org.atlasapi.client.query;

import org.atlasapi.content.criteria.attribute.Attribute;
import org.atlasapi.content.criteria.operator.Operators;

import com.google.common.collect.ImmutableList;

public final class StringAttributeBuilder<T> {

	private final Attribute<String> attribute;
	private final AtlasQuery<T> chain;

	StringAttributeBuilder(AtlasQuery<T> chain, Attribute<String> attribute) {
		this.chain = chain;
		this.attribute = attribute;
	}
	
	public AtlasQuery<T> equalTo(String... values) {
		return chain.add(attribute.createQuery(Operators.EQUALS, ImmutableList.copyOf(values)));
	}
	
	public AtlasQuery<T> in(Iterable<String> values) {
		return chain.add(attribute.createQuery(Operators.EQUALS, ImmutableList.copyOf(values)));
	}
	
	public AtlasQuery<T> search(String... values) {
		return chain.add(attribute.createQuery(Operators.SEARCH, ImmutableList.copyOf(values)));
	}
}
