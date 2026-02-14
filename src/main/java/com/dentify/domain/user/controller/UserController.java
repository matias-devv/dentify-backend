package com.dentify.domain.user.controller;

import com.dentify.domain.user.dto.UserDTO;
import com.dentify.domain.user.service.IUserService;
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
