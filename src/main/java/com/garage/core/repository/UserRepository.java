package com.garage.core.repository;

import com.garage.core.entity.UserEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CassandraRepository<UserEntity> {

    @Query("select * from users where user_name = ?0")
    Iterable<UserEntity> findByUserName(String username);

}
