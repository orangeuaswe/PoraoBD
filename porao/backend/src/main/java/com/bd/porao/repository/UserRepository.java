package com.bd.porao.repository;

import com.bd.porao.model.User;
import com.bd.porao.model.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Long>
{
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
}
