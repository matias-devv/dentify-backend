package com.dentify.domain.responsibleadult.dto;

import com.dentify.domain.responsibleadult.enums.Relation;

public record ResponsibleAdultDTO(String dni,
                                  String name,
                                  String surname,
                                  String phone_number,
                                  String email,
                                  Relation relation) {
}
