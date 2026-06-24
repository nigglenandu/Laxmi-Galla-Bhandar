package com.laxmi.galla.dto.request;

import com.laxmi.galla.enums.AccountAction;

public record CustomerActionRequest(

        AccountAction action,

        String reason

) {
}