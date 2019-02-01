package top.zlm.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table( name = "user")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "id", columnDefinition = "varchar(20) comment '主键ID'")
    private Long id;

    @Column(name = "score", columnDefinition = "decimal(10,2) default 0 comment '分数'")
    private Double score;

}