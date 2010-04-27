package org.uriplay.content.presentation;

enum PublisherDisplayName {
	
	BBC("bbc.co.uk", "BBC iPlayer"),
	C4("channel4.com", "Channel4 4OD" ),
	YOUTUBE("youtube", "YouTube");

	private final String id;
	private final String displayName;

	PublisherDisplayName(String id, String displayName) {
		this.id = id;
		this.displayName = displayName;
	}
	
	public String displayName() {
		return displayName;
	}

	public static PublisherDisplayName forPublisher(String publisher) {
		for (PublisherDisplayName name : values()) {
			if (name.id.equals(publisher)) {
				return name;
			}
		}
		throw new IllegalArgumentException("unknown publisher");
	}
}