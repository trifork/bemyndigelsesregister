package dk.bemyndigelsesregister.xmlschema;

import com.trifork.xmlquery.Namespaces;

public class BemyndigelseNamespaces {

	public static Namespaces getNamespaces() {
		Namespaces namespaces = Namespaces.getOIONamespaces();
		namespaces.addNamespace("bms20170801", "http://nsi.dk/bemyndigelse/2017/08/01/");
		namespaces.addNamespace("cpr", "http://rep.oio.dk/cpr.dk/xml/schemas/core/2005/03/18/");
		namespaces.addNamespace("cvr", "http://rep.oio.dk/cvr.dk/xml/schemas/2005/03/22/");
		namespaces.addNamespace("medcom", "http://www.medcom.dk/dgws/2006/04/dgws-1.0.xsd");
		namespaces.addNamespace("xs", "http://www.w3.org/2001/XMLSchema");
		return namespaces;
	}
}
