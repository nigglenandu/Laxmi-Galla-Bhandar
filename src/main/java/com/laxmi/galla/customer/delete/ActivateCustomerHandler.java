package com.laxmi.galla.customer.delete;

import com.laxmi.galla.entity.CustomerEntity;
import com.laxmi.galla.enums.AccountAction;
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
                CustomerActivaedEvent.of(customer.getId().toString(),
                        performedBy,
                        finalReason)
        );
    }
}
