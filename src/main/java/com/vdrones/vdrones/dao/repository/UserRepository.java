package com.vdrones.vdrones.dao.repository;

import com.vdrones.vdrones.dao.entity.users.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    UserEntity findByUsername(String userName);
}
