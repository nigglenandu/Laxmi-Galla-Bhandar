package com.laxmi.galla.customer.domain.handler;

import com.laxmi.galla.customer.internal.utils.ActionReasonUtils;
import com.laxmi.galla.customer.domain.event.CustomerRestoredEvent;
import com.laxmi.galla.customer.domain.entity.CustomerEntity;
import com.laxmi.galla.customer.domain.enums.AccountAction;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class RestoreCustomerHandler implements AccountActionHandler{
   private final ApplicationEventPublisher eventPublisher;

   public RestoreCustomerHandler(ApplicationEventPublisher eventPublisher){
       this.eventPublisher = eventPublisher;
   }

    @Override
    public AccountAction supports() {
        return AccountAction.RESTORE;
    }

    @Override
    public void handle(CustomerEntity customer, String reason, String performedBy) {
        String finalReason = ActionReasonUtils.buildReason(
                reason,
                AccountAction.RESTORE.name(),
                performedBy
        );

        customer.restore(finalReason, performedBy);

        eventPublisher.publishEvent(
                CustomerRestoredEvent.of(customer.getId().toString(),
                        performedBy,
                        finalReason)
        );
    }
}
