package com.laxmi.galla.customer.domain.handler;

import com.laxmi.galla.customer.internal.utils.ActionReasonUtils;
import com.laxmi.galla.customer.domain.event.CustomerBlockedEvent;
import com.laxmi.galla.customer.domain.entity.CustomerEntity;
import com.laxmi.galla.customer.domain.enums.AccountAction;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class BlockCustomerHandler implements AccountActionHandler {

    private final ApplicationEventPublisher eventPublisher;

    public BlockCustomerHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public AccountAction supports() {
        return AccountAction.BLOCK;
    }

    @Override
    public void handle(CustomerEntity customer, String reason, String performedBy) {
        String finalReason = ActionReasonUtils.buildReason(
                reason,
                AccountAction.BLOCK.name(),
                performedBy
        );

        customer.block(finalReason, performedBy);

        eventPublisher.publishEvent(
                CustomerBlockedEvent.of(customer.getId().toString(),
                        performedBy,
                        finalReason
                )
        );
    }
}
