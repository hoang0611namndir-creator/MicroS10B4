package com.pharmacy.notification.consumer;


import com.pharmacy.notification.event.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    @KafkaListener(
            topics = "medicine-stock-events",
            groupId = "notification-service-group"
    )
    public void consumeOrderEvent(OrderEvent event) {

        System.out.println("======================================");
        System.out.println("Order submitted: " + event.getOrderId());
        System.out.println("Medicine ID: " + event.getMedicineId());
        System.out.println("Quantity: " + event.getQuantity());
        System.out.println("======================================");

        sendNotification(event);
    }

    private void sendNotification(OrderEvent event) {

        System.out.println(
                "Hóa đơn cho đơn hàng "
                        + event.getOrderId()
                        + " đã được gửi tới khách hàng"
        );
    }
}