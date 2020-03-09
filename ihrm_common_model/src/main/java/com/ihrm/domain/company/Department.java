package com.ihrm.domain.company;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.io.Serializable;

/**
 * (CoDepartment)实体类
 *
 * @author makejava
 * @since 2020-03-06 17:25:10
 */
@Entity
@Table(name = "co_department")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department implements Serializable {
    private static final long serialVersionUID = 715693422955626479L;
    @Id
    private String id;
    /**
    * 企业ID
    */
    private String companyId;
    /**
    * 父级部门ID
    */
    private String pid;
    /**
    * 部门名称
    */
    private String name;
    /**
    * 部门编码
    */
    private String code;
    /**
    * 部门负责人
    */
    private String manager;
    /**
    * 介绍
    */
    private String introduce;
    /**
    * 创建时间
    */
    private Date createTime;
    /**
    * 负责人ID
    */
    private String managerId;


}