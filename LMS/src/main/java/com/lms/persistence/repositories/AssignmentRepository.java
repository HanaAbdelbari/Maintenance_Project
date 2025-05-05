package com.lms.persistence.repositories;

import com.lms.persistence.entities.AssignmentEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class AssignmentRepository {

  private final List<AssignmentEntity> store = new ArrayList<>();
  private final AtomicInteger idGenerator = new AtomicInteger(1);

  public AssignmentEntity add(AssignmentEntity entity) {
    entity.setId(idGenerator.getAndIncrement());
    return store.add(entity) ? entity : null;
  }

  public List<AssignmentEntity> findAll() {
    return new ArrayList<>(store);
  }

  public AssignmentEntity findById(int id) {
    return store.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
  }

  public boolean isExist(int id) {
    return store.stream().anyMatch(e -> e.getId() == id);
  }
}
