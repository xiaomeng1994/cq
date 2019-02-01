package top.zlm.service;

import com.sobte.cqp.jcq.event.JcqApp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import top.zlm.dao.UserRepository;
import top.zlm.domain.User;

/**
 * 用户业务
 */
@Slf4j
@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;


    /**
     * 入群
     * @param id id
     */
    public void save(Long id){
        try {
            userRepository.save(new User(id,0.0));
        } catch (Exception e) {
            log.error(String.format("QQ:%s入群失败：%s",id,e.getMessage()));
        }
    }

    /**
     * 上分
     * @param id 上分id
     * @param score 分数
     * @return 上分结果
     */
    @Transactional(rollbackFor = Exception.class)
    public String incrementScore(Long id,Double score) {
        userRepository.incrementScore(id,score);
        return String.format("%s上分成功", JcqApp.CC.at(id));
    }

    /**
     * 下分
     * @param id 下分id
     * @param score 分数
     * @return 下分结果
     */
    @Transactional(rollbackFor = Exception.class)
    public String decrementScore(Long id,Double score){
        Double selectScore = userRepository.selectScoreById(id);
        if(selectScore >= score){
            userRepository.decrementScore(id,score);
            return String.format("%s下分成功", JcqApp.CC.at(id));
        }else {
            return String.format("%s下分失败,您的分数不足", JcqApp.CC.at(id));
        }
    }

    /**
     * 用户主入口
     * @param fromGroup 群组
     * @param id id
     * @param msg 消息内容
     */
    public void main(Long fromGroup,Long id,String msg){
        try {
            if(StringUtils.startsWith(msg,"余")){
                Double score = userRepository.selectScoreById(id);
                JcqApp.CQ.sendGroupMsg(fromGroup, String.format("%s您的分数为:%s", JcqApp.CC.at(id),score));
            }else {
                String scoreStr = StringUtils.trim(StringUtils.substring(msg,1));
                if(NumberUtils.isCreatable(scoreStr)) {
                    Double score = NumberUtils.createDouble(scoreStr);
                    if (StringUtils.startsWith(msg, "上")) {
                        JcqApp.CQ.sendGroupMsg(fromGroup, this.incrementScore(id, score));
                    } else if (StringUtils.startsWith(msg, "下")) {
                        JcqApp.CQ.sendGroupMsg(fromGroup, this.decrementScore(id, score));
                    }
                }
            }
        } catch (Exception e){
            log.error(ExceptionUtils.getMessage(e));
        }
    }


}
