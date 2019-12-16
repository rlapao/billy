/*
 * Copyright (C) 2017 Premium Minds.
 *
 * This file is part of billy portugal Ebean (PT Pack).
 *
 * billy portugal Ebean (PT Pack) is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * billy portugal Ebean (PT Pack) is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with billy portugal Ebean (PT Pack). If not, see <http://www.gnu.org/licenses/>.
 */
package com.premiumminds.billy.portugal.services.builders;

import com.premiumminds.billy.core.services.builders.GenericInvoiceBuilder;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoice.SourceBilling;
import com.premiumminds.billy.portugal.services.entities.PTGenericInvoiceEntry;

public interface PTGenericInvoiceBuilder<TBuilder extends PTGenericInvoiceBuilder<TBuilder, TEntry, TDocument>, TEntry extends PTGenericInvoiceEntry, TDocument extends PTGenericInvoice>
        extends GenericInvoiceBuilder<TBuilder, TEntry, TDocument> {

    public TBuilder setCancelled(boolean cancelled);

    public TBuilder setBilled(boolean billed);

    public TBuilder setChangeReason(String reason);

    public TBuilder setSourceBilling(SourceBilling sourceBilling);

}
