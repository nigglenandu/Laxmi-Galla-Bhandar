package com.laxmi.galla.customer.delete;

import com.laxmi.galla.entity.CustomerEntity;
import com.laxmi.galla.enums.AccountAction;
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
