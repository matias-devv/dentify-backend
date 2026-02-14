package com.dentify.controller.admin;

import com.dentify.integration.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
class TestController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/email")
    public String testEmail() {

    //    emailService.sendPaymentReceipt(appointment, pay, payMP);
        return "Email enviado - revisar inbox";
    }
}