package com.epam.jmp.service.api;

import com.epam.jmp.dto.BankCard;
import com.epam.jmp.dto.Subscription;
import com.epam.jmp.dto.User;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface Service {
    
    void subscribe(BankCard bankCard);
    
    Optional<Subscription> getSubscriptionByBankCardNumber(String cardNumber);
    
    List<User> getAllUsers();

    // Task 22: New method for filtering subscriptions
    List<Subscription> getAllSubscriptionsByCondition(Predicate<Subscription> condition);

    default double getAverageUsersAge() {
        var users = getAllUsers();
        if (users.isEmpty()) {
            return 0.0;
        }
        
        var currentDate = LocalDate.now();
        
        return users.stream()
                .map(User::getBirthday)
                .mapToLong(birthday -> ChronoUnit.YEARS.between(birthday, currentDate))
                .average()
                .orElse(0.0);
    }

    static boolean isPayableUser(User user) {
        var currentDate = LocalDate.now();
        return ChronoUnit.YEARS.between(user.getBirthday(), currentDate) >= 18;
    }
}
