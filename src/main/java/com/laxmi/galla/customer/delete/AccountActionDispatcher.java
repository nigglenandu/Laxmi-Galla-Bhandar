package com.laxmi.galla.customer.delete;

import com.laxmi.galla.entity.CustomerEntity;
import com.laxmi.galla.enums.AccountAction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AccountActionDispatcher {

    private final Map<AccountAction, AccountActionHandler> handlers;

    public AccountActionDispatcher(List<AccountActionHandler> handlers) {

        this.handlers = handlers.stream()
                .collect(Collectors.toMap(
                        AccountActionHandler::supports,
                        Function.identity(),
                        (existing, duplicate) -> {
                            throw new IllegalStateException(
                                    "Duplicate handler found for action: "
                                            + existing.supports()
                            );
                        }
                ));
    }

    public void dispatch(
            AccountAction action,
            CustomerEntity customer,
            String reason,
            String performedBy
    ) {

        if (action == null) {
            throw new IllegalArgumentException("AccountAction cannot be null");
        }

        AccountActionHandler handler = handlers.get(action);

        if (handler == null) {
            throw new IllegalArgumentException(
                    "No handler registered for action: " + action
            );
        }

        handler.handle(customer, reason, performedBy);
    }

    public boolean supports(AccountAction action) {
        return handlers.containsKey(action);
    }
}