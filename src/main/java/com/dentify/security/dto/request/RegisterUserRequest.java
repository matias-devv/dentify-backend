package com.dentify.security.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record UserSaveRequest(@NotBlank String name,
                              @NotBlank String surname,
                              @NotBlank String username,
                              @NotBlank String password,
                              @NotBlank String dni,
                              String phone_number,
                              Long id_speciality,
                              @NotBlank Long id_role) {
}
