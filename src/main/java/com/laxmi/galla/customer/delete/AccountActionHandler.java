package com.laxmi.galla.customer.delete;

import com.laxmi.galla.entity.CustomerEntity;
import com.laxmi.galla.enums.AccountAction;

public interface AccountActionHandler {

    AccountAction supports();

    void handle(CustomerEntity customer, String reason, String performedBy);
}
