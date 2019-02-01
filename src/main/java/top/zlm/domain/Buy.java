package top.zlm.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table( name = "buy")
@NoArgsConstructor
@AllArgsConstructor
public class Buy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint comment '主键ID'")
    private Long id;


    @Column(name = "user_id", columnDefinition = "varchar(20) comment '用户ID'")
    private Long userId;

    @Column(name = "type", columnDefinition = "tinyint comment '类型'")
    private Short type;

    @Column(name = "content", columnDefinition = "varchar(50) comment '内容'")
    private String content;

    @Column(name = "num", columnDefinition = "varchar(50) comment '号码'")
    private String num;

    @Column(name = "score", columnDefinition = "decimal(10,2) comment '分数'")
    private Double score;

    @Column(name = "status", columnDefinition = "tinyint comment '状态'")
    private Short status;

    @Column(name = "win", columnDefinition = "decimal(10,2) comment '获得分数'")
    private Double win;

    @Column(name = "create_time", columnDefinition = "datetime comment '创建时间'")
    private LocalDateTime createTime;

    @Column(name = "current_num", columnDefinition = "varchar(50) comment '当前级数'")
    private String currentNum;

    @Column(name = "result", columnDefinition = "varchar(10) comment '结果'")
    private String result;

    public Buy(Long userId, Short type, String content, Double score, Short status, Double win, LocalDateTime createTime, String currentNum) {
        this.userId = userId;
        this.type = type;
        this.content = content;
        this.score = score;
        this.status = status;
        this.win = win;
        this.createTime = createTime;
        this.currentNum = currentNum;
    }

}
