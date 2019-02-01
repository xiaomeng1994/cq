package top.zlm.service;

import com.sobte.cqp.jcq.event.JcqApp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import top.zlm.dao.BuyRepository;
import top.zlm.dao.UserRepository;
import top.zlm.domain.Buy;
import top.zlm.task.Task;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 攻击业务层
 */
@Slf4j
@Component
public class BuyService {

    @Autowired
    private Task task;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BuyRepository buyRepository;

    /**
     * 攻击
     * @param buy id
     */
    @Transactional(rollbackFor = Exception.class)
    public String save(Buy buy){
        Double scoreById = userRepository.selectScoreById(buy.getUserId());
        if(scoreById >= buy.getScore()){
            buyRepository.save(buy);
            userRepository.decrementScore(buy.getUserId(),buy.getScore());
            return String.format("%s攻击成功", JcqApp.CC.at(buy.getUserId()));
        }else {
            return String.format("%s攻击失败,分数不足", JcqApp.CC.at(buy.getUserId()));
        }
    }

    /**
     * 兑换
     * @param currentNum 当前级数
     * @param result 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public void compare(String currentNum,List<Integer> result){
        Short status = new Short("1");
        List<Buy> buys = buyRepository.findAllByCurrentNum(currentNum);
        for (Buy buy : buys) {
            if(StringUtils.contains(buy.getNum(),String.valueOf(result.get(4)))){
                buy.setWin((buy.getScore() / StringUtils.length(buy.getNum())*9.65));
                buy.setStatus(status);
                userRepository.incrementScore(buy.getUserId(),buy.getWin());
            }
            buy.setResult(ArrayUtils.toString(result));
        }
        buyRepository.save(buys);
    }

    /**
     * 清
     * @param id id
     * @return result
     */
    @Transactional(rollbackFor = Exception.class)
    public String clear(Long id){
        Double score = buyRepository.sumScoreByUserIdAndAndCurrentNum(id, task.getCd());
        buyRepository.deleteByUserIdAndCurrentNum(id,task.getCd());
        userRepository.incrementScore(id,score);
        return String.format("%s清除成功", JcqApp.CC.at(id));
    }


    /**
     * 查
     * @param id id
     * @return result
     */
    public String selectByIdAndCurrentNum(Long id){
        StringBuilder builder = new StringBuilder(JcqApp.CC.at(id));
        List<Buy> buys = buyRepository.findAllByUserIdAndCurrentNum(id, task.getCd());
        if(CollectionUtils.isEmpty(buys)){
            builder.append("暂无数据");
        }else {
            for (Buy buy : buys) {
                builder.append("\n").append(buy.getContent()).append("/").append(buy.getScore());
            }
        }
        return builder.toString();
    }

    /**
     * 主入口
     * @param fromGroup 群组
     * @param id id
     * @param msg 消息内容
     */
    public void main(Long fromGroup,Long id,String msg){
        try {
            if(StringUtils.equals(msg,"查")){
                JcqApp.CQ.sendGroupMsg(fromGroup, this.selectByIdAndCurrentNum(id));
            }else if(StringUtils.equals(msg,"清")){
                JcqApp.CQ.sendGroupMsg(fromGroup, this.clear(id));
            }else {
                String scoreStr = StringUtils.trim(StringUtils.substring(msg,1));
                if(NumberUtils.isCreatable(scoreStr)) {
                    Double score = NumberUtils.createDouble(scoreStr);
                    if(score > 0){
                        Buy buy = new Buy(id, new Short("1"), StringUtils.substring(msg,0,1),
                                score, new Short("0"), 0.0, LocalDateTime.now(), task.getCd());
                        String result = this.save(buy);
                        JcqApp.CQ.sendGroupMsg(fromGroup, result);
                    }else {
                        JcqApp.CQ.sendGroupMsg(fromGroup, String.format("%s攻击失败,分数必须0", JcqApp.CC.at(id)));
                    }
                }
            }
        }catch (Exception e){
            log.error(ExceptionUtils.getMessage(e));
        }
    }

}
