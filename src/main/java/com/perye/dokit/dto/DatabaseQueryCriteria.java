package com.perye.dokit.dto;

import com.perye.dokit.annotation.Query;
import lombok.Data;

/**
 * @author perye
 * @email peryedev@gmail.com
 * @date 2019/12/10
 */
@Data
public class DatabaseQueryCriteria{

    /**
     * 模糊
     */
    @Query(type = Query.Type.INNER_LIKE)
    private String name;

    /**
     * 精确
     */
    @Query
    private String jdbcUrl;
}
