package dk.bemyndigelsesregister.batch;

import dk.bemyndigelsesregister.batch.exportmodel.Delegations;

import java.time.Instant;

public interface UploadManager {
    void upload(Delegations delegations, Instant startTime, int batchNo);
}
