package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Delegation;
import dk.bemyndigelsesregister.bemyndigelsesservice.domain.Status;
import dk.nsi.bemyndigelse._2016._01._01.State;
import org.joda.time.DateTime;

import java.util.List;

/**
 * BEM 2.0 bemyndigelse
 * Created by obj on 02-02-2016.
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
    Delegation createDelegation(String systemCode, String delegatorCpr, String delegateeCpr, String delegateeCvr, String roleCode, Status state, List<String> permissions, DateTime effectiveFrom, DateTime effectiveTo);

    /**
     * Henter bemyndigelser uddelegeret af en person
     *
     * @param cpr cprnr på delegerende person
     * @return liste af bemyndigelser
     */
    List<Delegation> getDelegationsByDelegatorCpr(String cpr);

    /**
     * Henter bemyndigelser uddelegeret til en person
     *
     * @param cpr cprnr på person
     * @return liste af bemyndigelser
     */
    List<Delegation> getDelegationsByDelegateeCpr(String cpr);

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
     *
     * @param delegatorCpr   Bemyndigende CPR
     * @param delegateeCpr   Bemyndigede CPR
     * @param delegationCode kode på på bemyndigelse (uuid)
     * @param deletionDate   slutdato til bemyndigelse
     * @return delegationId
     */
    String deleteDelegation(String delegatorCpr, String delegateeCpr, String delegationCode, DateTime deletionDate);
}
