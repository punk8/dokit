package com.perye.dokit.repository;

import com.perye.dokit.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

@SuppressWarnings("all")
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    /**
     * 根据名称查询
     * @param name /
     * @return /
     */
    Role findByName(String name);

    /**
     * 根据用户ID查询
     * @param id 用户ID
     * @return
     */
    Set<Role> findByUsers_Id(Long id);


    /**
     * 解绑角色菜单
     * @param id 菜单ID
     */
    @Modifying
    @Query(value = "delete from roles_menus where menu_id = ?1",nativeQuery = true)
    void untiedMenu(Long id);
}

