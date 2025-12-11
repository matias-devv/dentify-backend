package com.floss.odontologia.controller;

import com.floss.odontologia.model.Schedule;
import com.floss.odontologia.service.interfaces.IScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private IScheduleService iScheduleService;

    @PostMapping("/create")
    public String createSchedule(@RequestBody Schedule schedule) {
        return iScheduleService.createSchedule(schedule);
    }

    @GetMapping("/find/{id}")
    public Schedule findScheduleById(@PathVariable Long id) {
        return iScheduleService.getScheduleById(id);
    }

    @GetMapping("/find-all")
    public List<Schedule> findAllSchedules() {
        return iScheduleService.getAllSchedules();
    }

    @PutMapping("/edit")
    public void  editSchedule(@RequestBody Schedule schedule) {
        iScheduleService.editSchedule(schedule);
    }
}
