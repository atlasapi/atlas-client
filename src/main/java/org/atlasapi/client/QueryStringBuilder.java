/* Copyright 2009 Meta Broadcast Ltd

Licensed under the Apache License, Version 2.0 (the "License"); you
may not use this file except in compliance with the License. You may
obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing
permissions and limitations under the License. */

package org.atlasapi.client;

import java.util.Iterator;
import java.util.List;

import org.atlasapi.content.criteria.AttributeQuery;
import org.atlasapi.content.criteria.BooleanAttributeQuery;
import org.atlasapi.content.criteria.ContentQuery;
import org.atlasapi.content.criteria.DateTimeAttributeQuery;
import org.atlasapi.content.criteria.EnumAttributeQuery;
import org.atlasapi.content.criteria.IntegerAttributeQuery;
import org.atlasapi.content.criteria.MatchesNothing;
import org.atlasapi.content.criteria.QueryVisitor;
import org.atlasapi.content.criteria.StringAttributeQuery;
import org.atlasapi.content.criteria.attribute.Attribute;
import org.atlasapi.content.criteria.operator.Operator;
import org.atlasapi.content.criteria.operator.Operators;
import org.atlasapi.content.criteria.operator.Operators.Equals;
import org.joda.time.DateTime;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.metabroadcast.common.query.Selection;
import com.metabroadcast.common.url.UrlEncoding;

class QueryStringBuilder {

	private static final Equals DEFAULT_OP = Operators.EQUALS;
	
	private static final Joiner QUERY_PARTS = Joiner.on('&');

	public String build(ContentQuery query) {
		
		List<String> queryParts = query.accept(new QueryVisitor<String>() {
			
			@Override
			public String visit(DateTimeAttributeQuery query) {
				return asString(query);
			}
			
			@Override
			public String visit(EnumAttributeQuery<?> query) {
				return asString(query);
			}
			
			@Override
			public String visit(BooleanAttributeQuery query) {
				return asString(query);
			}
			
			@Override
			public String visit(StringAttributeQuery query) {
				return asString(query);
			}
			

			@Override
			public String visit(IntegerAttributeQuery query) {
				return asString(query);
			}
			
			@Override
			public String visit(MatchesNothing noOp) {
				throw new IllegalArgumentException();
			}
			
			private String asString(AttributeQuery<?> query) {
				return asString(query.getAttribute(), query.getOperator(), query.getValue());
			}
			
			private String asString(Attribute<?> attribute, Operator operator, List<?> values) {
				String opString = DEFAULT_OP.equals(operator) ? "" : "-" + operator.name();
				return attribute.externalName() + opString  + "=" + encodeValues(values);
			}

		});

		List<String> queryPartsWithSelection = Lists.newArrayList(queryParts);
		
		Selection selection = query.getSelection();
		if (selection != null) {
			String params = selection.asQueryParameters();
			if (params.length() > 1) {
				queryPartsWithSelection.add(params);
			}
		}
		return QUERY_PARTS.join(queryPartsWithSelection);

	}
	
	private static String encodeValues(List<?> values) {
		StringBuilder b = new StringBuilder();
		for (Iterator<?> iterator = values.iterator(); iterator.hasNext();) {
			b.append(encodeValue(iterator.next()));
			if (iterator.hasNext()) {
				b.append(",");
			}
		}
		return b.toString();
	}
	
	private static String encodeValue(Object value) {
		return UrlEncoding.encode(asString(value));
	}
	
	private static String asString(Object value) {
		if (value instanceof DateTime) {
			return String.valueOf(((DateTime) value).getMillis());
		}
		return value.toString();
	}
}