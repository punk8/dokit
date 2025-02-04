package com.perye.dokit.entity;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import java.io.Serializable;

/**
 * @author perye
 * @email peryedev@gmail.com
 * @date 2019/12/10 10:55 下午
 */
@Entity
@Data
@Table(name="mnt_database")
public class Database implements Serializable {

    /**
     * id
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 数据库名称
     */
    @Column(name = "name",nullable = false)
    private String name;

    /**
     * 数据库连接地址
     */
    @Column(name = "jdbc_url",nullable = false)
    private String jdbcUrl;

    /**
     * 数据库密码
     */
    @Column(name = "pwd",nullable = false)
    private String pwd;

    /**
     * 用户名
     */
    @Column(name = "user_name",nullable = false)
    private String userName;


    public void copy(Database source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
