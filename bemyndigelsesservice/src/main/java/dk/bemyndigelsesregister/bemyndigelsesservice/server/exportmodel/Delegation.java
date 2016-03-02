package dk.bemyndigelsesregister.bemyndigelsesservice.server.exportmodel;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * For data exported by ExportJob
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class Delegation {
    @XmlElement(name = "bemyndigede_cpr")
    protected String delegateeCpr;

    @XmlElement
    protected String system;

    @XmlElement
    protected String status;

    @XmlElement(name = "arbejdsfunktion")
    protected String role;

    @XmlElement(name = "rettighed")
    protected String permission;

    @XmlElement(name = "godkendelsesdato")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar approvalDate;

    @XmlElement(name = "ModifiedDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar modifiedDate;

    @XmlElement(name = "ValidFrom")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar effectiveFrom;

    @XmlElement(name = "ValidTo")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar effectiveTo;

    public Delegation() {
    }

    public Delegation(String delegateeCpr, String system, String status, String role, String permission, DateTime approvalDate, DateTime modifiedDate, DateTime effectiveFrom, DateTime effectiveTo) {
        this.delegateeCpr = delegateeCpr;
        this.system = system;
        this.status = status;
        this.role = role;
        this.permission = permission;
        this.approvalDate = toXmlGregorianCalendar(approvalDate);
        this.modifiedDate = toXmlGregorianCalendar(modifiedDate);
        this.effectiveFrom = toXmlGregorianCalendar(effectiveFrom);
        this.effectiveTo = toXmlGregorianCalendar(effectiveTo);
    }

    private XMLGregorianCalendar toXmlGregorianCalendar(DateTime dateTime) {
        if (dateTime == null)
            return null;
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime(dateTime, DateTimeZone.UTC).toGregorianCalendar());
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("An error occured trying to convert DateTime " + dateTime + " to XML", e);
        }
    }
}
