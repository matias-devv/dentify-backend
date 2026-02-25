package com.dentify.domain.user.service;

import com.dentify.common.util.JwtUtils;
import com.dentify.domain.speciality.model.Speciality;
import com.dentify.domain.speciality.service.ISpecialityService;
import com.dentify.domain.user.dto.request.UpdateUserProfileRequest;
import com.dentify.domain.user.dto.response.UserProfileResponse;
import com.dentify.domain.user.dto.response.UserSummaryResponse;
import com.dentify.mapper.UserAppMapper;
import com.dentify.domain.user.model.AppUser;
import com.dentify.domain.user.repository.IAppUserRepository;
import com.dentify.security.dto.request.RegisterUserRequest;
import com.dentify.mapper.AuthUserMapper;
import com.dentify.security.model.AuthUser;
import com.dentify.security.model.Role;
import com.dentify.security.repository.IAuthUserRepository;
import com.dentify.security.service.IRoleService;
import com.dentify.security.service.UserDetailsServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IAppUserRepository appUserRepository;
    private final IAuthUserRepository authUserRepository;
    private final ISpecialityService specialityService;
    private final IRoleService roleService;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImp userDetailsService;
    private final UserAppMapper userAppMapper;
    private final AuthUserMapper authUserMapper;

    //I assume that during login it validated whether the user existed before this method
    @Override
    public boolean saveUser(RegisterUserRequest request) {

        AppUser appUser = userAppMapper.setAttributesToAppUser( request);
        AuthUser authUser = authUserMapper.setAttributesToAuthUser(request);

        if( request.id_speciality() != null) {
            appUser = this.setSpecialityToAppUser(appUser, request.id_speciality());
        }

        authUser = this.setRoleToAuthUser( authUser, request.id_role() );
        
        appUserRepository.save( appUser);

        authUserRepository.save( authUser );

        this.setRelationBetweenAppUserAndAuthUser(appUser, authUser);

        return true;
    }

    private void setRelationBetweenAppUserAndAuthUser(AppUser appUser, AuthUser authUser) {

        authUser.setAppUser(appUser);
        appUser.setAuth_user(authUser);

        authUserRepository.save( authUser );
        appUserRepository.save(appUser);
    }

    private AuthUser setRoleToAuthUser(AuthUser authUser, Long idRole) {

        Optional<Role> role = roleService.getRole(idRole);

        if( role.isPresent() ) {

            Set<Role> roles = authUser.getRoles();

            if( !roles.contains( role.get() ) ) {

                roles.add( role.get() );

                authUser.setRoles( roles );
            }
        }
        return authUser;
    }

    private AppUser setSpecialityToAppUser(AppUser appUser, Long idSpeciality) {

        Speciality speciality = specialityService.getSpecialityEntityById(idSpeciality);

        if( speciality != null) {

            Set<Speciality> specialities = appUser.getSpecialities();

            if( !specialities.contains( speciality ) ) {

                specialities.add( speciality );

                appUser.setSpecialities( specialities );
            }
        }
        return appUser;
    }


    @Override
    public AppUser findUserAppEntityById(Long id) {
        return appUserRepository.findById( id ).orElseThrow( () -> new RuntimeException("App User not found"));
    }

    @Override
    public AppUser validateIfUserExists(Long id_user_app) {

        AppUser appUser = this.findUserAppEntityById(id_user_app);

        if(appUser == null) {
            return null;
        }
        return appUser;
    }

    @Override
    public UserProfileResponse findMe( UserDetails userDetails) {

        AppUser appUser = this.findUserAppEntityByUsername( userDetails.getUsername() );

        return userAppMapper.buildUserProfileResponse( userDetails, appUser);
    }

    private AppUser findUserAppEntityByUsername(String username) {

        Optional<AppUser> appUser = appUserRepository.findByAuthUsername(username);

        if ( appUser.isPresent() ) {
            return appUser.get();
        }
        else{
            throw new UsernameNotFoundException("There is no user associated with this username");
        }
    }

    @Override
    public UserProfileResponse updateUserApp(UserDetails userDetails, UpdateUserProfileRequest request) {

        AppUser appUser = this.findUserAppEntityByUsername(userDetails.getUsername());

        appUser = userAppMapper.setAttributesToUpdateAppUser(request, appUser);

        appUserRepository.save(appUser);

        return userAppMapper.buildUserProfileResponse(userDetails, appUser);
    }

    @Override
    public List<UserSummaryResponse> getAllAppUserSummaryFromDentists() {

        List<AppUser> users = appUserRepository.findAllByRoleName("ROLE_DENTIST");
        List<UserSummaryResponse> responseList = new ArrayList<>();

        for (AppUser appUser : users) {

            responseList.add( userAppMapper.buildUserSummaryResponse(appUser) );
        }
        return responseList;
    }

    @Override
    public String deactiveAppUser(Long id) {

        AppUser appUser = appUserRepository.findByIdWithAuthUser( id ).orElseThrow( () -> new RuntimeException("App User not found"));

        appUser.getAuth_user().setEnabled(false);
        appUser.getAuth_user().setAccountNotLocked(false);

        return "The user was successfully deactivated";
    }

    @Override
    public AppUser findByIdWithAuthUser(Long id) {
        return appUserRepository.findByIdWithAuthUser( id ).orElseThrow( () -> new RuntimeException("App User not found"));
    }

}
