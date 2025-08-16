package org.godigit.trackwise.repository;

import org.godigit.trackwise.model.Notification;
import org.godigit.trackwise.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
  List<Notification> findByRecipientAndReadFalse(Employee recipient);
}
