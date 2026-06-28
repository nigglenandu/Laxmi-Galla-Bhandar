package com.laxmi.galla.customer.domain.handler;

import com.laxmi.galla.customer.domain.entity.CustomerEntity;
import com.laxmi.galla.customer.domain.enums.AccountAction;

public interface AccountActionHandler {

    AccountAction supports();

    void handle(CustomerEntity customer, String reason, String performedBy);
}
