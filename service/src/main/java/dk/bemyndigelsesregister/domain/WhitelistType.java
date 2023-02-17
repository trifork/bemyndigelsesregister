package dk.bemyndigelsesregister.domain;

public enum WhitelistType {
    /**
     * Whitelists a specific system by CVR number (VOCES)
     */
    SYSTEM_CVR,

    /**
     * Whitelists a user by CVR number and CPR number (MOCES)
     */
    USER_CVR_CPR,
}
