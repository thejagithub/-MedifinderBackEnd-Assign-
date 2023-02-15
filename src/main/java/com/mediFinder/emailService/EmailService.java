package com.mediFinder.emailService;
import com.mediFinder.emailService.EmailDetails;


public interface EmailService {

    String sendSimpleMail(EmailDetails details);
}
