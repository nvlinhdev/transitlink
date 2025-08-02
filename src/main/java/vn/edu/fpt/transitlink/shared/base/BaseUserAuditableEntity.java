package vn.edu.fpt.transitlink.shared.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseUserAuditableEntity extends BaseTimeAuditableEntity {

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    protected UUID createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    protected UUID updatedBy;
}

