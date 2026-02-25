package com.dentify.domain.user.dto.request;

public record UpdateUserProfileRequest (String name,
                                        String surname,
                                        String phone_number,
                                        String clinic_name){
}
