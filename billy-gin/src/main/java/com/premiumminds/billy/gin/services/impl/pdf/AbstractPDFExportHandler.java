package com.premiumminds.billy.gin.services.impl.pdf;

import java.io.File;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.premiumminds.billy.core.persistence.dao.DAOGenericInvoice;
import com.premiumminds.billy.core.persistence.entities.GenericInvoiceEntity;
import com.premiumminds.billy.core.persistence.entities.GenericInvoiceEntryEntity;
import com.premiumminds.billy.core.services.UID;
import com.premiumminds.billy.core.services.entities.Tax;
import com.premiumminds.billy.core.services.entities.Tax.TaxRateType;
import com.premiumminds.billy.core.util.BillyMathContext;
import com.premiumminds.billy.gin.services.ExportServiceHandler;
import com.premiumminds.billy.gin.services.ExportServiceRequest;
import com.premiumminds.billy.gin.services.exceptions.ExportServiceException;
import com.premiumminds.billy.gin.services.export.IBillyTemplateBundle;
import com.premiumminds.billy.gin.services.export.ParamsTree;
import com.premiumminds.billy.gin.services.export.ParamsTree.Node;
import com.premiumminds.billy.gin.services.export.pdf.AbstractPDFHandler;

public abstract class AbstractPDFExportHandler extends AbstractPDFHandler implements ExportServiceHandler{
	
	protected static class ParamKeys {

		public static final String ROOT = "invoice";

		public static final String INVOICE_PAYMETHOD = "paymentMechanism";
		public static final String ID = "id";
		public static final String EMISSION_DATE = "emissionDate";
		public static final String DUE_DATE = "dueDate";
		public static final String TOTAL_BEFORE_TAX = "totalBeforeTax";
		public static final String TOTAL_TAX = "totalTax";
		public static final String TOTAL = "totalPrice";
		//=========Only for invoices=========
		public static final String INVOICE_PAYSETTLEMENT = "paymentSettlement";
		//==================================
		
		//=========Only for CreditNotes======
		public static final String INVOICE = "invoice";
		//==================================
		
		public static final String BUSINESS = "business";
		public static final String BUSINESS_LOGO = "logoPath";
		public static final String BUSINESS_NAME = "name";
		public static final String BUSINESS_FINANCIAL_ID = "financialId";
		public static final String BUSINESS_ADDRESS = "address";
		public static final String BUSINESS_ADDRESS_COUNTRY = "country";
		public static final String BUSINESS_ADDRESS_DETAILS = "details";
		public static final String BUSINESS_ADDRESS_CITY = "city";
		public static final String BUSINESS_ADDRESS_REGION = "region";
		public static final String BUSINESS_ADDRESS_POSTAL_CODE = "postalcode";

		public static final String BUSINESS_CONTACTS = "contacts";
		public static final String BUSINESS_PHONE = "phNo";
		public static final String BUSINESS_FAX = "faxNo";
		public static final String BUSINESS_EMAIL = "email";

		public static final String CUSTOMER = "customer";
		public static final String CUSTOMER_ID = "id";
		public static final String CUSTOMER_NAME = "name";
		public static final String CUSTOMER_FINANCIAL_ID = "financialId";

		public static final String CUSTOMER_BILLING_ADDRESS = "address";
		public static final String CUSTOMER_BILLING_ADDRESS_COUNTRY = "country";
		public static final String CUSTOMER_BILLING_ADDRESS_DETAILS = "details";
		public static final String CUSTOMER_BILLING_ADDRESS_CITY = "city";
		public static final String CUSTOMER_BILLING_ADDRESS_REGION = "region";
		public static final String CUSTOMER_BILLING_ADDRESS_POSTAL_CODE = "postalcode";

		public static final String ENTRIES = "entries";
		public static final String ENTRY = "entry";
		public static final String ENTRY_ID = "id";
		public static final String ENTRY_DESCRIPTION = "description";
		public static final String ENTRY_QUANTITY = "qty";
		// public static final String PRODUCT_DISCOUNT = "entries";
		public static final String ENTRY_UNIT_PRICE = "unitPrice";
		public static final String ENTRY_TOTAL = "total";
		public static final String ENTRY_TAX = "tax";

		public static final String TAX_DETAILS = "taxDetails";
		public static final String TAX_DETAIL = "detail";
		public static final String TAX_DETAIL_TAX = "tax";
		public static final String TAX_DETAIL_NET_VALUE = "baseValue";
		public static final String TAX_DETAIL_VALUE = "taxValue";
	}
	
	private DAOGenericInvoice daoGenericInvoice;
	protected MathContext mc =BillyMathContext.get();
	
	@Inject
	public AbstractPDFExportHandler(DAOGenericInvoice daoGenericInvoice){
		this.daoGenericInvoice = daoGenericInvoice;
	}
	
	public File toFile(URI fileURI, GenericInvoiceEntity invoice,
			IBillyTemplateBundle bundle) throws ExportServiceException {
		return super.toFile(fileURI, bundle.getXSLTFileStream(),
				this.mapDocumentToParamsTree(invoice, bundle), bundle);
	}
	
	protected void toStream(GenericInvoiceEntity invoice,
			OutputStream targetStream, IBillyTemplateBundle bundle)
			throws ExportServiceException {
		super.getStream(bundle.getXSLTFileStream(),
				this.mapDocumentToParamsTree(invoice, bundle), targetStream,
				bundle);
	}
	
	protected ParamsTree<String, String> mapDocumentToParamsTree(
			GenericInvoiceEntity document, IBillyTemplateBundle bundle) {

		ParamsTree<String, String> params = new ParamsTree<String, String>(
				ParamKeys.ROOT);
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
	
		params.getRoot().addChild(ParamKeys.ID, document.getNumber());
		
		setFields(params, document, bundle);
		
		setBussiness(params, document, bundle);

		if (null != document.getPaymentMechanism()) {
			params.getRoot().addChild(
					ParamKeys.INVOICE_PAYMETHOD,
					getPaymentMechanismTranslation((document.getPaymentMechanism())));
		}

		params.getRoot().addChild(ParamKeys.EMISSION_DATE,
				date.format(document.getDate()));
		
		if (null != document.getSettlementDate()) {
			params.getRoot().addChild(ParamKeys.DUE_DATE,
					date.format(document.getSettlementDate()));
		}

		setCustomer(params, document, bundle);
		
		

		TaxTotals taxTotals = new TaxTotals();
		
		Node<String, String> entries = params.getRoot().addChild(
				ParamKeys.ENTRIES);
		
		setEntries(taxTotals, entries, document);
		

		Node<String, String> taxDetails = params.getRoot().addChild(
				ParamKeys.TAX_DETAILS);
		
		setTaxDetails(taxTotals, taxDetails);
		

		params.getRoot().addChild(
				ParamKeys.TOTAL_BEFORE_TAX,
				document.getAmountWithoutTax().setScale(2, mc.getRoundingMode())
						.toPlainString());
		params.getRoot().addChild(
				ParamKeys.TOTAL_TAX,
				document.getTaxAmount().setScale(2, mc.getRoundingMode())
						.toPlainString());
		params.getRoot().addChild(
				ParamKeys.TOTAL,
				document.getAmountWithTax().setScale(2, mc.getRoundingMode())
						.toPlainString());
		return params;
	}
	
	protected void setTaxDetails(TaxTotals taxTotals, Node<String, String> taxDetails){
		
		for (TaxTotals.TaxTotalEntry taxDetail : taxTotals.getEntries()) {
			Node<String, String> taxDetailNode = taxDetails
					.addChild(ParamKeys.TAX_DETAIL);
			taxDetailNode.addChild(ParamKeys.TAX_DETAIL_TAX, taxDetail
					.getTaxValue().setScale(2, mc.getRoundingMode())
					.toPlainString()
					+ (taxDetail.isPercentage() ? "%" : "&#8364;"));
			taxDetailNode.addChild(ParamKeys.TAX_DETAIL_NET_VALUE, taxDetail
					.getNetValue().setScale(2, mc.getRoundingMode())
					.toPlainString());
			taxDetailNode.addChild(ParamKeys.TAX_DETAIL_VALUE, taxDetail
					.getAppliedTaxValue().setScale(2, mc.getRoundingMode())
					.toPlainString());
		}
		return;
	}

	
	protected <T extends GenericInvoiceEntity> void setEntries(TaxTotals taxTotals, Node<String, String> entries, T document){
		

		List<GenericInvoiceEntryEntity> genericInvoiceList = document
				.getEntries();
		for (GenericInvoiceEntryEntity entry : genericInvoiceList) {
			Node<String, String> entryNode = entries.addChild(ParamKeys.ENTRY);
			entryNode.addChild(ParamKeys.ENTRY_ID, entry.getProduct()
					.getProductCode());
			entryNode.addChild(ParamKeys.ENTRY_DESCRIPTION, entry.getProduct()
					.getDescription());
			entryNode.addChild(ParamKeys.ENTRY_QUANTITY, entry.getQuantity()
					.setScale(2, mc.getRoundingMode()).toPlainString());
			entryNode.addChild(ParamKeys.ENTRY_UNIT_PRICE, entry
					.getUnitAmountWithTax().setScale(2, mc.getRoundingMode())
					.toPlainString());
			entryNode.addChild(ParamKeys.ENTRY_TOTAL, entry.getAmountWithTax()
					.setScale(2, mc.getRoundingMode()).toPlainString());

			List<Tax> taxList = entry.getTaxes();
			for (Tax tax : taxList) {
				entryNode
						.addChild(
								ParamKeys.ENTRY_TAX,
								tax.getValue()
										+ (tax.getTaxRateType() == TaxRateType.PERCENTAGE ? "%"
												: "&#8364;"));
				taxTotals
						.add((tax.getTaxRateType() == TaxRateType.PERCENTAGE ? true
								: false), tax.getValue(), entry
								.getAmountWithoutTax(), tax.getUID().toString());
			}
		}
	}
	
	protected void setBussiness(ParamsTree<String, String> params, GenericInvoiceEntity document, IBillyTemplateBundle bundle){
		Node<String, String> businessInfo = params.getRoot().addChild(
				ParamKeys.BUSINESS);
		businessInfo.addChild(ParamKeys.BUSINESS_LOGO,
				bundle.getLogoImagePath());

		businessInfo.addChild(ParamKeys.BUSINESS_NAME, document.getBusiness()
				.getName());
		businessInfo.addChild(ParamKeys.BUSINESS_FINANCIAL_ID, document
				.getBusiness().getFinancialID());

		Node<String, String> businessAddress = businessInfo
				.addChild(ParamKeys.BUSINESS_ADDRESS);

		businessAddress.addChild(ParamKeys.BUSINESS_ADDRESS_COUNTRY, document
				.getBusiness().getAddress().getISOCountry());
		businessAddress.addChild(ParamKeys.BUSINESS_ADDRESS_DETAILS, document
				.getBusiness().getAddress().getDetails());
		businessAddress.addChild(ParamKeys.BUSINESS_ADDRESS_CITY, document
				.getBusiness().getAddress().getCity());
		businessAddress.addChild(ParamKeys.BUSINESS_ADDRESS_REGION, document
				.getBusiness().getAddress().getRegion());
		businessAddress.addChild(ParamKeys.BUSINESS_ADDRESS_POSTAL_CODE,
				document.getBusiness().getAddress().getPostalCode());

		Node<String, String> businessContacts = businessInfo
				.addChild(ParamKeys.BUSINESS_CONTACTS);
		businessContacts.addChild(ParamKeys.BUSINESS_PHONE, document
				.getBusiness().getMainContact().getTelephone());
		businessContacts.addChild(ParamKeys.BUSINESS_FAX, document.getBusiness()
				.getMainContact().getFax());
		businessContacts.addChild(ParamKeys.BUSINESS_EMAIL, document
				.getBusiness().getMainContact().getEmail());
		
		return;
	}
	
	protected void setCustomer(ParamsTree<String, String> params, GenericInvoiceEntity document, IBillyTemplateBundle bundle){
		Node<String, String> customer = params.getRoot().addChild(
				ParamKeys.CUSTOMER);
		customer.addChild(ParamKeys.CUSTOMER_ID, "");
		customer.addChild(ParamKeys.CUSTOMER_NAME, document.getCustomer()
				.getName());
		customer.addChild(ParamKeys.CUSTOMER_FINANCIAL_ID, getCustomerFinancialId(document, bundle));

		if (document.getCustomer().getBillingAddress() != null) {
			Node<String, String> customerAddress = customer
					.addChild(ParamKeys.CUSTOMER_BILLING_ADDRESS);
			customerAddress.addChild(
					ParamKeys.CUSTOMER_BILLING_ADDRESS_COUNTRY, document
							.getCustomer().getBillingAddress().getISOCountry());
			customerAddress.addChild(
					ParamKeys.CUSTOMER_BILLING_ADDRESS_DETAILS, document
							.getCustomer().getBillingAddress().getDetails());
			customerAddress.addChild(ParamKeys.CUSTOMER_BILLING_ADDRESS_CITY,
					document.getCustomer().getBillingAddress().getCity());
			customerAddress.addChild(ParamKeys.CUSTOMER_BILLING_ADDRESS_REGION,
					document.getCustomer().getBillingAddress().getRegion());
			customerAddress.addChild(
					ParamKeys.CUSTOMER_BILLING_ADDRESS_POSTAL_CODE, document
							.getCustomer().getBillingAddress().getPostalCode());
		}
		return;
	}

	protected class TaxTotals {

		Map<String, TaxTotalEntry> entries;

		private class TaxTotalEntry {

			BigDecimal baseValue;
			BigDecimal taxValue;
			Boolean percentageValued;

			public TaxTotalEntry(boolean perc, BigDecimal taxValue,
					BigDecimal baseValue) {
				this.baseValue = baseValue;
				this.taxValue = taxValue;
				this.percentageValued = perc;
			}

			public BigDecimal getNetValue() {
				return this.baseValue;
			}

			public BigDecimal getTaxValue() {
				return this.taxValue;
			}

			public boolean isPercentage() {
				return this.percentageValued;
			}

			public void addBaseValue(BigDecimal val) {
				this.baseValue = this.baseValue.add(val);
			}

			public BigDecimal getAppliedTaxValue() {
				BigDecimal appliedTaxVal;

				if (percentageValued) {
					BigDecimal tax = taxValue.divide(new BigDecimal("100"));
					appliedTaxVal = baseValue.multiply(tax);
				} else {
					appliedTaxVal = taxValue;
				}
				return appliedTaxVal;
			}
		}

		public TaxTotals() {
			entries = new HashMap<String, TaxTotalEntry>();
		}

		public void add(boolean isPercentage, BigDecimal taxValue,
				BigDecimal baseValue, String taxUid) {
			TaxTotalEntry currentEntry = new TaxTotalEntry(isPercentage,
					taxValue, baseValue);
			if (entries.containsKey(taxUid)) {
				this.entries.get(taxUid).addBaseValue(baseValue);
			} else {
				entries.put(taxUid, currentEntry);
			}
		}

		public Collection<TaxTotalEntry> getEntries() {
			return entries.values();
		}
	}

	public <T extends ExportServiceRequest> void export(T request,
			OutputStream targetStream) throws ExportServiceException {

		if (!(request instanceof AbstractExportRequest)) {
			throw new ExportServiceException("Cannot handle request of type "
					+ request.getClass().getCanonicalName());
		}
		AbstractExportRequest exportRequest = (AbstractExportRequest) request;
		UID docUid = exportRequest.getDocumentUID();

		try {
			GenericInvoiceEntity invoice = (GenericInvoiceEntity) daoGenericInvoice
					.get(docUid);
			this.toStream(invoice, targetStream, exportRequest.getBundle());
		} catch (Exception e) {
			throw new ExportServiceException(e);
		}
	}
	
	public abstract <T extends IBillyTemplateBundle, K extends GenericInvoiceEntity> void setFields(ParamsTree<String, String> params, K document, T bundle);
	
	public abstract String getPaymentMechanismTranslation(Enum<?> pmc);
	
	public abstract <T extends IBillyTemplateBundle, K extends GenericInvoiceEntity> String getCustomerFinancialId(K document, T bundle);
}
