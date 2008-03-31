/*
 * Copyright (c) 2007-2008, Rickard Öberg. All Rights Reserved.
 * Copyright (c) 2007-2008, Niclas Hedhman. All Rights Reserved.
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

package org.qi4j.library.auth;

import org.qi4j.composite.Composite;
import org.qi4j.composite.Mixins;
import org.qi4j.library.framework.entity.AssociationMixin;
import org.qi4j.library.framework.entity.PropertyMixin;
import org.qi4j.entity.EntityComposite;

/**
 * TODO
 */
@Mixins( { PropertyMixin.class, AssociationMixin.class } )
public interface RoleEntity extends EntityComposite, Role
{
}
