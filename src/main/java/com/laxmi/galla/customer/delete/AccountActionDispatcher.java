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

    public AccountActionDispatcher(List<AccountActionHandler> handlerList) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(
                        AccountActionHandler::supports,
                        Function.identity(),
                        (a, b) -> {
                            throw new IllegalStateException(
                                    "Duplicate handler for: " + a.supports()
                            );
                        }
                ));
    }

    public void dispatch(AccountAction action,
                         CustomerEntity customer,
                         String reason,
                         String performedBy) {

        AccountActionHandler handler = handlers.get(action);

        if (handler == null) {
            throw new IllegalStateException("No handler for: " + action);
        }

        handler.handle(customer, reason, performedBy);
    }

    public boolean supports(AccountAction action) {
        return handlers.containsKey(action);
    }
}