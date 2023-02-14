package com.newcen.newcen.commentFile.service;

import com.newcen.newcen.comment.repository.CommentRepository;
import com.newcen.newcen.commentFile.dto.request.CommentFileCreateRequest;
import com.newcen.newcen.commentFile.dto.request.CommentFileUpdateRequest;
import com.newcen.newcen.commentFile.dto.response.CommentFileListResponseDTO;
import com.newcen.newcen.commentFile.dto.response.CommentFileResponseDTO;
import com.newcen.newcen.commentFile.repository.CommentFileRepository;
import com.newcen.newcen.commentFile.repository.CommentFileRepositorySupport;
import com.newcen.newcen.common.entity.CommentEntity;
import com.newcen.newcen.common.entity.CommentFileEntity;
import com.newcen.newcen.common.entity.UserEntity;
import com.newcen.newcen.users.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

//@RequiredArgsConstructor
@Service
//@AllArgsConstructor
@Slf4j
public class CommentFileService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    private final CommentFileRepository commentFileRepository;
    private final CommentFileRepositorySupport commentFileRepositorySupport;

    public CommentFileService(UserRepository userRepository, CommentRepository commentRepository, CommentFileRepository commentFileRepository, CommentFileRepositorySupport commentFileRepositorySupport) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.commentFileRepository = commentFileRepository;
        this.commentFileRepositorySupport = commentFileRepositorySupport;
    }

    //댓글 파일 목록조회
    public CommentFileListResponseDTO retrive(Long commentId){
        List<CommentFileEntity> commentFileList = commentFileRepositorySupport.findCommentFileListByCommentId(commentId);
        List<CommentFileResponseDTO> responseDTO =commentFileList.stream()
                .map(c->new CommentFileResponseDTO(c))
                .collect(Collectors.toList());
        return CommentFileListResponseDTO.builder().error(null).statusCode(200).data(responseDTO).build();
    }
    //댓글 파일 생성
    public CommentFileListResponseDTO createCommentFile(final CommentFileCreateRequest dto, String userId, Long commentId){
        UserEntity user = null;
        user = userRepository.findById(userId).get();
        Optional<CommentEntity> comment = commentRepository.findById(commentId);
        if (user == null) {
            throw new RuntimeException("해당 유저가 없습니다.");
        }
        if (comment == null) {
            throw new RuntimeException("해당 게시글이 없습니다.");
        }
        CommentFileEntity commentFile = dto.toEntity(comment.get());
        CommentFileEntity savedCommentFile = commentFileRepository.save(commentFile);
        return retrive(commentId);

    }
    //댓글 파일 수정
    public CommentFileListResponseDTO updateCommentFile(final CommentFileUpdateRequest dto, String userId, Long commentId, String commentFileId){
        Optional<CommentFileEntity> getCommentFile = commentFileRepository.findById(commentFileId);
        if (getCommentFile==null){
            throw new RuntimeException("해당 댓글파일이 없습니다.");
        }
        Optional<UserEntity> user = userRepository.findById(userId);
        Optional<CommentEntity> getComment = commentRepository.findById(commentId);

        if (!Objects.equals(getCommentFile.get().getCommentId(), getComment.get().getCommentId())) {
            throw new RuntimeException("본인이 작성한 댓글이 아닙니다.");
        }
        String commentFilePath =null;
        if (dto.getCommentFilePath() == null || dto.getCommentFilePath() == ""){
            commentFilePath = getCommentFile.get().getCommentFilePath();
        }
        else {
            commentFilePath = dto.getCommentFilePath();
        }

        getCommentFile.get().updatePath(commentFilePath);
        commentFileRepository.save(getCommentFile.get());
        return retrive(commentId);

    }
    //댓글 파일 삭제
    public boolean deleteCommentFile(String userId, Long commentId, String commentFileId) {
        CommentEntity getComment = commentRepository.findById(commentId).get();
        UserEntity user = userRepository.findById(userId).get();
        if (!Objects.equals(getComment.getCommentWriter(), user.getUserName())) {
            throw new RuntimeException("본인이 작성한 댓글이 아닙니다.");
        }
        Optional<CommentFileEntity> getCommentFile = commentFileRepository.findById(commentFileId);
        if (!Objects.equals(getComment.getCommentId(), getCommentFile.get().getCommentId())) {
            throw new RuntimeException("본인이 작성한 댓글파일이 아닙니다.");
        }
        commentFileRepository.delete(getCommentFile.get());
        return true;
    }

}
