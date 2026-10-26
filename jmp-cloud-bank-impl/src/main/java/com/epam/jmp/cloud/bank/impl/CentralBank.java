package com.epam.jmp.cloud.bank.impl;

import com.epam.jmp.bank.api.Bank;
import com.epam.jmp.dto.BankCard;
import com.epam.jmp.dto.BankCardType;
import com.epam.jmp.dto.CreditBankCard;
import com.epam.jmp.dto.DebitBankCard;
import com.epam.jmp.dto.User;

import java.util.Map;
import java.util.function.BiFunction;

public class CentralBank implements Bank {

    private static final Map<BankCardType, BiFunction<String, User, BankCard>> CARD_FACTORIES = Map.of(
            BankCardType.CREDIT, CreditBankCard::new,
            BankCardType.DEBIT, DebitBankCard::new
    );

    @Override
    public BankCard createBankCard(User user, BankCardType type) {
        var factory = CARD_FACTORIES.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("Unsupported BankCardType: " + type);
        }
        var cardNumber = generateCardNumber(type);
        return factory.apply(cardNumber, user); // Invokes CreditBankCard::new or DebitBankCard::new
    }

    private String generateCardNumber(BankCardType type) {
        return type.name() + "-" + System.nanoTime();
    }
}
