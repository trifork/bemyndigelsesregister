package dk.bemyndigelsesregister.domain;

public enum Status {
    ANMODET("Anmodet"), GODKENDT("Godkendt");

    private final String value;

    Status(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Status fromValue(String v) {
        for (Status c : Status.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
