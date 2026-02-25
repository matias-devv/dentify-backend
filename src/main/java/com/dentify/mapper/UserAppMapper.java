package com.dentify.domain.user.mapper;

import com.dentify.domain.user.dto.request.UpdateUserProfileRequest;
import com.dentify.domain.user.dto.response.UserProfileResponse;
import com.dentify.domain.user.dto.response.UserSummaryResponse;
import com.dentify.domain.user.model.AppUser;
import com.dentify.security.dto.request.RegisterUserRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserAppMapper {

    public UserProfileResponse buildUserProfileResponse(UserDetails userDetails, AppUser appUser){

        List<String> rolesList = new ArrayList<>();

        for (GrantedAuthority grantedAuthority : userDetails.getAuthorities()) {

            if ( grantedAuthority.getAuthority().startsWith("ROLE_")) {

                rolesList.add( grantedAuthority.getAuthority());
            }
        }

        return new UserProfileResponse( appUser.getId_app_user(),
                                        appUser.getName(),
                                        appUser.getSurname(),
                                        appUser.getClinic_name(),
                                        appUser.getDni(),
                                        appUser.getPhone_number(),
                                        rolesList);
    }

    public AppUser setAttributesToAppUser(RegisterUserRequest request) {
        AppUser appUser = new AppUser();
        appUser.setName(request.name());
        appUser.setSurname(request.surname());
        appUser.setDni(request.dni());
        appUser.setPhone_number(request.phone_number());
        return appUser;
    }

    public AppUser setAttributesToUpdateAppUser(UpdateUserProfileRequest request, AppUser appUser) {
        appUser.setName(request.name());
        appUser.setSurname(request.surname());
        appUser.setPhone_number(request.phone_number());
        appUser.setClinic_name(request.clinic_name());
        return appUser;
    }

    public UserSummaryResponse buildUserSummaryResponse(AppUser appUser) {
        return new UserSummaryResponse(appUser.getId_app_user(),
                                       appUser.getName(),
                                       appUser.getSurname(),
                                       appUser.getSpecialities().stream().findFirst().get().getName() );
    }
}
