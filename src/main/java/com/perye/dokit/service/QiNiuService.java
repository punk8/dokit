package com.perye.dokit.service;

import com.perye.dokit.dto.QiniuQueryCriteria;
import com.perye.dokit.entity.QiniuConfig;
import com.perye.dokit.entity.QiniuContent;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface QiNiuService {

    Object queryAll(QiniuQueryCriteria criteria, Pageable pageable);

    List<QiniuContent> queryAll(QiniuQueryCriteria criteria);


    /**
     * 查配置
     * @return Cacheable
     */
    QiniuConfig find();

    /**
     * 修改配置
     * @param qiniuConfig 配置
     * @return QiniuConfig
     */
    QiniuConfig update(QiniuConfig qiniuConfig);

    /**
     * 上传文件
     * @param file 文件
     * @param qiniuConfig 配置
     * @return QiniuConfig
     */
    QiniuContent upload(MultipartFile file, QiniuConfig qiniuConfig);

    /**
     * 查询文件
     * @param id 文件ID
     * @return QiniuConfig
     */
    QiniuContent findByContentId(Long id);

    /**
     * 下载文件
     * @param content 文件信息
     * @param config 配置
     * @return String
     */
    String download(QiniuContent content, QiniuConfig config);

    /**
     * 删除文件
     * @param content 文件
     * @param config 配置
     * @return
     */
    void delete(QiniuContent content, QiniuConfig config);

    /**
     * 同步数据
     * @param config 配置
     */
    void synchronize(QiniuConfig config);

    /**
     * 删除文件
     * @param ids 文件ID数组
     * @param config 配置
     */
    void deleteAll(Long[] ids, QiniuConfig config);

    /**
     * 更新数据
     * @param type 类型
     */
    void update(String type);


    /**
     * 导出数据
     * @param queryAll /
     * @param response /
     */
    void downloadList(List<QiniuContent> queryAll, HttpServletResponse response) throws IOException;
}

