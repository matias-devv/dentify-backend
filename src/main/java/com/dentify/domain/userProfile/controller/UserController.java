package com.dentify.domain.user.controller;

import com.dentify.domain.user.dto.request.UpdateUserProfileRequest;
import com.dentify.domain.user.dto.response.UserProfileResponse;
import com.dentify.domain.user.dto.response.UserSummaryResponse;
import com.dentify.domain.user.service.IUserService;
import com.dentify.security.dto.request.RegisterUserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping("/save")
    public boolean savaUser(@RequestBody RegisterUserRequest request) {
        return userService.saveUser(request);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getUserAppById(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok( userService.findMe( userDetails) );
    }

    @GetMapping("/summary")
    public ResponseEntity<List> getAllUserAppSummaryFromDentists(){
        return ResponseEntity.ok( userService.getAllAppUserSummaryFromDentists() );
    }

    @PutMapping("/update")
    public ResponseEntity<UserProfileResponse> updateUserApp(@AuthenticationPrincipal UserDetails userDetails,
                                                             @Valid @RequestBody UpdateUserProfileRequest request){
        return ResponseEntity.ok( userService.updateUserApp(userDetails, request) );
    }

    @PatchMapping("/deactivate/{id}")
    public ResponseEntity<String> deactiveAppUser(@PathVariable Long id){
        return ResponseEntity.ok( userService.deactiveAppUser(id) );
    }
}
