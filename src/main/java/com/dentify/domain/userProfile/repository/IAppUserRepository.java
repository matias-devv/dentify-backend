package com.dentify.domain.user.repository;

import com.dentify.domain.user.model.AppUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IAppUserRepository extends JpaRepository<AppUser, Long> {

    @Query("""
           SELECT au
           FROM AppUser au
           JOIN FETCH au.auth_user auth
           WHERE auth.username = :username
           """)
    Optional<AppUser> findByAuthUsername(@Param("username") String username);

    @EntityGraph(attributePaths = {
            "auth_user",
            "auth_user.roles",
            "specialities"
    })
    @Query("""
       SELECT au
       FROM AppUser au
       WHERE EXISTS (
           SELECT 1
           FROM au.auth_user auth
           JOIN auth.roles r
           WHERE r.roleName = :roleName
       )
       """)
    List<AppUser> findAllByRoleName(@Param("roleName") String roleName);

    @Query("""
       SELECT au
       FROM AppUser au
       JOIN FETCH au.auth_user auth
       WHERE au.id_app_user = :id
       """)
    Optional<AppUser> findByIdWithAuthUser(@Param("id") Long id);
}
