package org.atlasapi.media.entity.simple;

import org.joda.time.DateTime;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

public class Identified {

	protected String uri;
	protected String curie;
	protected String id;
	protected String type;
	protected Audit audit;
	
	public Identified(String uri) {
		this.uri = uri;
	}
	
	public Identified() { /* required for XML/JSON tools */	}
	
	@Override
	public int hashCode() {
		if (uri == null) {
			return super.hashCode();
		}
		return uri.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (uri != null && obj instanceof Identified) {
			return uri.equals(((Identified) obj).uri);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(id:"  + id + ")";
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public void setCurie(String curie) {
		this.curie = curie;
	}

	public String getCurie() {
		return curie;
	}
	
	public String getId() {
        return id;
    }
	
	public void setId(String id) {
        this.id = id;
    }
	
	public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Audit getAudit() {
        return this.audit;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }

    protected void copyTo(Identified destination) {
        Preconditions.checkNotNull(destination);
        
        destination.setUri(getUri());
        destination.setCurie(getCurie());
        destination.setId(getId());
        destination.setType(getType());
    }

    public static final Function<Identified, String> TO_ID = new Function<Identified, String>() {
        @Override
        public String apply(Identified input) {
            return input.getId();
        }
    };
    
    public static final Function<Identified, String> TO_URI = new Function<Identified, String>() {
        @Override
        public String apply(Identified input) {
            return input.getUri();
        }
    };
}