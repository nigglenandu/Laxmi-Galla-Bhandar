package core.repository;

import core.exception.EntityDeletedException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Thin, safe, generic base repository for all platform entities.
 *
 * Provides ONLY very common technical queries that apply to most entities.
 * No domain-specific methods (no email, no username, no status, no type checks).
 *
 * Design goals:
 * - Safe: no crash if entity lacks deletedAt / createdBy fields
 * - Thin: only essential methods — no god-interface
 * - Future-proof: easy to add helpers later without breaking existing repos
 * - Production-grade: uses #{#entityName} + @Query for safety
 * - Reusable: zero business knowledge
 *
 * Usage patterns:
 * - Normal users/services: use findActive*() methods
 * - Admins/reports/cleanup: use findAllIncludingDeleted*() or findDeleted*()
 * - Services: use findByIdOrThrow() for safe lookup
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    // Active-only methods
    default Page<T> findActive(Pageable pageable) {
        return findAll((root, query, cb) -> cb.isNull(root.get("deletedAt")), pageable);
    }

    default T findActiveByIdOrThrow(ID id) {
        return findOne((root, query, cb) -> cb.and(
                cb.equal(root.get("id"), id),
                cb.isNull(root.get("deletedAt"))
        )).orElseThrow(() -> new EntityDeletedException(
                "Entity not found or has been soft-deleted with id: " + id
        ));
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Active-only (default / safe for normal users & business flows)
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Find all entities that are not soft-deleted.
     * Returns empty list if deletedAt field is missing.
     */
    @Query("SELECT t FROM #{#entityName} t WHERE t.deletedAt IS NULL")
    List<T> findActive();

    /**
     * Count all entities that are not soft-deleted.
     * Returns 0 if deletedAt field is missing.
     */
    @Query("SELECT COUNT(t) FROM #{#entityName} t WHERE t.deletedAt IS NULL")
    long countActive();

    /**
     * Check if entity exists and is not soft-deleted.
     */
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END " +
           "FROM #{#entityName} t WHERE t.id = :id AND t.deletedAt IS NULL")
    boolean existsActiveById(@Param("id") ID id);

    /**
     * Find entity by ID if not soft-deleted (returns Optional).
     */
    @Query("SELECT t FROM #{#entityName} t WHERE t.id = :id AND t.deletedAt IS NULL")
    Optional<T> findActiveById(@Param("id") ID id);


    default boolean existsActiveByIdOrThrow(ID id) {
        return existsActiveById(id); // already exists
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Full view (for admins, reports, cleanup jobs, audit trails)
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Find all entities, including soft-deleted ones.
     */
    @Query("SELECT t FROM #{#entityName} t")
    List<T> findAllIncludingDeleted();

    /**
     * Find only soft-deleted entities.
     */
    @Query("SELECT t FROM #{#entityName} t WHERE t.deletedAt IS NOT NULL")
    List<T> findDeleted();

    /**
     * Paginated view of all entities (including soft-deleted).
     */
    @Query("SELECT t FROM #{#entityName} t")
    Page<T> findAllIncludingDeleted(Pageable pageable);

    /**
     * Paginated view of only soft-deleted entities.
     */
    @Query("SELECT t FROM #{#entityName} t WHERE t.deletedAt IS NOT NULL")
    Page<T> findDeleted(Pageable pageable);

    // ─────────────────────────────────────────────────────────────────────────────
    // Convenience & safety defaults (very common in production services)
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Find entity by ID (regardless of deletion state) or throw exception.
     * Use this in services when you expect the entity to exist.
     */
    default T findByIdOrThrow(ID id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + id));
    }
}