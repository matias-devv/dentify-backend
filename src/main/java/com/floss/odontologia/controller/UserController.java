package com.floss.odontologia.controller;

import com.floss.odontologia.dto.request.UserDTO;
import com.floss.odontologia.model.AuthUser;
import com.floss.odontologia.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @PostMapping("/save")
    public String saveUser(@RequestBody UserDTO userDTO){
        return iUserService.saveUser(userDTO);
    }
}
