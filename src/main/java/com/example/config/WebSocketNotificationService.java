package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class WebSocketNotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void broadcastPrescriptionCreated(Object prescription, UUID doctorId) {
        messagingTemplate.convertAndSend("/topic/admin/prescriptions", prescription);
        messagingTemplate.convertAndSend("/topic/doctor/" + doctorId + "/prescriptions", prescription);
    }

    public void broadcastPrescriptionUpdated(Object prescription, UUID doctorId) {
        messagingTemplate.convertAndSend("/topic/admin/prescriptions", prescription);
        messagingTemplate.convertAndSend("/topic/doctor/" + doctorId + "/prescriptions", prescription);
    }

    public void broadcastPaymentProcessed(Object payment, UUID doctorId) {
        messagingTemplate.convertAndSend("/topic/admin/payments", payment);
        messagingTemplate.convertAndSend("/topic/doctor/" + doctorId + "/payments", payment);
    }

    public void sendAdminNotification(String message) {
        messagingTemplate.convertAndSend("/topic/admin/notifications", Map.of("message", message));
    }

    public void sendDoctorNotification(UUID doctorId, String message) {
        messagingTemplate.convertAndSend("/topic/doctor/" + doctorId + "/notifications", Map.of("message", message));
    }
}
