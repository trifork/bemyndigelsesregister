package dk.bemyndigelsesregister.domain;

public class Whitelist extends DomainObject {
    private WhitelistType whitelistType;
    private String name;
    private String subjectId;

    public WhitelistType getWhitelistType() {
        return whitelistType;
    }

    public void setWhitelistType(WhitelistType type) {
        this.whitelistType = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

}
