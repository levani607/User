package org.example.user.security;

import org.example.user.model.enums.UserRole;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithJwtUserSecurityContextFactory.class)
public @interface WithAuthentication {

    long id = 1L;

    long principal() default id;

    String token() default "";

    String username() default "test";

    long userEmail() default 1L;


    UserRole role() default UserRole.ROLE_USER;

}