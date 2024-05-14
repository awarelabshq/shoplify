package org.shoplify.common.repos;

import org.shoplify.common.model.FopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FopRepository extends JpaRepository<FopEntity, Long> {
}
