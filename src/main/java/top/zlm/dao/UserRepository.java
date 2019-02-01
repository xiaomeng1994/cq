package top.zlm.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import top.zlm.domain.User;

import javax.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Transactional
    @Modifying
    @Query("update User u set u.score = u.score + :score where u.id = :id")
    void incrementScore(@Param("id") Long id, @Param("score") Double score);

    @Transactional
    @Modifying
    @Query("update User u set u.score = u.score - :score where u.id = :id")
    void decrementScore(@Param("id") Long id, @Param("score") Double score);

    @Query("select u.score from User u where u.id = :id")
    Double selectScoreById(@Param("id")Long id);

}
