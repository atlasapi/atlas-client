package org.atlasapi.client.query;

import java.util.List;

import org.atlasapi.content.criteria.attribute.Attribute;
import org.atlasapi.content.criteria.operator.Operators;

import com.google.common.collect.ImmutableList;

public class EnumAttributeBuilder<T, E extends Enum<E>> {

    private final AtlasQuery chain;
    private final Attribute<Enum<E>> attribute;

    public EnumAttributeBuilder(AtlasQuery chain, Attribute<Enum<E>> attribute) {
        this.chain = chain;
        this.attribute = attribute;
    }
    
    public AtlasQuery equalTo(E... values) {
        return chain.add(attribute.createQuery(Operators.EQUALS, ImmutableList.copyOf(values)));
    }
    
    public AtlasQuery in(List<E> values) {
        return chain.add(attribute.createQuery(Operators.EQUALS, ImmutableList.copyOf(values)));
    }
}
