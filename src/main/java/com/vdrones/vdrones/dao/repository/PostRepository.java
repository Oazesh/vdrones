package com.vdrones.vdrones.dao.repository;

import com.vdrones.vdrones.dao.entity.post.PostEntity;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<PostEntity, Long> {
}
