package org.atlasapi.client;

import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.PeopleQueryResult;
import org.atlasapi.media.entity.simple.ScheduleQueryResult;
import org.atlasapi.media.entity.simple.TopicQueryResult;

interface StringQueryClient {
	
	ContentQueryResult contentQuery(String queryUri);

	ScheduleQueryResult scheduleQuery(String queryUri);

	PeopleQueryResult peopleQuery(String queryUri);
	
	TopicQueryResult topicQuery(String queryUri);
	
}
