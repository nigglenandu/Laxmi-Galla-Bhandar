package com.laxmi.galla.customer.delete;

import com.laxmi.galla.entity.CustomerEntity;
import com.laxmi.galla.enums.AccountAction;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class DeleteCustomerHandler implements AccountActionHandler{
    private final ApplicationEventPublisher eventPublisher;

    public DeleteCustomerHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public AccountAction supports() {
        return AccountAction.DELETE;
    }

    @Override
    public void handle(CustomerEntity customer, String reason, String performedBy) {
        String finalReason = (reason != null && !reason.trim().isEmpty())
                ? reason.trim()
                : "Deleted by " + performedBy;

        customer.markAsDeleted(performedBy);

        eventPublisher.publishEvent(
                CustomerDeletedEvent.of(
                        customer.getId().toString(),
                        performedBy,
                        finalReason,
                        null)
        );
    }
}
