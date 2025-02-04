package com.perye.dokit.service;

import com.perye.dokit.dto.AppDto;
import com.perye.dokit.dto.AppQueryCriteria;
import com.perye.dokit.entity.App;
import org.springframework.data.domain.Pageable;

/**
 * @author perye
 * @email peryedev@gmail.com
 * @date 2019/12/10
 */
public interface AppService {

    /**
     * 分页查询
     * @param criteria 条件
     * @param pageable 分页参数
     * @return /
     */
    Object queryAll(AppQueryCriteria criteria, Pageable pageable);

    /**
     * 查询全部数据
     * @param criteria 条件
     * @return /
     */
    Object queryAll(AppQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id /
     * @return /
     */
    AppDto findById(Long id);

    /**
     * 创建
     * @param resources /
     * @return /
     */
    AppDto create(App resources);

    /**
     * 编辑
     * @param resources /
     */
    void update(App resources);

    /**
     * 删除
     * @param id /
     */
    void delete(Long id);
}
