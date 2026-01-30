package com.floss.odontologia.controller;

import com.floss.odontologia.dto.request.AgendaRequestDTO;
import com.floss.odontologia.dto.request.MonthDateRangeRequestDTO;
import com.floss.odontologia.dto.request.WeekDateRangeRequestDTO;
import com.floss.odontologia.dto.response.calendar.AgendaResponseDTO;
import com.floss.odontologia.dto.response.calendar.FullDailyResponseDTO;
import com.floss.odontologia.dto.response.calendar.MonthSummaryResponseDTO;
import com.floss.odontologia.dto.response.calendar.WeekSummaryResponseDTO;
import com.floss.odontologia.dto.request.DayRequestDTO;
import com.floss.odontologia.service.interfaces.IAgendaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping ("/api/agendas")
public class AgendaController {

    private final IAgendaService agendaService;

    public AgendaController ( IAgendaService agendaService) {
        this.agendaService = agendaService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveAgenda(@RequestBody AgendaRequestDTO agendaRequestDTO) {
        return ResponseEntity.ok( agendaService.save(agendaRequestDTO) );
    }
//
//    @GetMapping("/user/{id_user_app}")
//    public ResponseEntity<List> findAgendasByUser(@PathVariable Long id_user_app) {
//        List<AgendaResponseDTO> listDto = agendaService.findAgendasByUser(id_user_app);
//        return ResponseEntity.ok(listDto);
//    }
//
//    @GetMapping("/{id_agenda}")
//    public ResponseEntity<AgendaResponseDTO> findAgendaById(@PathVariable Long id_agenda){
//        Optional<AgendaResponseDTO> agendaDto = agendaService.findAgendaById(id_agenda);
//        return agendaDto.map(ResponseEntity::ok).orElse( ResponseEntity.notFound().build() );
//    }
//
//    @PatchMapping("/patch")
//    public ResponseEntity<String> patchStatusAgenda( @RequestBody AgendaRequestDTO agendaRequestDTO){
//        return ResponseEntity.ok( agendaService.patchStatusAgenda(agendaRequestDTO) );
//    }
//
//    @PutMapping("/edit")
//    public ResponseEntity<String> editAgenda( @RequestBody AgendaRequestDTO agendaRequestDTO){
//        return ResponseEntity.ok( agendaService.editAgenda(agendaRequestDTO) );
//    }
//
//    @GetMapping("/calendar/day")
//    public ResponseEntity<FullDailyResponseDTO> getAllSlotsInDay(@RequestBody DayRequestDTO request){
//        return ResponseEntity.ok( agendaService.getAllSlotsInDay( request) );
//    }
//
//    @GetMapping("/calendar/week")
//    public ResponseEntity<WeekSummaryResponseDTO> getAvailableSlotsInWeek(@RequestBody WeekDateRangeRequestDTO request){
//        return ResponseEntity.ok( agendaService.getAvailableSlotsInWeek( request) );
//    }
//
//    @GetMapping("/calendar/month")
//    public ResponseEntity<MonthSummaryResponseDTO> getSummaryOfTheMonth(@RequestBody MonthDateRangeRequestDTO request){
//        return ResponseEntity.ok( agendaService.getSummaryOfTheMonth( request) );
//    }

}
