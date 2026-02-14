package com.dentify.domain.responsibleadult.service;

import com.dentify.domain.responsibleadult.model.ResponsibleAdult;
import com.dentify.domain.responsibleadult.repository.IResponsibleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResponsibleService implements IResponsibleService {

    @Autowired
    private IResponsibleRepository iResponsibleRepository;

    @Override
    public String createResponsible(ResponsibleAdult responsible) {
        iResponsibleRepository.save(responsible);
        return "The responsible adult was saved successfully";
    }
}
