package top.zlm.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import top.zlm.domain.Buy;

import java.util.List;

@Repository
public interface BuyRepository extends CrudRepository<Buy,Long>,JpaRepository<Buy, Long> {


    @Query("select sum(b.score) from Buy b where b.userId = :userId and b.currentNum = :currentNum")
    Double sumScoreByUserIdAndAndCurrentNum(@Param("userId") Long userId, @Param("currentNum") String currentNum);

    @Modifying
    @Transactional
    void deleteByUserIdAndCurrentNum(@Param("userId")Long userId, @Param("currentNum")String currentNum);

    @Modifying
    @Transactional
    @Query("update Buy b set b.num = :num where b.currentNum = :currentNum and b.content = :content")
    void updateNumByContentAndCurrentNum(@Param("num")String num, @Param("currentNum")String currentNum, @Param("content")String content);


    //@Query("select b from Buy b where b.userId = :userId and b.currentNum = :currentNum")
    List<Buy> findAllByUserIdAndCurrentNum(@Param("userId") Long userId, @Param("currentNum") String currentNum);

    //@Query("select b from Buy b where b.currentNum = :currentNum")
    List<Buy> findAllByCurrentNum(@Param("currentNum") String currentNum);

    List<Buy> findAllByCurrentNumAndStatus(@Param("currentNum") String currentNum, @Param("status") Short status);

}
