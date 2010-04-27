package org.uriplay.content.presentation;

import java.util.List;
import java.util.Set;

import org.jherd.model.simple.ModelBuilder;
import org.jherd.model.simple.SimpleModel;
import org.uriplay.media.entity.Brand;
import org.uriplay.media.entity.Item;

import com.google.common.collect.Sets;
import com.google.soy.common.collect.Lists;


/**
 * Renders the 'simple' (non-relational) attributes of a {@link Item}
 * @author John Ayres (john@metabroadcast.com)
 */
public class SimpleItemAttributesModelBuilder implements ModelBuilder<Item> {

	private Set<String> allowedGenrePrefixes = Sets.newHashSet("http://uriplay.org/genres/uriplay/");
	
	public SimpleModel build(Item item) {
		SimpleModel model = new SimpleModel();
		Brand brand = item.primaryBrand();
		model.put("uri", item.getCanonicalUri());
		model.put("thumbnail", item.getThumbnail());
		if (item.getImage() != null) {
			model.put("image", item.getImage());
		} else if (item.getThumbnail() != null) {
			model.put("image", item.getThumbnail());
		}
		model.put("description", item.getDescription());
		model.put("curie", item.getCurie());
		model.put("externalUrl", item.getCanonicalUri());
		addPublisher(model, item);
		addTitles(model, item, brand);
		addGenres(model, item);
		return model;
	}
	
	private void addGenres(SimpleModel model, Item item) {
		List<SimpleModel> genres = Lists.newArrayList();
		for (String genreUri : item.getGenres()) {
			for (String prefix : allowedGenrePrefixes) {
				if (genreUri.startsWith(prefix)) {
					SimpleModel genreModel = new SimpleModel();
					genreModel.put("name", genreUri.substring(prefix.length()));
					genreModel.put("uri", genreUri);
					genres.add(genreModel);
					break;
				}
			}
		}
		model.put("genres", genres);		
	}
	
	private String displayName(String publisher) {
		if (publisher == null) { 
			return "";
		}
		if ("bbc.co.uk".equals(publisher)) {
			return "BBC iPlayer";
		}
		if ("channel4.com".equals(publisher)) {
			return "Channel 4 - 4OD";
		}
		if ("youtube".equals(publisher)) {
			return "YouTube";
		}
		if ("ted.com".equals(publisher)) {
			return "TED Talks";
		}
		if ("http://vimeo.com/".equals(publisher)) {
			return "Vimeo";
		}
		return "";
		
	}
	
	public void addTitles(SimpleModel model, Item item, Brand brand) {
		if (brand == null) {
			model.put("primaryTitle", item.getTitle());
		} else {
			model.put("primaryTitle", brand.getTitle());
			model.put("secondaryTitle", item.getTitle());
		}
	}
	
	public void addPublisher(SimpleModel model, Item item) {
		SimpleModel publisherModel = new SimpleModel();
		publisherModel.put("uri", item.getPublisher());
		publisherModel.put("name",  displayName(item.getPublisher()));
		model.put("publisher", publisherModel);
	}

//	private DateTime transmissionTime() {
//	Version version = version();
//	if (version == null) {
//		return null;
//	}
//	return version.getTransmissionTime();
//}
	
//	private Version version() {
//		Set<Version> versions = item.getVersions();
//		if (versions == null || versions.isEmpty()) {
//			return null;
//		}
//		if (versions.size() == 1) {
//			return Iterables.getOnlyElement(versions);
//		}
//		return Iterables.get(versions, 0);
//	}
}