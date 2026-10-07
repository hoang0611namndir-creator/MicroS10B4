package com.pharmacy.notification;

import java.math.BigDecimal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BillNotificationRequest(
        @NotBlank @Email String customerEmail,
        @NotBlank String customerName,
        @NotBlank String branchName,
        @NotNull BigDecimal subtotal,
        @NotNull BigDecimal vatRate,
        @NotNull BigDecimal vatAmount,
        @NotNull BigDecimal totalAmount) {
}
