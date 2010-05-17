package org.uriplay.content.presentation;

import org.uriplay.media.entity.Brand;
import org.uriplay.media.entity.Item;

import com.metabroadcast.common.model.ModelBuilder;
import com.metabroadcast.common.model.SimpleModel;

/**
 * Renders the 'simple' (non-relational) attributes of a {@link Brand}
 * @author John Ayres (john@metabroadcast.com)
 */
public class SimpleBrandAttributesModelBuilder implements ModelBuilder<Brand> {

	public SimpleModel build(Brand brand) {
		SimpleModel model = new SimpleModel();
		model.put("title", brand.getTitle());
		model.put("uri", brand.getCanonicalUri());
		model.put("thumbnail", thubmnailFor(brand));
		model.put("description", brand.getDescription());
		return model;
	}
	
	private String thubmnailFor(Brand brand) {
		for (Item item : brand.getItems()) {
			if (item.getThumbnail() != null) {
				return item.getThumbnail();
			}
		}
		return null;
	}
}