package org.example.user.repository;

import org.example.user.model.domain.ApplicationUser;
import org.example.user.model.enums.UserStatus;
import org.example.user.model.response.UserListingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationUserRepository extends JpaRepository<ApplicationUser,Long> {

    Optional<ApplicationUser> findByIdAndStatus(Long id, UserStatus status);
    Optional<ApplicationUser> findByUsernameAndStatus(String username, UserStatus status);

    @Query("""
            select new org.example.user.model.response.UserListingResponse(u.id,u.username,u.status)
            from ApplicationUser u
            where (:prompt is null or u.username like :prompt )
            and (:status is null or u.status in (:status))
            
            """)
    Page<UserListingResponse> list(String prompt, List<UserStatus> status, Pageable pageRequest);

    boolean existsByUsernameAndStatus(String username, UserStatus status);
}
