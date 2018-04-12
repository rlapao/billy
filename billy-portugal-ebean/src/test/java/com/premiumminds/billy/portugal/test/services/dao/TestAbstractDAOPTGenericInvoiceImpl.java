/**
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
package com.premiumminds.billy.portugal.test.services.dao;

import org.junit.Before;
import org.junit.Test;

import com.premiumminds.billy.core.persistence.dao.DAOBusiness;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.portugal.persistence.dao.ebean.AbstractDAOPTGenericInvoiceImpl;
import com.premiumminds.billy.portugal.persistence.dao.ebean.DAOPTBusinessImpl;
import com.premiumminds.billy.portugal.persistence.dao.ebean.DAOPTGenericInvoiceImpl;
import com.premiumminds.billy.portugal.persistence.entities.PTBusinessEntity;
import com.premiumminds.billy.portugal.persistence.entities.PTGenericInvoiceEntity;
import com.premiumminds.billy.portugal.persistence.entities.ebean.JPAPTBusinessEntity;
import com.premiumminds.billy.portugal.persistence.entities.ebean.JPAPTGenericInvoiceEntity;

import io.ebean.Ebean;
import junit.framework.Assert;

public class TestAbstractDAOPTGenericInvoiceImpl extends BaseH2Test {

    private static String correctInvoiceNumber = "11";

    private static String incorrectInvoiceNumber = "13";

    private static UID invoiceUID = new UID("5fe8d552-98e9-401e-9ad6-56648e787a86");

    private static UID existingBusinessUID = new UID("1796dc4d-462c-468c-9f0f-170b65944341");

    private static UID nonExistingBusinessUID = new UID("a413c9e9-f2de-4f4b-a937-a63d88504796");

    private static AbstractDAOPTGenericInvoiceImpl<PTGenericInvoiceEntity,
    JPAPTGenericInvoiceEntity> invoiceDAO;

    private static DAOBusiness businessDAO;

    @Before
    public void prepare() {
        Ebean.beginTransaction();
        invoiceDAO = new DAOPTGenericInvoiceImpl();
        businessDAO = new DAOPTBusinessImpl();

        PTBusinessEntity business = new JPAPTBusinessEntity();
        business.setUID(existingBusinessUID);

        PTGenericInvoiceEntity invoice = new JPAPTGenericInvoiceEntity();
        invoice.setUID(invoiceUID);
        invoice.setNumber(correctInvoiceNumber);
        invoice.setBusiness(business);

        businessDAO.create(business);
        invoiceDAO.create(invoice);
        Ebean.commitTransaction();
    }

    @Test
    public void findByNumber() {
        PTGenericInvoiceEntity invoice =
                invoiceDAO.findByNumber(existingBusinessUID, correctInvoiceNumber);

        Assert.assertEquals(invoice.getClass(), JPAPTGenericInvoiceEntity.class);
        Assert.assertEquals(invoice.getUID(), invoiceUID);
        Assert.assertEquals(invoice.getNumber(), correctInvoiceNumber);
    }

    @Test
    public void findByNumber_noSuchUid() {
        PTGenericInvoiceEntity invoice =
                invoiceDAO.findByNumber(nonExistingBusinessUID, correctInvoiceNumber);
        Assert.assertEquals(invoice, null);
    }

    @Test
    public void findByNumber_incorrectNumber() {
        PTGenericInvoiceEntity invoice =
                invoiceDAO.findByNumber(existingBusinessUID, incorrectInvoiceNumber);
        Assert.assertEquals(invoice, null);
    }
}
