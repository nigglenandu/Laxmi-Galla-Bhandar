package com.laxmi.galla.customer.delete;

import com.laxmi.galla.entity.CustomerEntity;
import com.laxmi.galla.enums.AccountAction;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class DeactivateCustomerHandler implements AccountActionHandler {

    private final ApplicationEventPublisher eventPublisher;

    public DeactivateCustomerHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public AccountAction supports() {
        return AccountAction.DEACTIVATE;
    }

    @Override
    public void handle(CustomerEntity customer, String reason, String performedBy) {
        String finalReason = ActionReasonUtils.buildReason(
                reason,
                AccountAction.DEACTIVATE.name(),
                performedBy
        );

        customer.deactivate(finalReason, performedBy);

        eventPublisher.publishEvent(
                CustomerDeactivatedEvent.of(
                        customer.getId().toString(),
                        performedBy,
                        finalReason
                )
        );
    }
}
