package org.example.user.model.request;

import lombok.Data;
import org.example.user.model.enums.UserListingOrder;
import org.example.user.model.enums.UserStatus;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
public class UserListingRequest {

    private String usernamePrompt;
    private List<UserStatus> status;

    private Integer page;
    private Integer size;
    private Sort.Direction direction;
    private UserListingOrder order;
}
