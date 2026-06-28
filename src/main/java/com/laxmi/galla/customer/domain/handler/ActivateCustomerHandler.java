package com.laxmi.galla.customer.domain.handler;

import com.laxmi.galla.customer.internal.utils.ActionReasonUtils;
import com.laxmi.galla.customer.domain.event.CustomerActivatedEvent;
import com.laxmi.galla.customer.domain.entity.CustomerEntity;
import com.laxmi.galla.customer.domain.enums.AccountAction;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ActivateCustomerHandler implements AccountActionHandler {
    private final ApplicationEventPublisher eventPublisher;

    public ActivateCustomerHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public AccountAction supports() {
        return AccountAction.ACTIVATE;
    }

    @Override
    public void handle(CustomerEntity customer, String reason, String performedBy) {
        String finalReason = ActionReasonUtils.buildReason(
                reason,
                AccountAction.ACTIVATE.name(),
                performedBy
        );

        customer.activate(finalReason, performedBy);

        eventPublisher.publishEvent(
                CustomerActivatedEvent.of(customer.getId().toString(),
                        performedBy,
                        finalReason)
        );
    }
}
