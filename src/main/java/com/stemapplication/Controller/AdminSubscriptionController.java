//package com.stemapplication.Controller;
//import com.stemapplication.Models.Subscription;
//import com.stemapplication.Service.SubscriptionService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/admin/subscriptions")
//public class AdminSubscriptionController {
//
//    private final SubscriptionService subscriptionService;
//
//    public AdminSubscriptionController(SubscriptionService subscriptionService) {
//        this.subscriptionService = subscriptionService;
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Subscription>> getAllSubscriptions() {
//        return new ResponseEntity<>(subscriptionService.getAllSubscriptions(), HttpStatus.OK);
//    }
//}
