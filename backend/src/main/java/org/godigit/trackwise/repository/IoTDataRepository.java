package org.godigit.trackwise.repository;

import org.godigit.trackwise.model.IoTData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IoTDataRepository extends JpaRepository<IoTData, UUID> {
}
