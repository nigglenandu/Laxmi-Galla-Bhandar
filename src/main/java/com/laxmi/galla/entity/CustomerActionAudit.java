package com.laxmi.galla.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerActionAudit {
    private String blockReason;
    private String blockedBy;
    private Instant blockedAt;

    private String deactivatedReason;
    private String deactivatedBy;
    private Instant deactivatedAt;

    private String activatedReason;
    private String activatedBy;
    private Instant activatedAt;

    private String restoredReason;
    private String restoredBy;
    private Instant restoredAt;
}
