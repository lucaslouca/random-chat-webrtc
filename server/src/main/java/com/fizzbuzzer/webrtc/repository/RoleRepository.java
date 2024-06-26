package com.fizzbuzzer.webrtc.repository;

import com.fizzbuzzer.webrtc.entities.Role;
import org.springframework.data.repository.CrudRepository;


public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findByRoleName(String name);
}

