package com.dentify.domain.responsibleadult.controller;

import com.dentify.domain.responsibleadult.model.ResponsibleAdult;
import com.dentify.domain.responsibleadult.service.IResponsibleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/responsible")
public class ResponsibleController {

    @Autowired
    private IResponsibleService iResponsibleService;

    @PostMapping("/create")
    public String createResponsible(@RequestBody ResponsibleAdult responsibleAdult){
        return iResponsibleService.createResponsible(responsibleAdult);
    }

}
