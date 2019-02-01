package top.zlm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 开启定时任务
 */
@Configuration
@EnableScheduling
@ComponentScan(basePackages = {"top.zlm.task"})
public class TaskConfig {

    /**
     * 给定时任务一个线程池，就个一个线程即可
     * @return
     */
    @Bean
    public TaskScheduler taskScheduler(){
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.initialize();
        return taskScheduler;
    }

}
