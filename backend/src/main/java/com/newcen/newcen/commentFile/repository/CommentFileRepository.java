package com.newcen.newcen.commentFile.repository;

import com.newcen.newcen.common.entity.CommentFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface CommentFileRepository extends JpaRepository<CommentFileEntity, String> {
}
