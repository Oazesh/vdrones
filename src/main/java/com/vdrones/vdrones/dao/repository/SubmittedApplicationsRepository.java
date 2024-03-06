package com.vdrones.vdrones.dao.repository;

import com.vdrones.vdrones.dao.entity.post.SubmittedApplicationsEntity;
import org.springframework.data.repository.CrudRepository;

public interface SubmittedApplicationsRepository extends CrudRepository<SubmittedApplicationsEntity, Long> {
}
