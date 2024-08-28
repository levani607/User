package org.example.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.user.model.request.UserBaseRequest;
import org.example.user.model.request.UserListingRequest;
import org.example.user.model.request.UserCreateRequest;
import org.example.user.model.response.UserListingResponse;
import org.example.user.model.response.UserResponse;
import org.example.user.service.UserService;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(
            security = @SecurityRequirement(name = "bearer-token"))
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> create(@RequestBody @Valid UserCreateRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(
            security = @SecurityRequirement(name = "bearer-token"))
    @PreAuthorize("hasRole('ADMIN') || #id.equals(authentication.principal)")
    public ResponseEntity<Long> update(@PathVariable("id") Long id, @Valid @RequestBody UserBaseRequest request){
        return ResponseEntity.ok(userService.update(id,request));
    }

    @GetMapping
    @Operation(
            security = @SecurityRequirement(name = "bearer-token"))
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedModel<UserListingResponse>> list(UserListingRequest request){
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(userService.list(request));
    }
    @GetMapping("/{id}")
    @Operation(
            security = @SecurityRequirement(name = "bearer-token"))
    @PreAuthorize("hasRole('ADMIN') || #id.equals(authentication.principal)")
    public ResponseEntity<UserResponse> get(@PathVariable("id") Long id){
        return ResponseEntity.ok(userService.view(id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            security = @SecurityRequirement(name = "bearer-token"))
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id){
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
