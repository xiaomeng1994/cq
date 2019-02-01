import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import top.zlm.config.AppConfig;
import top.zlm.dao.BuyRepository;
import top.zlm.domain.Buy;
import top.zlm.domain.User;
import top.zlm.service.BuyService;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyTest {

    @Test
    public void test(){
        Pattern compile = Pattern.compile("(.*)/(.*)");
        String text = "145/95";
        Matcher matcher = compile.matcher(text);
        if (matcher.find()) {
            System.out.println(matcher.group(1) + "---" + matcher.group(2));
        }
    }

    @Test
    public void test1() throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        Thread.sleep(Integer.MAX_VALUE);
    }

    @Test
    public void test2() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        System.out.println(Thread.currentThread().getName());

        BuyService buyService = context.getBean(BuyService.class);
        BuyRepository buyRepository = context.getBean(BuyRepository.class);

//        buyService.compare("20190104-056",ImmutableList.of(7,6,9,2,3));

        List<Buy> allByCurrentNumAndStatus = buyRepository.findAllByCurrentNumAndStatus("20190104-061", new Short("1"));
        StringBuilder builder = new StringBuilder("击中结果：\n");
        for (Buy buy : allByCurrentNumAndStatus) {
            builder.append(buy.getUserId()).append("/").append(buy.getContent()).append("/").append(buy.getScore()).append("/").append(buy.getWin()).append("\n");
        }
        System.out.println(builder.toString());

//        Thread.sleep(20000);

    }


    @Test
    public void test3() throws Exception {
        //AppConstant.contentNum.entrySet().forEach(entry -> System.out.println(entry.getKey() + "----" + entry.getValue()));
//        ImmutableList<Integer> of = ImmutableList.of(1,5,7,8,5);
//        System.out.println(ArrayUtils.toString(of));
//        System.out.println(String.valueOf(of.get(4)));
//        if(StringUtils.contains("56489",String.valueOf(of.get(4)))){
//            System.out.println("搜索到了");
//        }
        User user = new User(1L, 0.0);
        System.out.println(user);

    }




}
