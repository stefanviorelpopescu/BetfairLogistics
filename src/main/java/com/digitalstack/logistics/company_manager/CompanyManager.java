package com.digitalstack.logistics.company_manager;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Getter
public class CompanyManager
{
    private LocalDate currentDate = LocalDate.of(2021, 12, 14);

    private Long companyProfit = 0L;

    /**
     * Advances the currentDate by one day
     */
    public void advanceDate() {
        currentDate = currentDate.plusDays(1);
    }

    /**
     * Update profit with the given amount
     * @param amount amount to add
     */
    public synchronized void updateProfit(long amount) {
        companyProfit += amount;
    }

}

