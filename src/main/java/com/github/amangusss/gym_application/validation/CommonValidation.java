package com.github.amangusss.gym_application.validation;

import java.time.LocalDate;

public interface CommonValidation {

    void validatePasswordChange(String oldPassword, String newPassword);
    void validateDateRange(LocalDate fromDate, LocalDate toDate);
}
