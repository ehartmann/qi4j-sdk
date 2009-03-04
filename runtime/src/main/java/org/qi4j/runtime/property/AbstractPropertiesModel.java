/*
 * Copyright (c) 2008, Rickard Öberg. All Rights Reserved.
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

package org.qi4j.runtime.property;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.qi4j.api.constraint.ConstraintViolationException;
import org.qi4j.api.property.Property;
import org.qi4j.api.property.StateHolder;
import org.qi4j.api.common.QualifiedName;
import org.qi4j.api.util.MethodKeyMap;
import org.qi4j.api.util.MethodValueMap;
import org.qi4j.api.value.ValueComposite;
import org.qi4j.bootstrap.PropertyDeclarations;
import org.qi4j.runtime.composite.ConstraintsModel;
import org.qi4j.runtime.composite.Resolution;
import org.qi4j.runtime.composite.BindingException;
import org.qi4j.runtime.value.ValueInstance;
import org.qi4j.runtime.value.ValueModel;
import org.qi4j.runtime.structure.Binder;

/**
 * Base class for properties model
 */
public abstract class AbstractPropertiesModel<T extends AbstractPropertyModel>
    implements Serializable, Binder
{
    protected final List<T> propertyModels = new ArrayList<T>();
    private final Map<QualifiedName, Method> accessors = new MethodValueMap<QualifiedName>();
    protected final Map<Method, T> mapMethodPropertyModel = new MethodKeyMap<T>();
    protected final ConstraintsModel constraints;
    protected PropertyDeclarations propertyDeclarations;
    protected boolean immutable;

    public AbstractPropertiesModel( ConstraintsModel constraints, PropertyDeclarations propertyDeclarations, boolean immutable )
    {
        this.constraints = constraints;
        this.propertyDeclarations = propertyDeclarations;
        this.immutable = immutable;
    }

    public void addPropertyFor( Method method )
    {
        if( Property.class.isAssignableFrom( method.getReturnType() ) )
        {
            T propertyModel = newPropertyModel( method );
            propertyModels.add( propertyModel );
            accessors.put( propertyModel.qualifiedName(), propertyModel.accessor() );
            mapMethodPropertyModel.put( method, propertyModel );
        }
    }

    public void bind( Resolution resolution ) throws BindingException
    {
        for( T propertyModel : propertyModels )
        {
            propertyModel.bind( resolution );
        }
    }

    public List<T> properties()
    {
        return new ArrayList<T>( propertyModels );
    }

    public PropertiesInstance newBuilderInstance()
    {
        Map<Method, Property<?>> properties = new MethodKeyMap<Property<?>>();
        for( T propertyModel : propertyModels )
        {
            Property property = propertyModel.newBuilderInstance();
            properties.put( propertyModel.accessor(), property );
        }

        return new PropertiesInstance( properties );
    }

    public PropertiesInstance newBuilderInstance( StateHolder state )
    {
        Map<Method, Property<?>> properties = new HashMap<Method, Property<?>>();
        for( T propertyModel : propertyModels )
        {
            Property property;
            if( !propertyModel.isComputed() )
            {
                Object initialValue = state.getProperty( propertyModel.accessor() ).get();

                initialValue = cloneInitialValue( initialValue, true );

                property = propertyModel.newBuilderInstance( initialValue );
            }
            else
            {
                property = propertyModel.newBuilderInstance();
            }
            properties.put( propertyModel.accessor(), property );
        }

        return new PropertiesInstance( properties );
    }

    public PropertiesInstance newInitialInstance()
    {
        Map<Method, Property<?>> properties = new MethodKeyMap<Property<?>>();
        for( T propertyModel : propertyModels )
        {
            Property property = propertyModel.newInitialInstance();
            properties.put( propertyModel.accessor(), property );
        }

        return new PropertiesInstance( properties );
    }

    public PropertiesInstance newInstance( StateHolder state )
    {
        Map<Method, Property<?>> properties = new MethodKeyMap<Property<?>>();
        for( AbstractPropertyModel propertyModel : propertyModels )
        {
            Object initialValue = state.getProperty( propertyModel.accessor() ).get();

            initialValue = cloneInitialValue( initialValue, false );

            // Create property instance
            Property property = propertyModel.newInstance( initialValue );
            properties.put( propertyModel.accessor(), property );
        }
        return new PropertiesInstance( properties );
    }

    private Object cloneInitialValue( Object initialValue, boolean isPrototype )
    {
        if( initialValue instanceof Collection )
        {
            Collection<Object> initialCollection = (Collection<Object>) initialValue;
            Collection<Object> newCollection;
            // Create new unmodifiable collection
            if( initialValue instanceof List )
            {
                newCollection = new ArrayList<Object>();
                initialValue = isPrototype ? newCollection : Collections.unmodifiableList( (List<Object>) newCollection );
            } else
            {
                newCollection = new HashSet<Object>();
                initialValue = isPrototype ? newCollection : Collections.unmodifiableSet( (Set<Object>) newCollection );
            }

            // Copy values, ensuring that values are cloned correctly
            for( Object value : initialCollection )
            {
                if (value instanceof ValueComposite )
                {
                    value = cloneValue( value, isPrototype );

                }

                newCollection.add( value );
            }
        } else if (initialValue instanceof ValueComposite)
        {
            initialValue = cloneValue(initialValue, isPrototype);
        }
        return initialValue;
    }

    private Object cloneValue( Object value, boolean isPrototype )
    {
        // Create real value
        ValueInstance instance = ValueInstance.getValueInstance( (ValueComposite) value );

        ValueModel model = (ValueModel) instance.compositeModel();
        StateHolder state;
        if (isPrototype)
            state = model.state().newBuilderInstance( instance.state() );
        else
            state = model.state().newInstance( instance.state() );
        ValueInstance newInstance = model.newValueInstance( instance.module(), state);
        return newInstance.proxy();
    }

    public T getPropertyByName( String name )
    {
        for( T propertyModel : propertyModels )
        {
            if( propertyModel.qualifiedName().name().equals( name ) )
            {
                return propertyModel;
            }
        }
        return null;
    }

    public T getPropertyByQualifiedName( QualifiedName name )
    {
        for( T propertyModel : propertyModels )
        {
            if( propertyModel.qualifiedName().equals( name ) )
            {
                return propertyModel;
            }
        }
        return null;
    }

    public T getPropertyByAccessor(Method accessor)
    {
        return mapMethodPropertyModel.get( accessor );
    }

    public void checkConstraints( PropertiesInstance properties, boolean allowNull )
        throws ConstraintViolationException
    {
        for( AbstractPropertyModel propertyModel : propertyModels )
        {
            Property property = properties.propertyFor( propertyModel.accessor() );
            if( !propertyModel.isComputed() )
            {
                propertyModel.checkConstraints( property.get(), allowNull );
            }
        }
    }

    protected abstract T newPropertyModel( Method method );
}