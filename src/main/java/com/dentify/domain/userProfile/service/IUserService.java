package com.dentify.domain.user.service;

import com.dentify.domain.user.dto.request.UpdateUserProfileRequest;
import com.dentify.domain.user.dto.response.UserProfileResponse;
import com.dentify.domain.user.dto.response.UserSummaryResponse;
import com.dentify.domain.user.model.AppUser;
import com.dentify.security.dto.request.RegisterUserRequest;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface IUserService {

    public boolean saveUser(RegisterUserRequest request);

    public AppUser findUserAppEntityById(Long id);

    public AppUser validateIfUserExists(Long id_app_user);

    UserProfileResponse findMe(UserDetails userDetails);

    UserProfileResponse updateUserApp(UserDetails userDetails, @Valid UpdateUserProfileRequest request);

    List<UserSummaryResponse> getAllAppUserSummaryFromDentists();

    String deactiveAppUser(Long id);

    public AppUser findByIdWithAuthUser(Long id);
}
