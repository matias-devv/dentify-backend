package com.dentify.domain.user.service;

import com.dentify.domain.user.dto.UserDTO;
import com.dentify.domain.user.model.AppUser;

public interface IUserService {

        //create
        public String saveUser(UserDTO userDTO);

        public AppUser findUserById(Long id);

        public AppUser validateIfUserExists(Long id_app_user);
 
}
