package org.example.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jakarta.annotation.PostConstruct;
import org.example.rabbitmq.util.RabbitMqUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

@Component
public class Sender {

    private static final Logger LOG = LoggerFactory.getLogger(Sender.class);

    @PostConstruct
    private void init() {
        Scanner scanner = new Scanner(System.in);

        askForMessage(scanner);
    }

    private void sendMessage(String message) {
        ConnectionFactory factory = new ConnectionFactory();

        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(RabbitMqUtil.getExchange(), RabbitMqUtil.getType());

            channel.basicPublish(
                    RabbitMqUtil.getExchange(),
                    RabbitMqUtil.getRoutingKey(),
                    false,
                    null,
                    message.getBytes());

        } catch (TimeoutException | IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void askForMessage(Scanner scanner) {
        LOG.info("Enter your message: ");

        String message = scanner.nextLine() + " " + LocalDateTime.now();

        sendMessage(message);

        askForMessage(scanner);
    }
}
