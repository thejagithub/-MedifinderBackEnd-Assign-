package com.mediFinder.emailService;
import com.mediFinder.emailService.EmailDetails;
import com.mediFinder.models.User;


public interface EmailService {

    String sendSimpleMail(EmailDetails emailDetails);
}
