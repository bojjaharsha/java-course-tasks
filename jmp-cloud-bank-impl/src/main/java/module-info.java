module jmp.cloud.bank.impl {
    requires transitive jmp.bank.api;
    requires jmp.dto;
    exports com.epam.jmp.cloud.bank.impl;
    provides com.epam.jmp.bank.api.Bank with com.epam.jmp.cloud.bank.impl.CentralBank;
}
