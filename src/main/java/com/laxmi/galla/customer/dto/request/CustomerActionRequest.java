package com.laxmi.galla.customer.dto.request;

import com.laxmi.galla.customer.domain.enums.AccountAction;

public record CustomerActionRequest(

        AccountAction action,

        String reason

) {
}