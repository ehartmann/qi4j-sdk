/*
 * Copyright 2012, Paul Merlin.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qi4j.entitystore.leveldb;

import org.qi4j.api.common.Visibility;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.library.fileconfig.FileConfigurationService;
import org.qi4j.test.EntityTestAssembler;
import org.qi4j.test.entity.AbstractEntityStoreTest;

public class JavaLevelDBEntityStoreTest
    extends AbstractEntityStoreTest
{

    @Override
    // START SNIPPET: assembly
    public void assemble( ModuleAssembly module )
        throws AssemblyException
    {
        // END SNIPPET: assembly
        super.assemble( module );
        ModuleAssembly config = module.layer().module( "config" );
        new EntityTestAssembler( Visibility.module ).assemble( config );
        module.services( FileConfigurationService.class );

        // START SNIPPET: assembly
        new LevelDBEntityStoreAssembler().
            withConfig( config, Visibility.layer ).
            identifiedBy( "java-leveldb-entitystore" ).
            assemble( module );
        // END SNIPPET: assembly

        config.forMixin( LevelDBEntityStoreConfiguration.class ).declareDefaults().flavour().set( "java" );
        // START SNIPPET: assembly
    }
    // END SNIPPET: assembly

}
