package com.premiumminds.billy.portugal.services.builders.impl;

import com.premiumminds.billy.core.exceptions.BillyValidationException;
import com.premiumminds.billy.core.services.builders.impl.GenericInvoiceBuilderImpl;
import com.premiumminds.billy.core.util.BillyValidator;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTBusiness;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTCustomer;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTInvoice;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTSupplier;
import com.premiumminds.billy.portugal.persistence.entities.PTInvoiceEntity;
import com.premiumminds.billy.portugal.services.builders.PTInvoiceBuilder;
import com.premiumminds.billy.portugal.services.entities.PTInvoice;
import com.premiumminds.billy.portugal.services.entities.PTInvoiceEntry;

public class PTInvoiceBuilderImpl<TBuilder extends PTInvoiceBuilderImpl<TBuilder, TEntry, TDocument>, TEntry extends PTInvoiceEntry, TDocument extends PTInvoice>
		extends GenericInvoiceBuilderImpl<TBuilder, TEntry, TDocument>
		implements PTInvoiceBuilder<TBuilder, TEntry, TDocument> {

	public PTInvoiceBuilderImpl(DAOPTInvoice daoPTInvoice,
			DAOPTBusiness daoPTBusiness, DAOPTCustomer daoPTCustomer,
			DAOPTSupplier daoPTSupplier) {
		super(daoPTInvoice, daoPTBusiness, daoPTCustomer, daoPTSupplier);
	}

	@Override
	public TBuilder setSelfBilled(boolean selfBilled) {
		BillyValidator.mandatory(selfBilled,
				GenericInvoiceBuilderImpl.LOCALIZER
						.getString("field.self_billed"));
		this.getTypeInstance().setSelfBilled(selfBilled);
		return this.getBuilder();
	}

	@Override
	public TBuilder setSourceId(String source) {
		BillyValidator.mandatory(source,
				GenericInvoiceBuilderImpl.LOCALIZER.getString("field.source"));
		return this.getBuilder();
	}

	@Override
	protected PTInvoiceEntity getTypeInstance() {
		return (PTInvoiceEntity) super.getTypeInstance();
	}

	@Override
	protected void validateInstance() throws BillyValidationException {
		super.validateInstance();
	}
}
