package dk.bemyndigelsesregister.bemyndigelsesservice.server;

import com.trifork.dgws.WhitelistChecker;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public class WhitelistCheckerDefault implements WhitelistChecker {

    @Override
    public Set<String> getLegalCvrNumbers(String whitelist) {
        return null;
    }
}
