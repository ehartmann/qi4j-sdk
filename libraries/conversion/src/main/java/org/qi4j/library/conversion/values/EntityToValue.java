package org.qi4j.library.conversion.values;

import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.qi4j.api.association.Association;
import org.qi4j.api.association.AssociationDescriptor;
import org.qi4j.api.association.AssociationStateDescriptor;
import org.qi4j.api.association.AssociationStateHolder;
import org.qi4j.api.association.ManyAssociation;
import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.entity.EntityDescriptor;
import org.qi4j.api.entity.EntityReference;
import org.qi4j.api.entity.Identity;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.property.PropertyDescriptor;
import org.qi4j.api.structure.Module;
import org.qi4j.api.type.CollectionType;
import org.qi4j.api.value.NoSuchValueException;
import org.qi4j.api.value.ValueBuilder;
import org.qi4j.api.value.ValueBuilderFactory;
import org.qi4j.api.value.ValueDescriptor;
import org.qi4j.functional.Function;
import org.qi4j.functional.Iterables;
import org.qi4j.spi.Qi4jSPI;

@Mixins( EntityToValue.EntityToValueMixin.class )
public interface EntityToValue
{
    <T> T convert( Class<T> valueType, Object entity );

    static class EntityToValueMixin
        implements EntityToValue
    {
        @Structure
        private ValueBuilderFactory vbf;

        @Structure
        private Qi4jSPI spi;

        @Structure
        private Module module;

        @Override
        public <T> T convert( final Class<T> valueType, Object entity )
        {
            ValueDescriptor valueDescriptor = module.valueDescriptor( valueType.getName() );
            if( valueDescriptor == null )
            {
                throw new NoSuchValueException( valueType.getName(), module.name() );
            }
            Unqualified unqualified = valueDescriptor.metaInfo( Unqualified.class );
//            Iterable<? extends PropertyDescriptor> properties = valueDescriptor.state().properties();
            final EntityComposite composite = (EntityComposite) entity;
            final EntityDescriptor entityDescriptor = spi.getEntityDescriptor( composite );
            final AssociationStateHolder associationState = spi.getState( composite );
            ValueBuilder builder;

            if( unqualified == null || !unqualified.value() )
            {
                // Copy state using qualified names
                builder = vbf.newValueBuilderWithState(
                    valueType,
                    new Function<PropertyDescriptor, Object>()
                    {
                        @Override
                        public Object map( PropertyDescriptor descriptor )
                        {
                            try
                            {
                                return associationState.propertyFor( descriptor.accessor() ).get();
                            }
                            catch( IllegalArgumentException e )
                            {
                                AssociationStateDescriptor entityState = entityDescriptor.state();
                                String associationName = descriptor.qualifiedName().name();
                                if( descriptor.valueType().mainType().equals( String.class ) )
                                {
                                    // Find Association and convert to string
                                    AssociationDescriptor associationDescriptor;
                                    try
                                    {
                                        associationDescriptor = entityState.getAssociationByName( associationName );
                                    }
                                    catch( IllegalArgumentException e1 )
                                    {
                                        return null;
                                    }
                                    AccessibleObject associationMethod = associationDescriptor.accessor();
                                    Object entity = associationState.associationFor( associationMethod ).get();
                                    if( entity != null )
                                    {
                                        return ( (Identity) entity ).identity().get();
                                    }
                                    else
                                    {
                                        return null;
                                    }
                                }
                                else if( descriptor.valueType() instanceof CollectionType
                                         && ( (CollectionType) descriptor.valueType() ).collectedType()
                                    .mainType()
                                    .equals( String.class ) )
                                {
                                    AssociationDescriptor associationDescriptor;
                                    try
                                    {
                                        associationDescriptor = entityState.getManyAssociationByName( associationName );
                                    }
                                    catch( IllegalArgumentException e1 )
                                    {
                                        return Collections.emptyList();
                                    }

                                    ManyAssociation state = associationState.manyAssociationFor( associationDescriptor.accessor() );
                                    List<String> entities = new ArrayList<String>();
                                    for( Object entity : state )
                                    {
                                        entities.add( ( (Identity) entity ).identity().get() );
                                    }
                                    return entities;
                                }

                                return null;
                            }
                        }
                    }, new Function<AssociationDescriptor, EntityReference>()
                    {
                        @Override
                        public EntityReference map( AssociationDescriptor associationDescriptor )
                        {
                            return EntityReference.getEntityReference(
                                associationState.associationFor( associationDescriptor.accessor() ).get() );
                        }
                    }, new Function<AssociationDescriptor, Iterable<EntityReference>>()
                    {
                        @Override
                        public Iterable<EntityReference> map( AssociationDescriptor associationDescriptor )
                        {
                            List<EntityReference> refs = new ArrayList<EntityReference>();
                            for( Object entity : associationState.manyAssociationFor( associationDescriptor.accessor() ) )
                            {
                                refs.add( EntityReference.getEntityReference( entity ) );
                            }
                            return refs;
                        }
                    }
                );
            }
            else
            {
                builder = vbf.newValueBuilderWithState(
                    valueType,
                    new Function<PropertyDescriptor, Object>()
                    {
                        @Override
                        public Object map( final PropertyDescriptor descriptor )
                        {
                            AssociationStateDescriptor entityState = entityDescriptor.state();
                            String propertyName = descriptor.qualifiedName().name();
                            try
                            {
                                PropertyDescriptor propertyDescriptor = entityState.getPropertyByName( propertyName );
                                return associationState.propertyFor( propertyDescriptor.accessor() ).get();
                            }
                            catch( Exception e )
                            {
                                if( descriptor.valueType().mainType().equals( String.class ) )
                                {
                                    // Find Association and convert to string
                                    AssociationDescriptor associationDescriptor;
                                    try
                                    {
                                        associationDescriptor = entityState.getAssociationByName( propertyName );
                                    }
                                    catch( IllegalArgumentException e1 )
                                    {
                                        return null;
                                    }

                                    AccessibleObject associationMethod = associationDescriptor.accessor();
                                    Object entity = associationState.associationFor( associationMethod ).get();
                                    if( entity != null )
                                    {
                                        return ( (Identity) entity ).identity().get();
                                    }
                                    else
                                    {
                                        return null;
                                    }
                                }
                                else if( descriptor.valueType() instanceof CollectionType
                                         && ( (CollectionType) descriptor.valueType() ).collectedType().mainType().equals( String.class ) )
                                {
                                    AssociationDescriptor associationDescriptor;
                                    try
                                    {
                                        associationDescriptor = entityState.getManyAssociationByName( propertyName );
                                    }
                                    catch( IllegalArgumentException e1 )
                                    {
                                        return null;
                                    }

                                    AccessibleObject associationMethod = associationDescriptor.accessor();
                                    ManyAssociation state = associationState.manyAssociationFor( associationMethod );
                                    List<String> entities = new ArrayList<String>();
                                    for( Object entity : state )
                                    {
                                        entities.add( ( (Identity) entity ).identity().get() );
                                    }
                                    return entities;
                                }
                                return null;
                            }
                        }
                    }, new Function<AssociationDescriptor, EntityReference>()
                    {
                        @Override
                        public EntityReference map( AssociationDescriptor descriptor )
                        {
                            AssociationDescriptor associationDescriptor;
                            try
                            {
                                associationDescriptor = entityDescriptor.state()
                                    .getAssociationByName( descriptor.qualifiedName()
                                                               .name() );
                            }
                            catch( Exception e )
                            {
                                return null;
                            }

                            AccessibleObject associationMethod = associationDescriptor.accessor();
                            Association<Object> association = associationState.associationFor( associationMethod );
                            return EntityReference.getEntityReference( association.get() );
                        }
                    }, new Function<AssociationDescriptor, Iterable<EntityReference>>()
                    {
                        @Override
                        public Iterable<EntityReference> map( final AssociationDescriptor descriptor )
                        {
                            AssociationDescriptor associationDescriptor;
                            try
                            {
                                String associationName = descriptor.qualifiedName().name();
                                AssociationStateDescriptor entityState = entityDescriptor.state();
                                associationDescriptor = entityState.getManyAssociationByName( associationName );
                            }
                            catch( IllegalArgumentException e )
                            {
                                return Iterables.empty();
                            }

                            List<EntityReference> refs = new ArrayList<EntityReference>();
                            AccessibleObject associationMethod = associationDescriptor.accessor();
                            for( Object entity : associationState.manyAssociationFor( associationMethod ) )
                            {
                                refs.add( EntityReference.getEntityReference( entity ) );
                            }
                            return refs;
                        }
                    }
                );
            }
            return createInstance( builder );
        }

        @SuppressWarnings( "unchecked" )
        private <T> T createInstance( ValueBuilder builder )
        {
            return (T) builder.newInstance();
        }
    }
}
