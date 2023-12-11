package dk.bemyndigelsesregister.ws;

import java.util.stream.Stream;

public enum Education {
    SOSU_HELPER("5147", "Social- og sundhedshjælper"),
    SOSU_ASSIST("5152", "Social- og sundhedsassistent"),
    NURSE("5166", "Sygeplejerske"),
    HEALTH_VISITOR("5168", "Sundhedsplejerske"),
    MIDWIFE("5175", "Jordemoder"),
    PHARMACONOMIST("5177", "Farmakonom"),
    PHARMACIST("5425", "Farmaceut"), // c.pharm.
    PHARMACIST_BACH("7420", "Farmaceut"), // bach.
    DENTIST("5433", "Tandlæge"),
    DOCTOR("7170", "Læge"),
    PHARMACIST_WITH_PRESCRIPTION_RIGHTS("B511", "Behandlerfarmaceut");

    private final String educationCode;
    private final String roleId;

    private Education(String educationCode, String roleId) {
        this.educationCode = educationCode;
        this.roleId = roleId;
    }

    public String getEducationCode() {
        return educationCode;
    }

    public String getRoleId() {
        return roleId;
    }

    public static Education fromEducationCode(String educationCode) {
        return Stream
                .of(values())
                .filter(education -> education.educationCode.equals(educationCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unexpected education code " + educationCode));
    }
}
