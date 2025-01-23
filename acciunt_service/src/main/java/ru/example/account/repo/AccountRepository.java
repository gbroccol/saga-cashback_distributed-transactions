package ru.example.account.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.account.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
