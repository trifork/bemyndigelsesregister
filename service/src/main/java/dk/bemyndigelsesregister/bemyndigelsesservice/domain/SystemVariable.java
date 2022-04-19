package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

import org.joda.time.DateTime;

import javax.persistence.Entity;

@Entity
public class SystemVariable extends DomainObject {
    private String name;
    private String value;

    public SystemVariable() {
    }

    public SystemVariable(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public SystemVariable(String name, long value) {
        this.name = name;
        setLongValue(value);
    }

    public SystemVariable(String name, DateTime value) {
        this.name = name;
        setDateTimeValue(value);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getLongValue() {
        return Long.parseLong(getValue());
    }

    public void setLongValue(long value) {
        setValue(String.valueOf(value));
    }

    public DateTime getDateTimeValue() {
        return new DateTime(getLongValue());
    }

    public void setDateTimeValue(DateTime value) {
        setLongValue(value.toInstant().getMillis());
    }
}
