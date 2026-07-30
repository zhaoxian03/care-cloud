package com.neusoft.care.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单节点VO - 用于构建前端菜单树结构
 *
 * 核心逻辑：
 * 1. 通过树形结构组织菜单，支持无限层级嵌套
 * 2. 由AdminServiceImpl将Permission实体树转换为此VO
 *
 * 注意事项：实现Serializable接口，支持Redis缓存序列化
 *
 * @author CareCenter Team
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuNode implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String code;

    private String type;

    private String path;

    private String icon;

    private List<MenuNode> children;
}
