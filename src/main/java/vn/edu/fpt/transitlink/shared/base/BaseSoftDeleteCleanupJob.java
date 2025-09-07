package vn.edu.fpt.transitlink.shared.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public abstract class BaseSoftDeleteCleanupJob<T extends BaseSoftDeletableEntity, ID> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected abstract SoftDeletableRepository<T, ID> getRepository();

    protected abstract Duration getRetentionDuration();

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanup() {
        OffsetDateTime threshold = OffsetDateTime.now(ZoneOffset.UTC).minus(getRetentionDuration());
        int deletedCount = getRepository().hardDeleteSoftDeletedBefore(threshold);
        log.info("Soft-delete cleanup: deleted {} records older than {}", deletedCount, threshold);
    }
}
