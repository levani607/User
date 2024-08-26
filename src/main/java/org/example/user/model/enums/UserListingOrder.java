package org.example.user.model.enums;

import lombok.Getter;

@Getter
public enum UserListingOrder {

    ID("id"),
    USERNAME("username"),
    STATUS("status");


    private final String value;
    UserListingOrder(String value) {
        this.value = value;
    }
}
