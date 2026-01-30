package com.floss.odontologia.service.interfaces;

import com.floss.odontologia.dto.request.UserDTO;
import com.floss.odontologia.model.AppUser;
import com.floss.odontologia.model.AuthUser;

public interface IUserService {

        //create
        public String createUser(UserDTO userDTO);

        public AppUser getUserEntityById(Long id);

        public AppUser validateIfUserExists(Long id_app_user);
}
