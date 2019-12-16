/*
 * Copyright (C) 2017 Premium Minds.
 *
 * This file is part of billy spain (ES Pack).
 *
 * billy spain (ES Pack) is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * billy spain (ES Pack) is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with billy spain (ES Pack). If not, see <http://www.gnu.org/licenses/>.
 */
package com.premiumminds.billy.spain.services.builders;

import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.spain.services.entities.ESCreditReceiptEntry;

public interface ESCreditReceiptEntryBuilder<TBuilder extends ESCreditReceiptEntryBuilder<TBuilder, TEntry>, TEntry extends ESCreditReceiptEntry>
        extends ESGenericInvoiceEntryBuilder<TBuilder, TEntry> {

    public TBuilder setReferenceUID(UID reference);

    public TBuilder setReason(String reason);

}
