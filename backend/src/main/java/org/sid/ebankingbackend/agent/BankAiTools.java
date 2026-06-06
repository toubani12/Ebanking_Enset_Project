package org.sid.ebankingbackend.agent;

import lombok.AllArgsConstructor;
import org.sid.ebankingbackend.dtos.AccountHistoryDto;
import org.sid.ebankingbackend.dtos.BankAccountDto;
import org.sid.ebankingbackend.dtos.CustmerDto;
import org.sid.ebankingbackend.exceptions.BankAccountNotFoundException;
import org.sid.ebankingbackend.exceptions.CustomerNotFoundException;
import org.sid.ebankingbackend.services.BankAccountService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BankAiTools {

    private final BankAccountService bankAccountService;

    @Tool(description = """
            Retrieve complete information about a bank customer: full name, email address, and unique identifier.
            Use this tool to identify a customer before querying their accounts or performing any operation on their behalf.
            Input: the numeric customer ID.
            """)
    public CustmerDto getCustomerInfo(
            @ToolParam(description = "The unique numeric identifier (ID) of the customer to look up.")
            Long customerId) {
        try {
            return bankAccountService.getCustmer(customerId);
        } catch (CustomerNotFoundException e) {
            throw new RuntimeException("No customer found with ID: " + customerId);
        }
    }

    @Tool(description = """
            Retrieve the current balance and details of a bank account: account type (CURRENT or SAVING),
            status (CREATED, ACTIVATED, SUSPENDED), creation date, associated customer, and type-specific fields
            (overdraft limit for current accounts, interest rate for saving accounts).
            Use this tool when the user asks about an account balance or its current state.
            Input: the account ID (UUID string).
            """)
    public BankAccountDto getAccountBalance(
            @ToolParam(description = "The unique alphanumeric identifier of the bank account (UUID format).")
            String accountId) {
        try {
            return bankAccountService.getBankAccount(accountId);
        } catch (BankAccountNotFoundException e) {
            throw new RuntimeException("No bank account found with ID: " + accountId);
        }
    }

    @Tool(description = """
            Retrieve the last 10 transactions (debit and credit operations) of a bank account, ordered from most recent.
            Each operation includes: date, amount, type (DEBIT or CREDIT), and description.
            Also returns the current balance and total number of pages of history available.
            Use this tool when the user asks about transaction history, recent activity, or account movements.
            Input: the account ID (UUID string).
            """)
    public AccountHistoryDto getAccountHistory(
            @ToolParam(description = "The unique alphanumeric identifier of the bank account (UUID format).")
            String accountId) {
        try {
            return bankAccountService.getAccountHistory(accountId, 0, 10);
        } catch (BankAccountNotFoundException e) {
            throw new RuntimeException("No bank account found with ID: " + accountId);
        }
    }
}
