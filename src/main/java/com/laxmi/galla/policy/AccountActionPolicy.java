package com.laxmi.galla.policy;

import com.laxmi.galla.entity.CustomerEntity;
import com.laxmi.galla.enums.AccountAction;
import com.laxmi.galla.enums.AccountStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class AccountActionPolicy {

    private static final Map<AccountAction, Set<AccountStatus>> RULES = Map.of(
            AccountAction.DELETE, Set.of(AccountStatus.ACTIVE, AccountStatus.INACTIVE),
            AccountAction.BLOCK, Set.of(AccountStatus.ACTIVE),
            AccountAction.ACTIVATE, Set.of(AccountStatus.INACTIVE),
            AccountAction.DEACTIVATE, Set.of(AccountStatus.ACTIVE, AccountStatus.BLOCKED),
            AccountAction.RESTORE, Set.of(AccountStatus.BLOCKED)
    );

    public void validate(CustomerEntity customer, AccountAction action) {

        if (action == null) {
            throw new IllegalArgumentException("Action cannot be null");
        }

        if (customer.isDeleted() && action != AccountAction.RESTORE) {
            throw new IllegalStateException("Only RESTORE allowed for deleted customer");
        }

        Set<AccountStatus> allowed = RULES.get(action);

        if (allowed == null) {
            throw new IllegalStateException("Unsupported action: " + action);
        }

        if (!allowed.contains(customer.getStatus())) {
            throw new IllegalStateException(
                    "Action " + action + " not allowed in state " + customer.getStatus()
            );
        }
    }
}