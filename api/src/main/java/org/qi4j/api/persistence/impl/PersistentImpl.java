/*
 * Copyright (c) 2007, Rickard Öberg. All Rights Reserved.
 * Copyright (c) 2007, Niclas Hedhman. All Rights Reserved.
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
package org.qi4j.api.persistence.impl;

import org.qi4j.api.persistence.Persistent;
import org.qi4j.api.EntityRepository;


/**
 * MixinModel that holds a reference to a persistent repository
 */
public final class PersistentImpl
    implements Persistent
{
    private EntityRepository persistentStorage;

    public void setEntityRepository( EntityRepository aStorage )
    {
        persistentStorage = aStorage;
    }

    public EntityRepository getEntityRepository()
    {
        return persistentStorage;
    }
}
