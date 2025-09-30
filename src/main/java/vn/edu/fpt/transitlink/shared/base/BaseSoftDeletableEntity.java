package vn.edu.fpt.transitlink.shared.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@FilterDef(name = "deletedFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
@Filter(name = "deletedFilter", condition = "is_deleted = :isDeleted")
public abstract class BaseSoftDeletableEntity extends BaseUserAuditableEntity {

    @Column(name = "is_deleted", nullable = false)
    protected boolean isDeleted = false;

    @Column(name = "deleted_at")
    protected OffsetDateTime deletedAt;

    @Column(name = "deleted_by")
    protected UUID deletedBy;

    public void softDelete(UUID userId) {
        this.isDeleted = true;
        this.deletedAt = OffsetDateTime.now(ZoneOffset.UTC);
        this.deletedBy = userId;
    }

    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
        this.deletedBy = null;
    }
}
