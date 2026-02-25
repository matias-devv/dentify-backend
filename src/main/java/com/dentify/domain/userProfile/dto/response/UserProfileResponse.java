package com.dentify.domain.user.dto.response;

import java.util.List;

public record UserProfileResponse(Long id,
                                  String name,
                                  String surname,
                                  String clinicName,
                                  String dni,
                                  String phoneNumbers,
                                  List<String> roles) {
}
