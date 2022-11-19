package ru.mirea.web3.web3javaclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Scanner;

@SpringBootApplication
public class Web3JavaClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(Web3JavaClientApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        System.out.print("Please, enter IP Address of Server Hosting Machine [x.x.x.x]: ");

        Scanner scanner = new Scanner(System.in);
        String ip = null;
        boolean isCorrect = false;
        while (!isCorrect) {
            ip = scanner.nextLine();
            if (ip.matches("^\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b")) {
                isCorrect = true;
            } else {
                System.out.print("IP Address is incorrect! Please, enter another [x.x.x.x]: ");
            }
        }

        return builder.uriTemplateHandler(new DefaultUriBuilderFactory("http://" + ip + ":8080")).build();
    }
}
