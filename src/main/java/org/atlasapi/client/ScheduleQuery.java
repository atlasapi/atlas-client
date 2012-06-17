package org.atlasapi.client;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.LinkedHashSet;
import java.util.List;

import org.atlasapi.media.content.Channel;
import org.atlasapi.media.content.Publisher;
import org.atlasapi.output.Annotation;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.metabroadcast.common.url.QueryStringParameters;

public final class ScheduleQuery {
	
	private final Joiner CSV = Joiner.on(',');
	
	private final List<Channel> channels;
	private final Interval interval;

	private final List<Publisher> publishers;

    private ImmutableSet<Annotation> annotations;
	
	public ScheduleQuery(ScheduleQueryBuilder builder) {
		checkArgument(!builder.channels.isEmpty(), "No channels specified");
		channels = ImmutableList.copyOf(builder.channels);
		
		checkArgument(builder.start != null, "No start time specified");
		checkArgument(builder.end != null, "No end time specified");
		interval = new Interval(builder.start, builder.end);
		
		publishers = ImmutableList.copyOf(builder.publishers);
		annotations = builder.annotations;
	}
	
	QueryStringParameters toParams() {
		QueryStringParameters params = new QueryStringParameters();
		params.add("channel", CSV.join(Channel.toKeys(channels)));
		params.add("from", interval.getStart().toString());
		params.add("to", interval.getEnd().toString());
		
		if (!publishers.isEmpty()) {
			params.add("publisher", CSV.join(Iterables.transform(publishers, Publisher.TO_KEY)));
		}
		if (!annotations.isEmpty()) {
		    params.add("annotations", CSV.join(Iterables.transform(annotations, Annotation.TO_KEY)));
		}
		
		return params;
	}
	
	public static final class ScheduleQueryBuilder {
		
		private LinkedHashSet<Channel> channels = Sets.newLinkedHashSet();
		private LinkedHashSet<Publisher> publishers = Sets.newLinkedHashSet();
		private ImmutableSet<Annotation> annotations = ImmutableSet.of();
		
		private DateTime start = null;
		private DateTime end = null;
		
		private ScheduleQueryBuilder() {}

		public ScheduleQueryBuilder withChannels(Channel... channels) {
			this.channels.addAll(ImmutableList.copyOf(channels));
			return this;
		}

		public ScheduleQuery build() {
			return new ScheduleQuery(this);
		}

		public ScheduleQueryBuilder withOnBetween(Interval interval) {
			this.start = interval.getStart();
			this.end = interval.getEnd();
			return this;
		}

		
		public ScheduleQueryBuilder withPublishers(Iterable<Publisher> publishers) {
			this.publishers = Sets.newLinkedHashSet(publishers);
			return this;
		}
		
		public ScheduleQueryBuilder withAnnotations(Iterable<Annotation> annotations) {
		    this.annotations = ImmutableSet.copyOf(annotations);
		    return this;
		}
		
		public ScheduleQueryBuilder withAnnotations(Annotation...annotations) {
		    this.annotations = ImmutableSet.copyOf(annotations);
		    return this;
		}
	}

	public static ScheduleQueryBuilder builder() {
		return new ScheduleQueryBuilder();
	}
}
