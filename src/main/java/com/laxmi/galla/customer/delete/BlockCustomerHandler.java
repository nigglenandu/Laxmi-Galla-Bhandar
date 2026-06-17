package com.laxmi.galla.customer.delete;

import com.laxmi.galla.entity.CustomerEntity;
import com.laxmi.galla.enums.AccountAction;
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
