package com.floss.odontologia.service.interfaces;

import com.floss.odontologia.model.Secretary;

public interface ISecretaryService {

    //create
    public String createSecretary(Secretary secretary);
    //save secretary/user? deberia usar herencia?

    //update
    public String editSecretary(Secretary secretary);
}
