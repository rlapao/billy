/**
 * Copyright (C) 2013 Premium Minds.
 *
 * This file is part of billy platypus (PT Pack).
 *
 * billy platypus (PT Pack) is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * billy platypus (PT Pack) is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with billy platypus (PT Pack). If not, see <http://www.gnu.org/licenses/>.
 */
package com.premiumminds.billy.portugal.persistence.dao.jpa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.portugal.persistence.dao.DAOPTInvoice;
import com.premiumminds.billy.portugal.persistence.entities.PTBusinessEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTInvoiceEntity;
import com.premiumminds.billy.portugal.persistence.entities.jpa.JPAPTInvoiceEntity;
import com.premiumminds.billy.portugal.persistence.entities.jpa.QJPAPTInvoiceEntity;

public class DAOPTInvoiceImpl extends DAOPTGenericInvoiceImpl implements
		DAOPTInvoice {

	@Inject
	public DAOPTInvoiceImpl(Provider<EntityManager> emProvider) {
		super(emProvider);
	}

	@Override
	public PTInvoiceEntity getEntityInstance() {
		return new JPAPTInvoiceEntity();
	}

	@Override
	protected Class<? extends JPAPTInvoiceEntity> getEntityClass() {
		return JPAPTInvoiceEntity.class;
	}

	public List<PTInvoiceEntity> getBusinessInvoicesForSAFTPT(UID uid,
			Date from, Date to) {

		QJPAPTInvoiceEntity invoice = QJPAPTInvoiceEntity.jPAPTInvoiceEntity;
		JPAQuery query = new JPAQuery(this.getEntityManager());
		PTBusinessEntity business = getBusinessEntity(uid);

		query.from(invoice);

		List<BooleanExpression> predicates = new ArrayList<BooleanExpression>();
		BooleanExpression invoiceBusiness = invoice.business.eq(business);
		predicates.add(invoiceBusiness);
		BooleanExpression active = invoice.active.eq(true);
		predicates.add(active);
		BooleanExpression valid = invoice.date.between(from, to);
		predicates.add(valid);

		for (BooleanExpression e : predicates) {
			query.where(e);
		}

		return checkEntityList(query.list(invoice), PTInvoiceEntity.class);
	}
}
