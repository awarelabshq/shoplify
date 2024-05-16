package org.shoplify.user.repos;

import org.shoplify.user.model.FopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FopRepository extends JpaRepository<FopEntity, Long> {
}
