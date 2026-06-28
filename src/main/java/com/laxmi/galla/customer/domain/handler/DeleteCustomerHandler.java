package com.laxmi.galla.customer.domain.handler;

import com.laxmi.galla.customer.internal.utils.ActionReasonUtils;
import com.laxmi.galla.customer.domain.event.CustomerDeletedEvent;
import com.laxmi.galla.customer.domain.entity.CustomerEntity;
import com.laxmi.galla.customer.domain.enums.AccountAction;
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

        String finalReason = ActionReasonUtils.buildReason(
                reason,
                AccountAction.DELETE.name(),
                performedBy
        );

        customer.markAsDeleted(performedBy);

        eventPublisher.publishEvent(
                CustomerDeletedEvent.of(
                        customer.getId().toString(),
                        performedBy,
                        finalReason)
        );
    }
}
