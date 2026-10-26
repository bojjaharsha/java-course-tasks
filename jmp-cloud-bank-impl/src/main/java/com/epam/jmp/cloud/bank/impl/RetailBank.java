package com.epam.jmp.cloud.bank.impl;

import com.epam.jmp.bank.api.Bank;
import com.epam.jmp.dto.BankCard;
import com.epam.jmp.dto.BankCardType;
import com.epam.jmp.dto.CreditBankCard;
import com.epam.jmp.dto.DebitBankCard;
import com.epam.jmp.dto.User;

public class RetailBank implements Bank {
    @Override
    public BankCard createBankCard(User user, BankCardType type) {
        if (type == BankCardType.CREDIT) {
            return new CreditBankCard(generateCardNumber(), user);
        } else {
            return new DebitBankCard(generateCardNumber(), user);
        }
    }

    private String generateCardNumber() {
        return "RETAIL-" + System.nanoTime();
    }
}
