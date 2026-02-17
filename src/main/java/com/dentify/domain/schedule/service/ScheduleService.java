package com.dentify.domain.schedule.service;

import com.dentify.domain.schedule.model.Schedule;
import com.dentify.domain.schedule.repository.IScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ScheduleService implements IScheduleService {

    private IScheduleRepository iScheduleRepository;

}
