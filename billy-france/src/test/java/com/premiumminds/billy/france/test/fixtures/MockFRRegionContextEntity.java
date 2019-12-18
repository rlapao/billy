/*
 * Copyright (C) 2017 Premium Minds.
 *
 * This file is part of billy france (FR Pack).
 *
 * billy france (FR Pack) is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * billy france (FR Pack) is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with billy france (FR Pack). If not, see <http://www.gnu.org/licenses/>.
 */
package com.premiumminds.billy.france.test.fixtures;

import com.premiumminds.billy.core.persistence.entities.ContextEntity;
import com.premiumminds.billy.core.services.entities.Context;
import com.premiumminds.billy.core.test.fixtures.MockContextEntity;
import com.premiumminds.billy.france.persistence.entities.FRRegionContextEntity;

public class MockFRRegionContextEntity extends MockContextEntity implements FRRegionContextEntity {

    private static final long serialVersionUID = 1L;

    public String regionCode;

    public MockFRRegionContextEntity() {
    }

    @Override
    public <T extends ContextEntity> void setParentContext(T parent) {
        super.setParentContext(parent);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Context getParentContext() {
        return super.getParentContext();
    }
}