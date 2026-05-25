package com.unab.dunab.repository;

import com.unab.dunab.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de usuarios - estructura de datos: lista enlazada gestionada por JPA.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByStudentCode(String studentCode);

    boolean existsByEmail(String email);

    boolean existsByStudentCode(String studentCode);

    /**
     * Ranking Top 12 - obtiene los 12 estudiantes con mayor saldo DUNAB.
     * Estructura: cola de prioridad implícita ordenada por saldo descendente.
     */
    @Query("SELECT u FROM User u WHERE u.role = 'STUDENT' ORDER BY u.dunabBalance DESC LIMIT 12")
    List<User> findTop12ByDunabBalance();

    /** Búsqueda por nombre o código para el admin */
    @Query("SELECT u FROM User u WHERE u.fullName LIKE %:query% OR u.studentCode LIKE %:query% OR u.email LIKE %:query%")
    List<User> searchUsers(String query);
}
