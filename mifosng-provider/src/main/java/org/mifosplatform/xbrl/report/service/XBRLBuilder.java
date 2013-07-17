package org.mifosplatform.xbrl.report.service;


import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mifosplatform.xbrl.report.data.ContextData;
import org.mifosplatform.xbrl.taxonomy.data.TaxonomyData;
import org.springframework.beans.factory.annotation.Autowired;

public class XBRLBuilder {
	
	private static final String SCHEME_URL = "http://www.themix.org";
	private static final String IDENTIFIER = "000000";
	
	private Element root;
//	private HashMap<String,String> namespaceMap;
	private HashMap<ContextData,String> contextMap = new HashMap<ContextData,String>();
	private Date startDate;
	private Date endDate;
	private Integer instantScenarioCounter = 1;
	private Integer durationScenarioCounter = 1;
	
	@Autowired
	private ReadNamespaceService readNamespaceService;
	
	public String buildWithMap(Map<TaxonomyData,BigDecimal> map, Date startDate, Date endDate) {
		
		Document doc = DocumentHelper.createDocument();
		root = doc.addElement("xbrl");
		
		root.addElement("schemaRef")
			.addNamespace("link",
				"http://www.themix.org/sites/default/files/Taxonomy2010/dct/dc-all_2010-08-31.xsd");
		
		this.startDate = startDate;
		this.endDate = endDate;
		
		for (Entry<TaxonomyData,BigDecimal> entry : map.entrySet()) {
			TaxonomyData taxonomy = entry.getKey();
			BigDecimal value = entry.getValue();
			this.addTaxonomy(taxonomy, value);

		}
		
		this.addContexts();
		
		doc.setXMLEncoding("UTF-8");

		return doc.asXML();
	}
	
	public void addTaxonomy(TaxonomyData taxonomy, BigDecimal value) {
		Element xmlElement = root.addElement(taxonomy.getName());
//		String prefix = taxonomy.getNamespace();
//		
//		if (prefix != null && (!prefix.isEmpty())) {
//			NamespaceData ns = readNamespaceService.retrieveNamespaceByPrefix(prefix);
//			if (ns != null) {
//				xmlElement.addNamespace(prefix, ns.getUrl());
//			}
//			
//		}
		String dimension = taxonomy.getDimension();
		SimpleDateFormat timeFormat=new SimpleDateFormat("MM_dd_yyyy");
		
		ContextData context = null;
		if (dimension != null) {
			String[] dims = dimension.split("\\.");
			
			if (dims.length == 2) {
				context = new ContextData(dims[0],dims[1],taxonomy.getPeriod());
				if (contextMap.containsKey(context)) {
					
				} else {
					
				}
			}
		}
		
		if (context == null) {
			context = new ContextData(null,null,taxonomy.getPeriod());
		}
		
		if (!contextMap.containsKey(context)) {
			
			
			String contextRefID = (context.getPeriodType() == 0) ? ("As_Of_"+timeFormat.format(endDate)+(instantScenarioCounter++)) : ("Duration_"+timeFormat.format(startDate)+"_To_"+timeFormat.format(endDate)+(durationScenarioCounter++));

			contextMap.put(context, contextRefID);
		}
		

		xmlElement.addAttribute("contextRef", contextMap.get(context));
//		xmlElement.addAttribute("unitRef", getUnitRef(element));
		xmlElement.addAttribute("decimals", getNumberOfDecimalPlaces(value).toString());
		
		// add the child
		xmlElement.addText(value.toPlainString());

	}
	
	/**
	 * Adds the generic number unit
	 */
	void addNumberUnit() {
		Element numerUnit = root.addElement("unit");
//		numerUnit.addAttribute("id", numericUnit);
		Element measure = numerUnit.addElement("measure");
		measure.addText("xbrli:pure");

	}

	/**
	 * Adds the currency unit to the document
	 * 
	 * @param currencyCode
	 */
	public void addCurrencyUnit(String currencyCode) {
		Element currencyUnitElement = root.addElement("unit");
		currencyUnitElement.addAttribute("id", currencyCode);
		Element measure = currencyUnitElement.addElement("measure");
		measure.addText("iso4217:" + currencyCode);

	}
	
	public void addContexts() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		for (Entry<ContextData, String> entry : contextMap.entrySet()) {
			ContextData context = entry.getKey();
			Element contextElement = root.addElement("context");
			contextElement.addAttribute("id", entry.getValue());
			contextElement.addElement("entity")
				.addElement("identifier")
				.addAttribute("scheme", SCHEME_URL)
				.addText(IDENTIFIER);
			
			Element periodElement = contextElement.addElement("period")
										.addElement("identifier")
										.addText(IDENTIFIER);
			
			if (context.getPeriodType() == 0) {
				periodElement.addElement("instant").addText(format.format(endDate));
			} else {
				periodElement.addElement("startDate").addText(format.format(startDate));
				periodElement.addElement("endDate").addText(format.format(endDate));
			}
			
			String dimension = context.getDimension();
			String dimType = context.getDimensionType();
			if (dimType != null && dimension != null) {
				contextElement.addElement("scenario")
				.addElement("explicitMember")
//				.addNamespace("xbrldi", readNamespaceService.retrieveNamespaceByPrefix("xbrldi").getUrl())
				.addAttribute("dimension", dimType)
				.addText(dimension);
			}
		}
		
	}
	
	private Integer getNumberOfDecimalPlaces(BigDecimal bigDecimal) {
		String string = bigDecimal.stripTrailingZeros().toPlainString();
		int index = string.indexOf(".");
		return index < 0 ? 0 : string.length() - index - 1;
	}
}
