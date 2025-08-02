package vn.edu.fpt.transitlink.shared.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseSoftDeletableEntity extends BaseUserAuditableEntity {

    @Column(name = "is_deleted", nullable = false)
    protected boolean deleted = false;

    @Column(name = "deleted_at")
    protected OffsetDateTime deletedAt;

    @Column(name = "deleted_by")
    protected UUID deletedBy;

    public void softDelete(UUID userId) {
        this.deleted = true;
        this.deletedAt = OffsetDateTime.now(ZoneOffset.UTC);
        this.deletedBy = userId;
    }
}
