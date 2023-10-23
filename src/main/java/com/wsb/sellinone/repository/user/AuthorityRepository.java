package com.wsb.sellinone.repository.user;

import com.wsb.sellinone.entity.user.AuthorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<AuthorityEntity, String> {

}
