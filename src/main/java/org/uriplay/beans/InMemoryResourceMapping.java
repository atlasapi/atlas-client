package org.uriplay.beans;

import java.util.Set;

import org.jherd.naming.ResourceMapping;
import org.jherd.persistence.BeanStore;
import org.jherd.util.caching.FixedExpiryCache;
import org.uriplay.media.entity.Description;

public class InMemoryResourceMapping implements BeanStore, ResourceMapping {

	private final FixedExpiryCache<String, Object> cache;
	
	public InMemoryResourceMapping(final int cacheTimoutInMins) {
		cache = createCache(cacheTimoutInMins);
	}

	private FixedExpiryCache<String, Object> createCache(final int cacheTimoutInMins) {
		return new FixedExpiryCache<String, Object>(cacheTimoutInMins) {

			@Override
			protected Object cacheMissFor(String key) {
				return null;
			}
		};
	}

	public boolean canMatch(String uri) {
		return true;
	}

	public Object getResource(String uri) {
		return cache.get(uri);
	}

	public String getUri(Object bean) {
		throw new UnsupportedOperationException();
	}

	public Set<String> getUris(Object bean) {
		if (bean instanceof Description) {
			Description description = (Description) bean;
			return description.getAllUris();
		}
		return null;
	}

	public boolean isReserved(String uri) {
		return false;
	}

	public Set<Object> getAllResources() {
		throw new UnsupportedOperationException();
	}

	public void store(Set<? extends Object> beans) {
		for (Object bean : beans) {
			for (String uri : getUris(bean)) {
				cache.put(uri, bean);
			}
 		}
	}

}
