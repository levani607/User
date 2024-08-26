package org.example.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.user.exceptions.CustomException;
import org.example.user.model.domain.ApplicationUser;
import org.example.user.model.enums.UserStatus;
import org.example.user.model.request.UserBaseRequest;
import org.example.user.model.request.UserListingRequest;
import org.example.user.model.request.UserCreateRequest;
import org.example.user.model.response.UserListingResponse;
import org.example.user.model.response.UserResponse;
import org.example.user.repository.ApplicationUserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final ApplicationUserRepository applicationUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long create(UserCreateRequest request) {
        if(applicationUserRepository.existsByUsernameAndStatus(request.getUsername(), UserStatus.ACTIVE)) {
            throw new CustomException("User already exists", HttpStatus.CONFLICT);
        }
        ApplicationUser entity = new ApplicationUser();
        fillUserValues(entity, request);
        entity.setUsername(request.getUsername());
        return applicationUserRepository.save(entity).getId();
    }

    @Transactional
    public Long update(Long id, UserBaseRequest userCreateRequest) {
        ApplicationUser applicationUser = applicationUserRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("User with id %s was not found!;".formatted(id),HttpStatus.NOT_FOUND));
        fillUserValues(applicationUser, userCreateRequest);
        return applicationUserRepository.save(applicationUser).getId();
    }
    @Transactional
    public void delete(Long id) {
        ApplicationUser applicationUser = applicationUserRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("User with id %s was not found!;".formatted(id),HttpStatus.NOT_FOUND));
        applicationUser.setStatus(UserStatus.DELETED);
        applicationUserRepository.save(applicationUser);
    }

    public UserResponse view(Long id) {
        ApplicationUser applicationUser = applicationUserRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new CustomException("User with id %s was not found!;".formatted(id),HttpStatus.NOT_FOUND));

        return new UserResponse(applicationUser);
    }

    public PagedModel<UserListingResponse> list(UserListingRequest request){

        return new PagedModel<>(applicationUserRepository
                .list(
                        resolvePrompt(request.getUsernamePrompt()),
                        request.getStatus(),
                        PageRequest.of(request.getPage(),request.getSize(), Sort.by(request.getDirection(),request.getOrder().getValue()))
                ));
    }

    private String resolvePrompt(String usernamePrompt) {
        if(StringUtils.hasText(usernamePrompt)) {
            return "%"+usernamePrompt+"%";
        }

        return usernamePrompt;
    }

    public void fillUserValues(ApplicationUser applicationUser, UserBaseRequest request) {
        applicationUser.setEmail(request.getEmail());
        applicationUser.setRole(request.getRole());
        applicationUser.setFirstname(request.getFirstname());
        applicationUser.setPassword(passwordEncoder.encode(request.getPassword()));
        applicationUser.setStatus(UserStatus.ACTIVE);


    }

}
