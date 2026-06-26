package com.abhi.wallet.service;

import com.abhi.wallet.dto.request.TransferEvent;
import com.abhi.wallet.dto.request.WithdrawRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
public class NotificationService {

    private final KafkaTemplate<String, TransferEvent> kafkaTemplate;

    public NotificationService(
            KafkaTemplate<String, TransferEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotification(TransferEvent transferEvent){
        System.out.println(("kafka producing"+transferEvent.toString()));
        log.info("kafka producing {}",transferEvent.toString());
        kafkaTemplate.send("transfer-events",transferEvent.getReferenceId(),transferEvent);
    }
}
