package dk.bemyndigelsesregister.service;

import dk.bemyndigelsesregister.domain.Delegation;
import dk.bemyndigelsesregister.domain.ExpirationInfo;
import dk.bemyndigelsesregister.domain.Status;
import java.time.Instant;

import java.util.List;

/**
 * BEM 2.0 bemyndigelse
 */
public interface DelegationManager {
    /**
     * CreateDelegation kan både anvendes til at oprette godkendte og ikke-godkendte bemyndigelser.
     * Metoden overskriver en eksisterende bemyndigelse med samme nøgle, da der ikke kan
     * eksistere mere end én bemyndigelse på et bestemt tidspunkt med samme nøgle. Sletningen foregår
     * ved at sætte slutdato til det aktuelle tidspunkt. Hvis man kalder OpretBemyndigelser med status
     * Accepteret, og der er en eksisterende bemyndigelse med status Anmodet og ellers identiske værdier i
     * nøglen, så slettes denne, dvs. operationen svarer til en godkendelse af de anmodede bemyndigelser
     *
     * @param systemCode    IT-System, nøgleoplysning
     * @param delegatorCpr  Bemyndigende CPR, nøgleoplysning
     * @param delegateeCpr  Bemyndigede CPR, nøgleoplysning
     * @param delegateeCvr  Bemyndigede CVR, nøgleoplysning
     * @param roleCode      Arbejdsfunktion, nøgleoplysning
     * @param state         Anmodet / Godkendt, nøgleoplysning
     * @param permissions   De rettigheder, der gives bemyndigelse til
     * @param effectiveFrom Gyldigheds fradato
     * @param effectiveTo   Gyldigheds tildato
     * @return Data for bemyndigelse
     */
    Delegation createDelegation(String systemCode, String delegatorCpr, String delegateeCpr, String delegateeCvr, String roleCode, Status state, List<String> permissions, Instant effectiveFrom, Instant effectiveTo);


    /**
     * Henter bemyndigelser uddelegeret af en person
     *
     * @param cpr cprnr på delegerende person
     * @return liste af bemyndigelser
     */
    List<Delegation> getDelegationsByDelegatorCpr(String cpr);

    /**
     * Henter bemyndigelser uddelegeret af en person
     *
     * @param cpr cprnr på delegerende person
     * @param effectiveFrom start på periode, der anvendes til filtrering
     * @param effectiveTo slut på periode, der anvendes til filtrering
     * @return liste af bemyndigelser
     */
    List<Delegation> getDelegationsByDelegatorCpr(String cpr, Instant effectiveFrom, Instant effectiveTo);

    /**
     * Henter bemyndigelser uddelegeret til en person
     *
     * @param cpr cprnr på person
     * @return liste af bemyndigelser
     */
    List<Delegation> getDelegationsByDelegateeCpr(String cpr);

    /**
     * Henter bemyndigelser uddelegeret til en person
     *
     * @param cpr cprnr på person
     * @param effectiveFrom start på periode, der anvendes til filtrering
     * @param effectiveTo slut på periode, der anvendes til filtrering
     * @return liste af bemyndigelser
     */
    List<Delegation> getDelegationsByDelegateeCpr(String cpr, Instant effectiveFrom, Instant effectiveTo);


    /**
     * Henter bemyndigelser fra id
     *
     * @param id bemyndigelse id
     * @return Bemyndigelse
     */
    Delegation getDelegation(long id);

    /**
     * Henter bemyndigelser fra kode/uuid
     *
     * @param delegationCode bemyndigelse UUID
     * @return Bemyndigelse
     */
    Delegation getDelegation(String delegationCode);

    /**
     * deleteDelegation sletter bemyndigelsen med den angivne nøgle.
     * Sletningen foregår i praksis ved at sætte slutdato til det aktuelle tidspunkt.
     * Kan også bruges til at afvise en anmodning om bemyndigelse
     *
     * @param delegatorCpr   Bemyndigende CPR
     * @param delegateeCpr   Bemyndigede CPR
     * @param delegationCode kode på på bemyndigelse (uuid)
     * @param deletionDate   slutdato til bemyndigelse
     * @return delegationId
     */
    String deleteDelegation(String delegatorCpr, String delegateeCpr, String delegationCode, Instant deletionDate);

    ExpirationInfo getExpirationInfo(String delegatorCpr, int days);

    int cleanup(Instant beforeDate, int maxRecords);
}
