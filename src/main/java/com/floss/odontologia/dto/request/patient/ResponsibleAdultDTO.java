package com.floss.odontologia.dto.request.patient;

import com.floss.odontologia.enums.Relation;

public record ResponsibleAdultDTO(String dni,
                                  String name,
                                  String surname,
                                  String phone_number,
                                  String email,
                                  Relation relation) {
}
