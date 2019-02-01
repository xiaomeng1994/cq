package top.zlm.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sobte.cqp.jcq.entity.Group;
import com.sobte.cqp.jcq.event.JcqApp;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import top.zlm.constant.AppConstant;
import top.zlm.dao.BuyRepository;
import top.zlm.domain.Buy;
import top.zlm.service.BuyService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class Task {

    @Autowired
    private BuyService buyService;

    @Autowired
    private BuyRepository buyRepository;

    private String host = "https://www.hl888fun.com";

    private String cd;
    private String ud;
    private String nd;
    private Integer cdTs = 0;
    private Integer udTs = 0;

    /**
     * 检测相关
     */
    private Boolean isNotWholeBean = Boolean.TRUE;
    private Boolean isNotTip = Boolean.TRUE;

    /**
     * 任务定时调度
     */
    @Test
    @Scheduled(fixedRate = 1000)
    public void countDown() {
        --cdTs;
        --udTs;
        //String post = this.post("https://www.hl888fun.com/cp/loadDrawHistory.do?siteCode=CQSSC&noOfD=_30");
        //更新冷却时间
        if(udTs <= 0){
            Request.Builder requestBuilder = new Request.Builder()
                    .url(String.format("%s/cp/loadSiteInfo.do?siteCode=CQSSC", this.host))
                    .addHeader("Cookie", "sx_username=mengmeng;");
            try {
                String post = this.post(requestBuilder);
                log.info(post);
                Map<String, String> map = JSON.parseObject(JSON.parseObject(post,
                        new TypeReference<Map<String, String>>() {}).get("message"),
                        new TypeReference<Map<String, String>>() {});
                //由于调度时间过短，防止重复
                log.info("ld:{}，lr:{},cd:{},cdTs:{},ud:{},udTs:{},nd:{}"
                        ,map.get("ld"),map.get("lr"),map.get("cd"),map.get("cdTs"),map.get("ud"),map.get("udTs"),map.get("nd"));
                log.info("this.ud:{},map.ud:{}",this.ud,map.get("ud"));
                //延迟开奖
                this.udTs = Integer.parseInt(map.get("udTs"));
                if(!StringUtils.equals(this.ud,map.get("ud"))){
                    //已经开奖
                    List<Integer> lr = JSON.parseArray(map.get("lr"), Integer.class);
                    /**
                     *  结果相关
                     */
                    String ld = map.get("ld");
                    this.cd = map.get("cd");
                    this.ud = map.get("ud");
                    this.nd = map.get("nd");
                    this.cdTs = Integer.parseInt(map.get("cdTs"));

                    //log.info("ld:{}，lr:{},cd:{},cdTs:{},ud:{},udTs:{},nd:{}", ld,lr,this.cd,this.cdTs,map.get("ud"),map.get("udTs"),map.get("nd"));
                    if(!ObjectUtils.isEmpty(JcqApp.CQ)){
                        //兑奖
                        buyService.compare(ld,lr);
                        List<Buy> currentNumAndStatus = buyRepository.findAllByCurrentNumAndStatus(ld, new Short("1"));
                        StringBuilder builder = new StringBuilder(String.format("攻击结果：%s-->%s\n喜报：\n", ld,lr));
                        for (Buy buy : currentNumAndStatus) {
                            builder.append(buy.getUserId()).append("/").append(buy.getContent()).append("/").append(buy.getScore()).append("/").append(buy.getWin()).append("\n");
                        }
                        List<Group> groupList = JcqApp.CQ.getGroupList();
                        for (Group group : groupList) {
                            JcqApp.CQ.sendGroupMsg(group.getId(),builder.toString());
                        }
                    }
                }
                log.info("udTs执行0");
            }catch (Exception e){
                log.error(ExceptionUtils.getStackTrace(e));
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        //当前冷却时间
        if(cdTs <= 0){
            //改为10分钟(待会udTs到期再同步)
            this.cdTs = 600;
            //进入下一期投注
            List<Group> groupList = JcqApp.CQ.getGroupList();
            for (Group group : groupList) {
                JcqApp.CQ.sendGroupMsg(group.getId(),String.format("攻击开始：%s",this.cd));
                JcqApp.CQ.setGroupWholeBan(group.getId(),Boolean.FALSE);
            }
            //已开奖才执行下面的（防止延迟开奖超过40秒）
            //将新的攻击设置为打开禁言
            this.isNotWholeBean = Boolean.TRUE;
            //将本次攻击设置为未提示过
            this.isNotTip = Boolean.TRUE;
            log.info("cdTs执行0");
        } else if(cdTs <= 10 && this.isNotWholeBean){
            List<Group> groupList = JcqApp.CQ.getGroupList();
            for (Group group : groupList) {
                JcqApp.CQ.sendGroupMsg(group.getId(), String.format("%s攻击结束", this.cd));
                JcqApp.CQ.setGroupWholeBan(group.getId(),Boolean.TRUE);
            }
            //转化攻击信息
            AppConstant.contentNum.forEach((key, value) -> buyRepository.updateNumByContentAndCurrentNum(key, this.cd, value));
            //转换之后立即切换当前期数(防止管理员乱搞)
            this.cd = this.nd;

            //将本次攻击设置为禁言
            this.isNotWholeBean = Boolean.FALSE;
            //将本次攻击设置为已经提示过
            this.isNotTip = Boolean.FALSE;
            log.info("获取结果10");
        }else if(cdTs <= 40 && this.isNotTip){
            List<Group> groupList = JcqApp.CQ.getGroupList();
            for (Group group : groupList) {
                JcqApp.CQ.sendGroupMsg(group.getId(), String.format("%s剩余攻击时间：30秒", this.cd));
            }
            //将本次攻击设置为已经提示过
            this.isNotTip = Boolean.FALSE;
            log.info("执行提示40");
        }
    }



    /**
     * 请求相关
     */
    private MediaType okJSON= MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    private RequestBody body = RequestBody.create(okJSON, "");

    /**
     * post请求
     * @param builder
     * @return
     * @throws IOException
     */
    private String post(Request.Builder builder) throws Exception {
        Request request = builder
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        }
    }


    /**
     * 任务初始化，暂无作用:PostConstruct
     * @throws Exception
     */
    //@PostConstruct
    public void init() throws Exception {
        System.out.println("初始化我开始");
        Thread.sleep(2000);
        System.out.println("初始化我结束");
    }

    public String getCd() {
        return cd;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
