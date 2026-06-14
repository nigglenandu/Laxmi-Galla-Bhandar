package com.laxmi.galla.customer.delete;

import com.laxmi.galla.entity.CustomerEntity;
import com.laxmi.galla.enums.AccountStatus;

public interface AccountActionHandler {

    AccountStatus supports();

    void handle(CustomerEntity customer, String reason, String performedBy);
}
