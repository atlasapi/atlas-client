package org.atlasapi.client.query;

import org.atlasapi.content.criteria.attribute.Attribute;
import org.atlasapi.content.criteria.operator.Operators;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.google.common.collect.ImmutableList;

public class DateTimeAttributeBuilder<T> {

	private final AtlasQuery<T> chain;
	private final Attribute<DateTime> attribute;

	DateTimeAttributeBuilder(AtlasQuery<T> chain, Attribute<DateTime> attribute) {
		this.chain = chain;
		this.attribute = attribute;
	}
	
	public AtlasQuery<T> equalTo(DateTime... values) {
		return chain.add(attribute.createQuery(Operators.EQUALS, ImmutableList.copyOf(values)));
	}
	
	public AtlasQuery<T> after(DateTime value) {
		return chain.add(attribute.createQuery(Operators.AFTER, ImmutableList.of(value)));
	}
	
	public AtlasQuery<T> before(DateTime value) {
		return chain.add(attribute.createQuery(Operators.BEFORE, ImmutableList.of(value)));
	}

	public AtlasQuery<T> between(DateTime lower, DateTime upper) {
		return chain.add(attribute.createQuery(Operators.AFTER, ImmutableList.of(lower))).add(attribute.createQuery(Operators.BEFORE, ImmutableList.of(upper)));
	}
	
	public AtlasQuery<T> between(Interval interval) {
		return between(interval.getStart(), interval.getEnd());
	}
}
