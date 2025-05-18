package com.stemapplication.Service;
import com.stemapplication.DTO.SubscriptionRequestDto;
import com.stemapplication.DTO.SubscriptionResponseDto;
import com.stemapplication.Models.Subscription;
import com.stemapplication.Repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String adminEmail;

    public SubscriptionResponseDto subscribe(SubscriptionRequestDto requestDto) {
        String email = requestDto.getEmail();


        // Check if email already exists
        if (subscriptionRepository.existsByEmail(email)) {
            return new SubscriptionResponseDto("Email is already subscribed!", false);
        }

        // Save new subscription
        Subscription subscription = new Subscription(email);
        subscriptionRepository.save(subscription);

        // Send email notification to admin
        sendNotificationToAdmin(email);

        return new SubscriptionResponseDto("Subscription successful!", true);
    }

    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    private void sendNotificationToAdmin(String subscriberEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(adminEmail);
        message.setSubject("New STEM Subscription");
        message.setText("A new user has subscribed with email: " + subscriberEmail);

        mailSender.send(message);
    }


}

