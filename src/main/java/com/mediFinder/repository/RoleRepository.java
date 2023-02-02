package com.mediFinder.repository;

import java.util.Optional;

import com.mediFinder.models.ERole;
import com.mediFinder.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
