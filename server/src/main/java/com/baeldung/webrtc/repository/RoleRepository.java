package com.baeldung.webrtc.repository;

import com.baeldung.webrtc.entities.Role;
import org.springframework.data.repository.CrudRepository;


public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findByRoleName(String name);
}

