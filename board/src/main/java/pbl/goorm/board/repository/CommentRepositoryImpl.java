package pbl.goorm.board.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pbl.goorm.board.model.DeleteStatus;
import pbl.goorm.board.model.dto.CommentUpdateDto;
import pbl.goorm.board.model.entity.Comment;

import java.util.List;
import java.util.Optional;


//need to make id autogenerated
@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

    private final EntityManager em;

    @Override
    public void delete(Long commentId) {
        Comment comment = em.find(Comment.class, commentId);
        if (comment == null || comment.getDeleteStatus().equals(DeleteStatus.DELETE)){
            throw new RuntimeException("cannot find such comment or already deleted comment");
        }
        comment.setDeleteStatus(DeleteStatus.DELETE);
    }

    @Override
    public void update(Long commentId, CommentUpdateDto updateDto) {
        Comment comment = em.find(Comment.class, commentId);
        if (comment == null){
            throw new RuntimeException("cannot find such comment");
        }
        if (comment.getDeleteStatus() != DeleteStatus.DELETE){
            comment.setBody(updateDto.getBody());
        }
    }

    @Override
    public Optional<Comment> findById(Long commentId) {
        String jpql = "select c from Comment c" +
                " where c.deleteStatus ='ACTIVE' and c.id=:commentId";
        TypedQuery<Comment> query = em.createQuery(jpql, Comment.class);
        query.setParameter("commentId", commentId);
        Optional<Comment> comment = query.getResultList()
                .stream()
                .filter(c -> c.getClass().equals(Comment.class))
                .findAny();
        return comment;
    }

    @Override
    public void clear() {
        String jpql = "delete from Comment";
        Query query = em.createQuery(jpql);
        query.executeUpdate();
    }
}