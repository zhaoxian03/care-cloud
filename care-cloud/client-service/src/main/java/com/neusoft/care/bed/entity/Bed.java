package com.neusoft.care.bed.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 床位实体类 - 对应数据库 bed 表
 * 
 * 功能说明：存储养老院床位信息，包括房间号、床号、状态等
 * 实现Serializable接口，支持Redis缓存序列化
 * 
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("bed")
public class Bed implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 床位ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 房间号（如 A101） */
    private String roomNumber;

    /** 床号（如 01） */
    private String bedNumber;

    /** 楼层 */
    private Integer floor;

    /** 朝向（南/北/东/西等） */
    private String orientation;

    /** 状态（0-空闲，1-占用） */
    private Integer status;

    /** 备注 */
    private String remark;

    /** 逻辑删除标志（0-正常，1-已删除）
     * MyBatis-Plus 的逻辑删除注解。
     * MyBatis-Plus 会自动把 delete 操作转为 UPDATE，
     * */
    @TableLogic
    private Integer isDeleted;
}
