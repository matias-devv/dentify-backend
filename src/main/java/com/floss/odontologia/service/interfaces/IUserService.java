package com.floss.odontologia.service.interfaces;

import com.floss.odontologia.dto.request.UserDTO;
import com.floss.odontologia.model.AppUser;
import com.floss.odontologia.model.AuthUser;
import jakarta.validation.constraints.NotBlank;

public interface IUserService {

        //create
        public String saveUser(UserDTO userDTO);

        public AppUser findUserById(Long id);

        public AppUser validateIfUserExists(Long id_app_user);
 
}
