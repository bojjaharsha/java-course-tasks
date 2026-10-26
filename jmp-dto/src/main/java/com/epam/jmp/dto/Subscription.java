package com.epam.jmp.dto;

import java.time.LocalDate;
import java.util.Objects;

public class Subscription {
    private String bankcardNumber;
    private LocalDate startDate;

    public Subscription() {}

    public Subscription(String bankcardNumber, LocalDate startDate) {
        this.bankcardNumber = bankcardNumber;
        this.startDate = startDate;
    }

    public String getBankcardNumber() {
        return bankcardNumber;
    }

    public void setBankcardNumber(String bankcardNumber) {
        this.bankcardNumber = bankcardNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subscription)) return false;
        Subscription that = (Subscription) o;
        return Objects.equals(bankcardNumber, that.bankcardNumber) &&
               Objects.equals(startDate, that.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bankcardNumber, startDate);
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "bankcardNumber='" + bankcardNumber + '\'' +
                ", startDate=" + startDate +
                '}';
    }
}
