package org.example.user.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.user.model.enums.UserRole;
import org.example.user.model.enums.UserStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "application_user", schema = "user_service")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationUser {

    @Id
    @Column(name = "id")
    @SequenceGenerator(
            name = "application_user_id_seq",
            sequenceName = "application_user_id_seq",
            allocationSize = 1,
            schema = "user_service"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "application_user_id_seq"
    )
    private Long id;
    @Column(name = "username")
    private String username;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;


}
