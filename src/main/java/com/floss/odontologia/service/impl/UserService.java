package com.floss.odontologia.service.impl;

import com.floss.odontologia.dto.request.UserDTO;
import com.floss.odontologia.model.*;
import com.floss.odontologia.repository.IAppUserRepository;
import com.floss.odontologia.service.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    @Autowired
    private IAppUserRepository userRepository;

    @Override
    public String saveUser(UserDTO userDTO) {

        AppUser user = new AppUser();

        user.setName(userDTO.name());

        user.setSurname(userDTO.surname());

        user.setEmail(userDTO.email());

        userRepository.save(user);

        return "user created";
    }

    @Override
    public AppUser findUserById(Long id) {
        return userRepository.findById( id ).orElseThrow( () -> new RuntimeException("App User not found"));
    }

    @Override
    public AppUser validateIfUserExists(Long id_user_app) {
        AppUser appUser = this.findUserById(id_user_app);
        if(appUser == null) {
            return null;
        }
        return appUser;
    }
}
