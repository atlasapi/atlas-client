package org.atlasapi.client;

import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.ScheduleQueryResult;

interface StringQueryClient {
	
	ContentQueryResult query(String queryUri);

	ScheduleQueryResult scheduleQuery(String queryUri);

}
