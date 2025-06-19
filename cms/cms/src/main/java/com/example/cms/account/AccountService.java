package com.example.cms.account;

import com.example.cms.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountResponse create(AccountRequest request) {
        String status = request.getStatus();

        if (!"ACTIVE".equalsIgnoreCase(status) && !"INACTIVE".equalsIgnoreCase(status)) {
            throw new IllegalArgumentException("Invalid status. Allowed values are: ACTIVE, INACTIVE");
        }

        Account account = new Account();
        account.setStatus(status.toUpperCase());
        account.setBalance(request.getBalance());
        account.setCurrency(request.getCurrency());

        return toResponse(accountRepository.save(account));
    }

    public AccountResponse get(UUID id) {
        Account account = getEntity(id);
        return toResponse(account);
    }

    public Page<AccountResponse> getAll(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return accountRepository.findAll(pageable)
                .map(this::toResponse);
    }


    public AccountResponse update(UUID id, AccountRequest request) {
        Account account = getEntity(id);
        account.setStatus(request.getStatus());
        account.setBalance(request.getBalance());
        account.setCurrency(request.getCurrency());
        return toResponse(accountRepository.save(account));
    }

    public void delete(UUID id) {
        accountRepository.deleteById(id);
    }

    private Account getEntity(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Account not found"));
    }

    private AccountResponse toResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setStatus(account.getStatus());
        response.setBalance(account.getBalance());
        response.setCurrency(account.getCurrency());
        return response;
    }
}
