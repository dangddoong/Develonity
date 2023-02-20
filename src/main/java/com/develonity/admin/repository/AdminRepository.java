package com.develonity.admin.repository;

import com.develonity.admin.entity.Admin;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

  Optional<Admin> findByLoginId(String loginId);

}
