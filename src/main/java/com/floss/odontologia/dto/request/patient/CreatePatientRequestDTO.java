package com.floss.odontologia.dto.request.patient;

import com.floss.odontologia.enums.CoverageType;

import java.time.LocalDate;
import java.util.List;

public record CreatePatientRequestDTO(String dni,
                                      String name,
                                      String surname,
                                      Integer age,
                                      LocalDate date_of_birth,
                                      String insurance,
                                      CoverageType coverageType,
                                      String phone_number,
                                      String email,
                                      List<ResponsibleAdultDTO> responsibleAdultList) {
}
