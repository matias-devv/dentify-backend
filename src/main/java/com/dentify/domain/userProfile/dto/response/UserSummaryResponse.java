package com.dentify.domain.user.dto.response;

public record UserSummaryResponse (Long id,
                                   String name,
                                   String surname,
                                   String speciality ){
}
