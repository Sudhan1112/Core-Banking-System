package com.cbs.util;

import com.cbs.model.enums.AccountType;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.Random;

@Component
public class AccountNumberGenerator {
    
    private final Random random = new Random();
    
    public String generateAccountNumber(AccountType accountType) {
        String prefix = getPrefixForAccountType(accountType);
        String year = String.valueOf(Year.now().getValue()).substring(2);
        String randomDigits = String.format("%06d", random.nextInt(1000000));
        String checkDigit = calculateCheckDigit(prefix + year + randomDigits);
        
        return prefix + year + randomDigits + checkDigit;
    }
    
    private String getPrefixForAccountType(AccountType accountType) {
        switch (accountType) {
            case SAVINGS:
                return "SB";
            case CURRENT:
                return "CA";
            case FIXED_DEPOSIT:
                return "FD";
            case RECURRING_DEPOSIT:
                return "RD";
            case SALARY_ACCOUNT:
                return "SA";
            default:
                return "AC";
        }
    }
    
    private String calculateCheckDigit(String accountNumber) {
        int sum = 0;
        for (int i = 0; i < accountNumber.length(); i++) {
            sum += Character.getNumericValue(accountNumber.charAt(i)) * (i + 1);
        }
        return String.valueOf(sum % 10);
    }
}