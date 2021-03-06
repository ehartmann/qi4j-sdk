/*
 * Copyright (c) 2007, Rickard Öberg. All Rights Reserved.
 * Copyright (c) 2007, Niclas Hedhman. All Rights Reserved.
 * Copyright (c) 2007, Alin Dreghiciu. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.qi4j.runtime.value;

import java.io.StringWriter;
import java.lang.reflect.Proxy;
import org.json.JSONException;
import org.qi4j.api.composite.CompositeInstance;
import org.qi4j.api.json.JSONWriterSerializer;
import org.qi4j.api.value.ValueComposite;
import org.qi4j.runtime.association.AssociationModel;
import org.qi4j.runtime.association.ManyAssociationModel;
import org.qi4j.runtime.composite.MixinsInstance;
import org.qi4j.runtime.composite.TransientInstance;
import org.qi4j.runtime.property.PropertyInstance;
import org.qi4j.runtime.property.PropertyModel;
import org.qi4j.runtime.structure.ModuleInstance;

/**
 * ValueComposite instance
 */
public final class ValueInstance extends TransientInstance
    implements CompositeInstance, MixinsInstance
{
    public static ValueInstance getValueInstance( ValueComposite composite )
    {
        return (ValueInstance) Proxy.getInvocationHandler( composite );
    }

    public ValueInstance( ValueModel compositeModel,
                          ModuleInstance moduleInstance,
                          Object[] mixins,
                          ValueStateInstance state
    )
    {
        super( compositeModel, moduleInstance, mixins, state );
    }

    @Override
    public boolean equals( Object o )
    {
        if( this == o )
        {
            return true;
        }
        if( o == null || !Proxy.isProxyClass( o.getClass() ) )
        {
            return false;
        }

        try
        {
            ValueInstance that = (ValueInstance) Proxy.getInvocationHandler( o );
            return state.equals( that.state );
        }
        catch( ClassCastException e )
        {
            return false;
        }
    }

    @Override
    public ValueStateInstance state()
    {
        return (ValueStateInstance) state;
    }

    @Override
    public ValueModel descriptor()
    {
        return (ValueModel) compositeModel;
    }

    /**
     * When a ValueBuilder is about to start, ensure that all state has builder infos, i.e. they are mutable.
     */
    public void prepareToBuild()
    {
        for( PropertyModel propertyDescriptor : descriptor().state().properties() )
        {
            PropertyInstance<Object> propertyInstance =
                (PropertyInstance<Object>) state.propertyFor( propertyDescriptor.accessor() );

            propertyInstance.prepareToBuild( propertyDescriptor );
        }

        for( AssociationModel associationDescriptor : descriptor().state().associations() )
        {
            state().associationFor( associationDescriptor.accessor() )
                .setAssociationInfo( associationDescriptor.getBuilderInfo() );
        }

        for( ManyAssociationModel associationDescriptor : descriptor().state().manyAssociations() )
        {
            state().manyAssociationFor( associationDescriptor.accessor() )
                .setAssociationInfo( associationDescriptor.getBuilderInfo() );
        }
    }

    /**
     * When a ValueBuilder is finished and is about to instantiate a Value, call this to ensure that the state has correct
     * settings, i.e. is immutable.
     */
    public void prepareBuilderState()
    {
        for( PropertyModel propertyDescriptor : descriptor().state().properties() )
        {
            PropertyInstance<Object> propertyInstance =
                (PropertyInstance<Object>) state.propertyFor( propertyDescriptor.accessor() );
            propertyInstance.prepareBuilderState( propertyDescriptor );
        }

        for( AssociationModel associationDescriptor : descriptor().state().associations() )
        {
            state().associationFor( associationDescriptor.accessor() ).setAssociationInfo( associationDescriptor );
        }

        for( ManyAssociationModel associationDescriptor : descriptor().state().manyAssociations() )
        {
            state().manyAssociationFor( associationDescriptor.accessor() ).setAssociationInfo( associationDescriptor );
        }
    }

    @Override
    public int hashCode()
    {
        return state.hashCode();
    }

    @Override
    public String toString()
    {
        StringWriter string = new StringWriter();
        try
        {
            new JSONWriterSerializer( string ).serialize( this.<ValueComposite>proxy() );
        }
        catch( JSONException e )
        {
            throw new IllegalStateException( "Could not JSON serialize value", e );
        }
        return string.toString();
    }
}