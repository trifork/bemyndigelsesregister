package dk.bemyndigelsesregister.bemyndigelsesservice.domain;

public class ExpirationInfo {
    private String delegatorCpr;
    private int delegationCount;
    private int delegateeCount;
    private int daysToFirstExpiration;
    private int firstExpiryDelegationCount;
    private int firstExpiryDelegateeCount;

    public String getDelegatorCpr() {
        return delegatorCpr;
    }

    public void setDelegatorCpr(String delegatorCpr) {
        this.delegatorCpr = delegatorCpr;
    }

    public int getDelegationCount() {
        return delegationCount;
    }

    public void setDelegationCount(int delegationCount) {
        this.delegationCount = delegationCount;
    }

    public int getDelegateeCount() {
        return delegateeCount;
    }

    public void setDelegateeCount(int delegateeCount) {
        this.delegateeCount = delegateeCount;
    }

    public int getDaysToFirstExpiration() {
        return daysToFirstExpiration;
    }

    public void setDaysToFirstExpiration(int daysToFirstExpiration) {
        this.daysToFirstExpiration = daysToFirstExpiration;
    }

    public int getFirstExpiryDelegationCount() {
        return firstExpiryDelegationCount;
    }

    public void setFirstExpiryDelegationCount(int firstExpiryDelegationCount) {
        this.firstExpiryDelegationCount = firstExpiryDelegationCount;
    }

    public int getFirstExpiryDelegateeCount() {
        return firstExpiryDelegateeCount;
    }

    public void setFirstExpiryDelegateeCount(int firstExpiryDelegateeCount) {
        this.firstExpiryDelegateeCount = firstExpiryDelegateeCount;
    }
}
