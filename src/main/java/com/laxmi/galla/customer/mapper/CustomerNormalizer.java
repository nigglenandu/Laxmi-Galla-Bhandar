package com.laxmi.galla.customer.mapper;

import com.laxmi.galla.customer.domain.entity.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerNormalizer {

    public CustomerEntity normalize(CustomerEntity c) {
        if (c.getUser().getFirstName() != null)
            c.getUser().setFirstName(c.getUser().getFirstName().trim());

        if (c.getUser().getLastName() != null)
            c.getUser().setLastName(c.getUser().getLastName().trim());

        if (c.getAddress() != null)
            c.setAddress(c.getAddress().trim());

        if (c.getPanNumber() != null)
            c.setPanNumber(c.getPanNumber().trim().toUpperCase());

        return c;
    }
}