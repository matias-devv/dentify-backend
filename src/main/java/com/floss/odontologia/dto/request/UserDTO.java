package com.floss.odontologia.dto.request;

import lombok.Getter;
import lombok.Setter;

public record UserDTO (String name,
                       String surname,
                       String email){
}
