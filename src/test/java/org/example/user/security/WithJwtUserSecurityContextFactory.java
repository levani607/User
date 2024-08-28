package org.example.user.security;


import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithJwtUserSecurityContextFactory implements WithSecurityContextFactory<WithAuthentication> {


    @Override
    public SecurityContext createSecurityContext(WithAuthentication jwtAuthenticationInfo) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        MyAuthentication testAuthentication = createTestAuthentication(jwtAuthenticationInfo);
        context.setAuthentication(testAuthentication);
        return context;
    }

    @NotNull
    private MyAuthentication createTestAuthentication(WithAuthentication jwtAuthenticationInfo) {
        return new MyAuthentication(jwtAuthenticationInfo.token(), jwtAuthenticationInfo.role()
        ,jwtAuthenticationInfo.principal(),jwtAuthenticationInfo.username(),true);
    }


}
