package dk.bemyndigelsesregister.dao;

import java.util.List;

public final class TestData {
    public static final String permissionCode1 = "R01";
    public static final String permissionDescription1 = "Laegemiddelordination";

    public static final String permissionCode2 = "R02";
    public static final String permissionDescription2 = "Testrettighed2";

    public static final String systemCode = "testsys";
    public static final String systemDescription = "Trifork test system";

    public static final String roleCode = "Laege";
    public static final String roleDescription = "For unit test only";
    public static final List<String> roleEducationCodes = List.of("TestCode1", "TestCode2");

    public static final String domainCode = "Trifork";
}
