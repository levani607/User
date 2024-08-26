package org.example.user.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.user.model.enums.UserStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListingResponse {

    private Long id;
    private String username;
    private UserStatus status;
}
