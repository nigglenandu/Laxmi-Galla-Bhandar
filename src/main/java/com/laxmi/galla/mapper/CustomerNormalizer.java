package com.laxmi.galla.mapper;

import com.laxmi.galla.dto.request.CustomerRequestDto;
import com.laxmi.galla.entity.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerNormalizer {

    public CustomerEntity normalize(CustomerEntity c) {
        if (c.getFirstName() != null)
            c.setFirstName(c.getFirstName().trim());

        if (c.getLastName() != null)
            c.setLastName(c.getLastName().trim());

        if (c.getAddress() != null)
            c.setAddress(c.getAddress().trim());

        if (c.getPanNumber() != null)
            c.setPanNumber(c.getPanNumber().trim().toUpperCase());

        return c;
    }
}