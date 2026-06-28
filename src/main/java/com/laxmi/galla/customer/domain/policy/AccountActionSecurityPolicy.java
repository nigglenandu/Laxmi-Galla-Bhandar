package com.laxmi.galla.customer.domain.policy;

import com.laxmi.galla.customer.domain.enums.AccountAction;
import com.laxmi.galla.security.enums.Role;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class AccountActionSecurityPolicy {

    private static final Map<AccountAction, Set<Role>> RULES = Map.of(

            AccountAction.DELETE, Set.of(Role.ADMIN),

            AccountAction.BLOCK, Set.of(Role.ADMIN),

            AccountAction.RESTORE, Set.of(Role.ADMIN),

            AccountAction.ACTIVATE, Set.of(Role.ADMIN, Role.CUSTOMER),

            AccountAction.DEACTIVATE, Set.of(Role.ADMIN, Role.CUSTOMER)
    );

    public void validate(AccountAction action, Role role, boolean isSelfAction) {

        Set<Role> allowedRoles = RULES.get(action);

        if (allowedRoles == null) {
            throw new IllegalStateException("Unsupported action: " + action);
        }

        // CUSTOMER can only act on self
        if (role == Role.CUSTOMER && !isSelfAction) {
            throw new IllegalStateException("Customer can only modify own account");
        }

        if (!allowedRoles.contains(role)) {
            throw new IllegalStateException(role + " not allowed for " + action);
        }
    }
}