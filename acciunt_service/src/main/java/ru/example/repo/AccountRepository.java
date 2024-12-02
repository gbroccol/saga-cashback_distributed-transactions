package ru.example.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
