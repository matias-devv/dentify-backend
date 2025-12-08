package com.floss.odontologia.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserDTO {
 //duduso por OAuth
    private String name;
    private String surname;
    private String dni;
    private String username;
    private String password;
    private String role;
    private String speciality;
}
