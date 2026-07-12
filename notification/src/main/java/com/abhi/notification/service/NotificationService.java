package com.abhi.notification.service;

import com.abhi.notification.dto.TransferEvent;
import com.abhi.notification.entity.NotificationLog;
import com.abhi.notification.exception.BadReqeuest;
import com.abhi.notification.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    @Autowired
    NotificationRepository notificationRepository;

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private  String fromMail;
    @Value("${spring.mail.to-username}")
    private  String toMail;

    public NotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @KafkaListener(topics = "transfer-events",groupId = "notification-group")
    public void consumeEvent(TransferEvent transferEvent){
        System.out.println("transfer e "+transferEvent.toString());
        log.info("consuming {}",transferEvent.toString());
        sendEmail(transferEvent);
    }

    @Transactional
    public boolean sendEmail(TransferEvent transferEvent){
        if(notificationRepository.existsByReferenceId(transferEvent.getReferenceId())){
            throw new BadReqeuest("Transaction already processed");
        }
        // send email
        log.info("sending mail  {}",transferEvent.toString());

        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom(fromMail);
        simpleMailMessage.setSubject("Transaction successful");
        simpleMailMessage.setTo(toMail);
        simpleMailMessage.setText(buildEmailBody(transferEvent));

        javaMailSender.send(simpleMailMessage);
        NotificationLog notificationLog=NotificationLog.builder()
                        .type("EMAIL")
                                .status("SUCCESS")
                                        .referenceId(transferEvent.getReferenceId())
                                                .build();
        notificationRepository.save(notificationLog);
        return true;
    }
    private String buildEmailBody(TransferEvent event) {
        return String.format(
                "Dear User,\n\n" +
                        "Your transfer of Rs. %s was successful.\n" +
                        "Reference ID: %s\n" +
                        "Date: %s\n\n" +
                        "Thank you.",
                event.getAmount(),
                event.getReferenceId(),
                event.getTransferredAt()
        );
    }


}
