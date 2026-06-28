package com.laxmi.galla.customer.domain.handler;

import com.laxmi.galla.customer.internal.utils.ActionReasonUtils;
import com.laxmi.galla.customer.domain.event.CustomerDeactivatedEvent;
import com.laxmi.galla.customer.domain.entity.CustomerEntity;
import com.laxmi.galla.customer.domain.enums.AccountAction;
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
