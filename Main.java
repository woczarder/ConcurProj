package com.harddrillstudio.wat.concurrent;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.stream.Collectors.toList;

public class Main {
    Display display;

    public Main() {
        display = new Display(300, 300);
    }

    public void start() throws InterruptedException {
        WaitingRoom waitingRoom = new WaitingRoom(10);

        ExecutorService executorService = Executors.newFixedThreadPool(50);
        executorService.submit(new Barber(waitingRoom));
        executorService.submit(new Barber(waitingRoom));
        executorService.submit(new Barber(waitingRoom));

        List<Customer> customers = Stream.generate(() -> new Customer(waitingRoom))
                .limit(100)
                .peek(executorService::submit)
                .collect(toList());

        while (!customers.stream().allMatch(Customer::isShaved)) {
            TimeUnit.SECONDS.sleep(1);
        }

        System.out.println("all customers have been shaved");
        executorService.shutdownNow();
        executorService.awaitTermination(1, MINUTES);
    }

}
