/*  Copyright 2008 Niclas Hedhman.
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
package org.qi4j.test.performance.entitystore.jdbm;

import org.junit.Test;
import org.qi4j.api.common.Visibility;
import org.qi4j.bootstrap.Assembler;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.cache.ehcache.EhCacheConfiguration;
import org.qi4j.cache.ehcache.EhCachePoolService;
import org.qi4j.cache.ehcache.assembly.EhCacheAssembler;
import org.qi4j.entitystore.jdbm.JdbmConfiguration;
import org.qi4j.entitystore.jdbm.assembly.JdbmEntityStoreAssembler;
import org.qi4j.test.EntityTestAssembler;
import org.qi4j.test.performance.entitystore.model.AbstractEntityStorePerformanceTest;

import java.io.File;

/**
 * Performance test for JdbmEntityStoreComposite
 */
public class JdbmEntityStorePerformanceTest
    extends AbstractEntityStorePerformanceTest
{
    public JdbmEntityStorePerformanceTest()
    {
        super( "JdbmEntityStore", createAssembler() );
    }

    private static Assembler createAssembler()
    {
        return new Assembler()
        {
            public void assemble( ModuleAssembly module )
                throws AssemblyException
            {
                new JdbmEntityStoreAssembler( Visibility.module ).assemble( module );
                ModuleAssembly configModule = module.layer().module( "Config" );
                configModule.entities( JdbmConfiguration.class ).visibleIn( Visibility.layer );
                new EntityTestAssembler( Visibility.module ).assemble( configModule );

                module.services( EhCachePoolService.class );
                configModule.entities( EhCacheConfiguration.class ).visibleIn( Visibility.layer );
            }
        };
    }

    @Test
    @Override
    public void whenCreateEntityWithSinglePropertyThenRecordIterationsPerSecond()
        throws Exception
    {
        super.whenCreateEntityWithSinglePropertyThenRecordIterationsPerSecond();
    }

    @Test
    @Override
    public void whenCreateEntityWithSinglePropertyInBatchThenRecordIterationsPerSecond()
        throws Exception
    {
        super.whenCreateEntityWithSinglePropertyInBatchThenRecordIterationsPerSecond();
    }

    @Override
    public void cleanUp()
        throws Exception
    {
        super.cleanUp();
        File dbFile = new File( "target/jdbmstore.data.db" );
        boolean success = true;
        if( dbFile.exists() )
        {
            success = dbFile.delete();
        }

        File logFile = new File( "qi4j/jdbmstore.data.lg" );
        if( logFile.exists() )
        {
            success = success & logFile.delete();
        }
        if( !success )
        {
            throw new Exception( "Could not delete test data" );
        }
    }
}