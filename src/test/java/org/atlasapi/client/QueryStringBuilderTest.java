/* Copyright 2010 Meta Broadcast Ltd

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

import static org.atlasapi.content.criteria.ContentQueryBuilder.query;
import static org.junit.Assert.assertEquals;

import org.atlasapi.content.criteria.ContentQueryBuilder;
import org.atlasapi.content.criteria.attribute.Attributes;
import org.atlasapi.media.entity.MediaType;
import org.junit.Test;

import com.metabroadcast.common.query.Selection;

public class QueryStringBuilderTest  {

	private QueryStringBuilder builder;
	
	@Test
	public void testTheBuilder() throws Exception {
		this.builder = new QueryStringBuilder();
		
		check(query().equalTo(Attributes.DESCRIPTION_TYPE, MediaType.AUDIO), "mediaType=AUDIO");
		
		check(query().equalTo(Attributes.DESCRIPTION_GENRE, "a", "b"), "genre=a,b");
	}
	
	@Test
	public void testQueryLimits() throws Exception {
		this.builder = new QueryStringBuilder();
		check(query().equalTo(Attributes.DESCRIPTION_GENRE, "funny").withSelection(new Selection(0, 10)), "genre=funny&limit=10");
	}
	
	@Test
	public void testQueryApiKey() throws Exception {
		this.builder = new QueryStringBuilder();
		builder.setApiKey("testKey");
		
		check(query().equalTo(Attributes.DESCRIPTION_TYPE, MediaType.AUDIO), "mediaType=AUDIO&apiKey=testKey");
		check(query().equalTo(Attributes.DESCRIPTION_GENRE, "a", "b"), "genre=a,b&apiKey=testKey");
	}

	private void check(ContentQueryBuilder query, String expected) {
		assertEquals(expected, this.builder.build(query.build()));
	}
}
