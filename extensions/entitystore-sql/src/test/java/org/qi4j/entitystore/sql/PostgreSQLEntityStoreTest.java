/*
 * Copyright (c) 2010, Stanislav Muhametsin. All Rights Reserved.
 * Copyright (c) 2010, Paul Merlin. All Rights Reserved.
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
package org.qi4j.entitystore.sql;

import java.sql.Connection;
import java.sql.Statement;
import javax.sql.DataSource;
import org.junit.Ignore;
import org.qi4j.api.common.Visibility;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.usecase.UsecaseBuilder;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.entitystore.memory.MemoryEntityStoreService;
import org.qi4j.entitystore.sql.assembly.PostgreSQLEntityStoreAssembler;
import org.qi4j.entitystore.sql.internal.SQLs;
import org.qi4j.library.sql.assembly.DataSourceAssembler;
import org.qi4j.library.sql.common.SQLConfiguration;
import org.qi4j.library.sql.common.SQLUtil;
import org.qi4j.library.sql.dbcp.DBCPDataSourceServiceAssembler;
import org.qi4j.test.entity.AbstractEntityStoreTest;

/**
 * WARN This test is deactivated on purpose, please do not commit it activated.
 *
 * To run it you need to have a user & database set up in postgresql. Here are two snippets to create and drop the
 * needed test environment.
 *
 * Use 'password' as password for the jdbc_test_login user.
 *
 * <pre>
 * createuser -A -D -P -E -W jdbc_test_login
 * createdb -O jdbc_test_login -W jdbc_test_db
 * psql -d jdbc_testdb
 * CREATE EXTENSION ltree;
 * </pre>
 *
 * To clear the data:
 *
 * <pre>
 * dropdb -W jdbc_test_db
 * createdb -O jdbc_test_login -W jdbc_test_db
 * psql -d jdbc_test_db
 * CREATE EXTENSION ltree;
 * </pre>
 *
 * To remove the test user:
 *
 * <pre>
 * dropuser -W jdbc_test_login
 * </pre>
 */
@Ignore( "This test needs a PostgreSQL instance running" )
public class PostgreSQLEntityStoreTest
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
        config.services( MemoryEntityStoreService.class );

        // START SNIPPET: assembly
        // DataSourceService
        new DBCPDataSourceServiceAssembler().
                identifiedBy( "postgresql-datasource-service" ).
                visibleIn( Visibility.module ).
                withConfig( config ).
                withConfigVisibility( Visibility.layer ).
                assemble( module );

        // DataSource
        new DataSourceAssembler().
                withDataSourceServiceIdentity( "postgresql-datasource-service" ).
                identifiedBy( "postgresql-datasource" ).
                visibleIn( Visibility.module ).
                withCircuitBreaker();

        // SQL EntityStore
        new PostgreSQLEntityStoreAssembler().
                visibleIn( Visibility.application ).
                withConfig( config ).
                withConfigVisibility( Visibility.layer ).
                assemble( module );
    }
    // END SNIPPET: assembly

    @Override
    public void tearDown()
            throws Exception
    {

        UnitOfWork uow = this.module.newUnitOfWork( UsecaseBuilder.newUsecase(
                "Delete " + getClass().getSimpleName() + " test data" ) );
        try {
            SQLConfiguration config = uow.get( SQLConfiguration.class,
                                               PostgreSQLEntityStoreAssembler.DEFAULT_ENTITYSTORE_IDENTITY );
            Connection connection = module.findService( DataSource.class ).get().getConnection();
            String schemaName = config.schemaName().get();
            if ( schemaName == null ) {
                schemaName = SQLs.DEFAULT_SCHEMA_NAME;
            }

            Statement stmt = null;
            try {
                stmt = connection.createStatement();
                stmt.execute( String.format( "DELETE FROM %s." + SQLs.TABLE_NAME, schemaName ) );
                connection.commit();
            } finally {
                SQLUtil.closeQuietly( stmt );
            }
        } finally {
            uow.discard();
            super.tearDown();
        }
    }

}
