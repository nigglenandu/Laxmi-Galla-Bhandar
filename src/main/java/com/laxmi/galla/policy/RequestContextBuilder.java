package com.laxmi.galla.policy;

import com.laxmi.galla.core.security.CustomUserDetails;
import com.laxmi.galla.core.security.context.AuthContext;
import com.laxmi.galla.dto.RequestContext;
import com.laxmi.galla.entity.CustomerEntity;
import com.laxmi.galla.security.RoleResolver;
import com.laxmi.galla.security.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestContextBuilder {

    private final RoleResolver roleResolver;
    private final AuthContext authContext;

    public RequestContext build(CustomerEntity targetCustomer) {

        CustomUserDetails user = authContext.getCurrentUser();

        Role role = roleResolver.resolve(user.getAuthorities());

        boolean isSelfAction =
                targetCustomer.getUser().getId().equals(user.getId());

        String correlationId = authContext.getCorrelationId();

        return new RequestContext(
                user.getId(),
                user.getUsername(),
                role,
                isSelfAction,
                correlationId
        );
    }
}