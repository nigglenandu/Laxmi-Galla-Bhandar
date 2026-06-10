package com.laxmi.galla.core.event;

import java.time.Instant;

/**
 * Common contract for all domain events.
 */
public interface DomainEvent {

    String aggregateId();

    Instant occurredAt();

    String eventType(); // String-based for maximum decoupling
}