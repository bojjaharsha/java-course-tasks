package com.epam.jmp.application;

import com.epam.jmp.bank.api.Bank;
import com.epam.jmp.dto.BankCardType;
import com.epam.jmp.dto.SubscriptionNotFoundException;
import com.epam.jmp.dto.User;
import com.epam.jmp.service.api.Service;

import java.time.LocalDate;
import java.util.ServiceLoader;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== JMP Application Demo (Tasks 21 - 25) ===");

        Bank bank = ServiceLoader.load(Bank.class)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No Bank implementation found!"));

        Service service = ServiceLoader.load(Service.class)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No Service implementation found!"));

        System.out.println("Loaded Bank Implementation: " + bank.getClass().getName());
        System.out.println("Loaded Service Implementation: " + service.getClass().getName());

        var user1 = new User("Alice", "Smith", LocalDate.of(1992, 3, 15));
        var user2 = new User("Bob", "Johnson", LocalDate.of(2010, 7, 20));

        var creditCard = bank.createBankCard(user1, BankCardType.CREDIT);
        var debitCard = bank.createBankCard(user2, BankCardType.DEBIT);

        System.out.println("\nCreated Card: " + creditCard.getNumber() + " (" + creditCard.getClass().getSimpleName() + ")");
        System.out.println("Created Card: " + debitCard.getNumber() + " (" + debitCard.getClass().getSimpleName() + ")");

        service.subscribe(creditCard);
        service.subscribe(debitCard);

        System.out.println("\n--- All Subscriptions for Credit Cards ---");
        var creditSubscriptions = service.getAllSubscriptionsByCondition(
                sub -> sub.getBankcardNumber().startsWith("CREDIT")
        );
        creditSubscriptions.forEach(sub -> 
                System.out.println("Matched Subscription: " + sub.getBankcardNumber() + ", Date: " + sub.getStartDate())
        );

        System.out.println("\n--- Testing Custom Exception with orElseThrow ---");
        var existingCardNumber = creditCard.getNumber();
        var foundSubscription = service.getSubscriptionByBankCardNumber(existingCardNumber)
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found for: " + existingCardNumber));
        System.out.println("Successfully found: " + foundSubscription.getBankcardNumber());

        try {
            var nonExistentNumber = "INVALID-9999";
            System.out.println("Attempting lookup for invalid card: " + nonExistentNumber);
            service.getSubscriptionByBankCardNumber(nonExistentNumber)
                    .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found for: " + nonExistentNumber));
        } catch (SubscriptionNotFoundException ex) {
            System.out.println("Caught expected custom exception: " + ex.getMessage());
        }

        System.out.println("\n--- Statistics ---");
        System.out.println("Average Users Age: " + service.getAverageUsersAge());
        System.out.println("Is " + user1.getName() + " payable? " + Service.isPayableUser(user1));
        System.out.println("Is " + user2.getName() + " payable? " + Service.isPayableUser(user2));
    }
}
