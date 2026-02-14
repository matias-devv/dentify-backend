package com.dentify.domain.schedule.service;

import com.dentify.domain.schedule.dto.ScheduleDTO;
import com.dentify.domain.schedule.model.Schedule;

import java.util.List;

public interface IScheduleService {

    //create
    public String createSchedule(Schedule schedule);

    //read
    public Schedule getScheduleById(Long id);

    public List<ScheduleDTO> getAllDentistSchedules(Long id);

    public void editSchedule(Schedule schedule);

    public ScheduleDTO setAttributesDto(Schedule schedule);
}
