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

package org.qi4j.test.performance.runtime.service;

import junit.framework.TestCase;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.bootstrap.SingletonAssembler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.NumberFormat;

/**
 * JAVADOC
 */
public class ServiceInvocationPerformanceTest
    extends TestCase
{
    @Service
    ServiceInvocationPerformanceTest.MyService service;

    public void testInjectService()
        throws Exception
    {
        SingletonAssembler assembly = new SingletonAssembler()
        {
            public void assemble( ModuleAssembly module )
                throws AssemblyException
            {
                module.services( ServiceInvocationPerformanceTest.MyServiceComposite.class );
                module.objects( ServiceInvocationPerformanceTest.class );
            }
        };

        assembly.module().injectTo( this );

        // Warmup
        for( int i = 0; i < 60000; i++ )
        {
            service.test();
        }

        int rounds = 5;
        for( int i = 0; i < rounds; i++ )
        {
            performanceCheck( service );
        }
    }

    private void performanceCheck( MyService simple )
    {
        long count = 10000000L;

        long start = System.currentTimeMillis();
        for( long i = 0; i < count; i++ )
        {
            simple.test();
        }
        long end = System.currentTimeMillis();
        long time = end - start;
        long callsPerSecond = ( count / time ) * 1000;
        System.out.println( "Calls per second: " + NumberFormat.getIntegerInstance().format( callsPerSecond ) );
    }

    //    @Mixins( ServiceInvocationPerformanceTest.MyServiceMixin.class )

    @Mixins( NoopMixin.class )
    public static interface MyServiceComposite
        extends ServiceInvocationPerformanceTest.MyService, ServiceComposite
    {
    }

    public static interface MyService
    {
        void test();
    }

    public static class MyServiceMixin
        implements ServiceInvocationPerformanceTest.MyService
    {
        public void test()
        {
        }
    }

    public final static class NoopMixin
        implements InvocationHandler
    {
        public Object invoke( Object object, Method method, Object[] objects )
            throws Throwable
        {
            return null;
        }
    }
}
