/*  Copyright 2008 Edward Yakop.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
* implied.
*
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.qi4j.library.swing.visualizer.detailPanel.internal.tree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import org.qi4j.library.swing.visualizer.model.ApplicationDetailDescriptor;
import org.qi4j.library.swing.visualizer.model.CompositeDetailDescriptor;
import org.qi4j.library.swing.visualizer.model.CompositeMethodDetailDescriptor;
import org.qi4j.library.swing.visualizer.model.EntityDetailDescriptor;
import org.qi4j.library.swing.visualizer.model.LayerDetailDescriptor;
import org.qi4j.library.swing.visualizer.model.MixinDetailDescriptor;
import org.qi4j.library.swing.visualizer.model.ModuleDetailDescriptor;
import org.qi4j.library.swing.visualizer.model.ObjectDetailDescriptor;
import org.qi4j.library.swing.visualizer.model.ServiceDetailDescriptor;
import org.qi4j.spi.composite.CompositeDescriptor;

/**
 * TODO: localization
 *
 * @author edward.yakop@gmail.com
 * @since 0.5
 */
final class TreeModelBuilder
{
    public final DefaultMutableTreeNode build( ApplicationDetailDescriptor aDetailDescriptor )
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode( aDetailDescriptor );

        if( aDetailDescriptor != null )
        {
            addLayersNode( root, aDetailDescriptor );
        }

        return root;
    }

    private void addLayersNode( DefaultMutableTreeNode root, ApplicationDetailDescriptor aDetailDescriptor )
    {
        Iterable<LayerDetailDescriptor> layers = aDetailDescriptor.layers();
        for( LayerDetailDescriptor layer : layers )
        {
            DefaultMutableTreeNode layerNode = new DefaultMutableTreeNode( layer );
            root.add( layerNode );

            addModulesNode( layerNode, layer );
        }
    }

    private void addIfNotEmpty( DefaultMutableTreeNode parent, MutableTreeNode children )
    {
        int numberOfChildren = children.getChildCount();
        if( numberOfChildren > 0 )
        {
            parent.add( children );
        }
    }

    private void addModulesNode( DefaultMutableTreeNode layerNode, LayerDetailDescriptor aLayer )
    {
        Iterable<ModuleDetailDescriptor> modules = aLayer.modules();
        for( ModuleDetailDescriptor module : modules )
        {
            addModuleNode( layerNode, module );
        }
    }

    private void addModuleNode( DefaultMutableTreeNode layerNode, ModuleDetailDescriptor aModule )
    {
        DefaultMutableTreeNode moduleNode = new DefaultMutableTreeNode( aModule );
        layerNode.add( moduleNode );

        DefaultMutableTreeNode servicesNode = new DefaultMutableTreeNode( "services" );
        addServiceNodes( servicesNode, aModule );
        addIfNotEmpty( moduleNode, servicesNode );

        DefaultMutableTreeNode entities = new DefaultMutableTreeNode( "entities" );
        addEntityNodes( entities, aModule );
        addIfNotEmpty( moduleNode, entities );

        DefaultMutableTreeNode composites = new DefaultMutableTreeNode( "composites" );
        addCompositeNodes( composites, aModule );
        addIfNotEmpty( moduleNode, composites );

        DefaultMutableTreeNode objects = new DefaultMutableTreeNode( "objects" );
        addObjectNodes( objects, aModule );
        addIfNotEmpty( moduleNode, objects );
    }

    private void addServiceNodes( DefaultMutableTreeNode aServicesNode, ModuleDetailDescriptor aModule )
    {
        Iterable<ServiceDetailDescriptor> services = aModule.services();
        for( ServiceDetailDescriptor service : services )
        {
            aServicesNode.add( new DefaultMutableTreeNode( service ) );
        }
    }

    private void addEntityNodes( DefaultMutableTreeNode aEntitiesNode, ModuleDetailDescriptor aModule )
    {
        Iterable<EntityDetailDescriptor> entities = aModule.entities();
        for( EntityDetailDescriptor entity : entities )
        {
            DefaultMutableTreeNode entityNode = new DefaultMutableTreeNode( entity );
            aEntitiesNode.add( entityNode );
        }
    }

    @SuppressWarnings( "unchecked" )
    private void addCompositeNodes( DefaultMutableTreeNode aCompositesNode, ModuleDetailDescriptor aModule )
    {
        Iterable<CompositeDetailDescriptor> composites = aModule.composites();
        for( CompositeDetailDescriptor composite : composites )
        {
            addCompositeNode( aCompositesNode, composite );
        }
    }

    private void addCompositeNode(
        DefaultMutableTreeNode aCompositesNode,
        CompositeDetailDescriptor<CompositeDescriptor> aDescriptor )
    {
        DefaultMutableTreeNode compositeNode = new DefaultMutableTreeNode( aDescriptor );
        aCompositesNode.add( compositeNode );

        DefaultMutableTreeNode mixinsNode = new DefaultMutableTreeNode( "mixins" );
        addMixinNodes( mixinsNode, aDescriptor );
        addIfNotEmpty( compositeNode, mixinsNode );

        DefaultMutableTreeNode methodsNode = new DefaultMutableTreeNode( "methods" );
        addMethodsNode( methodsNode, aDescriptor );
        addIfNotEmpty( compositeNode, methodsNode );
    }

    private void addMixinNodes(
        DefaultMutableTreeNode mixinsNode,
        CompositeDetailDescriptor<CompositeDescriptor> aCompositeDetailDescriptor )
    {
        Iterable<MixinDetailDescriptor> mixins = aCompositeDetailDescriptor.mixins();
        for( MixinDetailDescriptor mixin : mixins )
        {
            addMixinNode( mixinsNode, mixin );
        }
    }

    private void addMixinNode( DefaultMutableTreeNode mixinsNode, MixinDetailDescriptor aMixin )
    {
        DefaultMutableTreeNode mixinNode = new DefaultMutableTreeNode( aMixin );
        mixinsNode.add( mixinNode );

        // Constructors
        DefaultMutableTreeNode constructorsNode = new DefaultMutableTreeNode( "constructors" );
        addIterableItemNodes( constructorsNode, aMixin.constructors() );
        addIfNotEmpty( mixinNode, constructorsNode );

        // Injected fields
        DefaultMutableTreeNode fieldsNode = new DefaultMutableTreeNode( "injected fields" );
        addIterableItemNodes( fieldsNode, aMixin.injectedFields() );
        addIfNotEmpty( mixinNode, fieldsNode );

        // Injected methods
        DefaultMutableTreeNode methodsNode = new DefaultMutableTreeNode( "injected methods" );
        addIterableItemNodes( methodsNode, aMixin.injectedMethods() );
        addIfNotEmpty( mixinNode, methodsNode );
    }

    private void addIterableItemNodes( DefaultMutableTreeNode aGroupNode, Iterable iterable )
    {
        for( Object item : iterable )
        {
            DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode( item );
            aGroupNode.add( itemNode );
        }
    }

    private void addMethodsNode(
        DefaultMutableTreeNode methodsNode,
        CompositeDetailDescriptor<CompositeDescriptor> aCompositeDetailDescriptor )
    {
        Iterable<CompositeMethodDetailDescriptor> methods = aCompositeDetailDescriptor.methods();
        for( CompositeMethodDetailDescriptor method : methods )
        {
            DefaultMutableTreeNode methodNode = new DefaultMutableTreeNode( method );
            methodsNode.add( methodNode );
        }
    }

    private void addObjectNodes( DefaultMutableTreeNode aObjectsNode, ModuleDetailDescriptor aModule )
    {
        Iterable<ObjectDetailDescriptor> objects = aModule.objects();
        for( ObjectDetailDescriptor object : objects )
        {
            addObjectNode( aObjectsNode, object );
        }
    }

    private void addObjectNode( DefaultMutableTreeNode aObjectsNode, ObjectDetailDescriptor aDescriptor )
    {
        DefaultMutableTreeNode objectNode = new DefaultMutableTreeNode( aDescriptor );
        aObjectsNode.add( objectNode );

        // Constructors
        DefaultMutableTreeNode constructorsNode = new DefaultMutableTreeNode( "constructors" );
        addIterableItemNodes( constructorsNode, aDescriptor.constructors() );
        addIfNotEmpty( objectNode, constructorsNode );

        // Injected fields
        DefaultMutableTreeNode fieldsNode = new DefaultMutableTreeNode( "injected fields" );
        addIterableItemNodes( fieldsNode, aDescriptor.injectedFields() );
        addIfNotEmpty( objectNode, fieldsNode );

        // Injected methods
        DefaultMutableTreeNode methodsNode = new DefaultMutableTreeNode( "injected methods" );
        addIterableItemNodes( methodsNode, aDescriptor.injectedMethods() );
        addIfNotEmpty( objectNode, methodsNode );
    }
}
