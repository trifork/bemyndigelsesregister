package dk.bemyndigelsesregister.service;

import java.util.List;

public interface DumpRestoreManager {
    /**
     * Slet alle bemyndigelser for en eller flere personer
     *
     * @param identifiers - cprnumre for bemyndigende personer
     * @return cprnumre for de personer, det lykkedes at slette bemyndigelser for
     */
    List<String> resetPatients(List<String> identifiers);
}
