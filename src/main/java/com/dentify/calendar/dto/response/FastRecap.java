package com.dentify.calendar.dto.response;

public record FastRecap(Integer total_slots,
                        Integer free_slots,
                        Integer occupied_slots,
                        Double percentage_occupancy) {
}
