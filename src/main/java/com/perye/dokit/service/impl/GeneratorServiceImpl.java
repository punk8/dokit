package com.perye.dokit.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ZipUtil;
import com.perye.dokit.entity.ColumnInfo;
import com.perye.dokit.entity.GenConfig;
import com.perye.dokit.exception.BadRequestException;
import com.perye.dokit.repository.ColumnInfoRepository;
import com.perye.dokit.service.GeneratorService;
import com.perye.dokit.utils.FileUtil;
import com.perye.dokit.utils.GenUtil;
import com.perye.dokit.utils.PageUtil;
import com.perye.dokit.utils.StringUtils;
import com.perye.dokit.vo.TableInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings({"unchecked","all"})
public class GeneratorServiceImpl implements GeneratorService {

    @PersistenceContext
    private EntityManager em;

    private final ColumnInfoRepository columnInfoRepository;

    public GeneratorServiceImpl(ColumnInfoRepository columnInfoRepository) {
        this.columnInfoRepository = columnInfoRepository;
    }

    @Override
    public Object getTables() {
        // 使用预编译防止sql注入
        String sql = "select table_name ,create_time , engine, table_collation, table_comment from information_schema.tables " +
                "where table_schema = (select database()) " +
                "order by create_time desc";
        Query query = em.createNativeQuery(sql);
        return query.getResultList();
    }

    @Override
    public Object getTables(String name, int[] startEnd) {
        // 使用预编译防止sql注入
        String sql = "select table_name ,create_time , engine, table_collation, table_comment from information_schema.tables " +
                "where table_schema = (select database()) " +
                "and table_name like ? order by create_time desc";
        Query query = em.createNativeQuery(sql);
        query.setFirstResult(startEnd[0]);
        query.setMaxResults(startEnd[1]-startEnd[0]);
        query.setParameter(1, StringUtils.isNotBlank(name) ? ("%" + name + "%") : "%%");
        List result = query.getResultList();
        List<TableInfo> tableInfos = new ArrayList<>();
        for (Object obj : result) {
            Object[] arr = (Object[]) obj;
            tableInfos.add(new TableInfo(arr[0],arr[1],arr[2],arr[3], ObjectUtil.isNotEmpty(arr[4])? arr[4] : "-"));
        }
        Query query1 = em.createNativeQuery("SELECT COUNT(*) from information_schema.tables where table_schema = (select database())");
        Object totalElements = query1.getSingleResult();
        return PageUtil.toPage(tableInfos,totalElements);
    }

    @Override
    public List<ColumnInfo> getColumns(String tableName) {
        List<ColumnInfo> columnInfos = columnInfoRepository.findByTableNameOrderByIdAsc(tableName);
        if(CollectionUtil.isNotEmpty(columnInfos)){
            return columnInfos;
        } else {
            columnInfos = query(tableName);
            return columnInfoRepository.saveAll(columnInfos);
        }
    }

    public List<ColumnInfo> query(String tableName){
        // 使用预编译防止sql注入
        String sql = "select column_name, is_nullable, data_type, column_comment, column_key, extra from information_schema.columns " +
                "where table_name = ? and table_schema = (select database()) order by ordinal_position";
        Query query = em.createNativeQuery(sql);
        query.setParameter(1,tableName);
        List result = query.getResultList();
        List<ColumnInfo> columnInfos = new ArrayList<>();
        for (Object obj : result) {
            Object[] arr = (Object[]) obj;
            columnInfos.add(
                    new ColumnInfo(
                            tableName,
                            arr[0].toString(),
                            "NO".equals(arr[1]),
                            arr[2].toString(),
                            ObjectUtil.isNotNull(arr[3]) ? arr[3].toString() : null,
                            ObjectUtil.isNotNull(arr[4]) ? arr[4].toString() : null,
                            ObjectUtil.isNotNull(arr[5]) ? arr[5].toString() : null)
            );
        }
        return columnInfos;
    }

    @Override
    public void sync(List<ColumnInfo> columnInfos) {

    }

    @Override
    public void save(List<ColumnInfo> columnInfos) {
        columnInfoRepository.saveAll(columnInfos);
    }

    @Override
    public void generator(GenConfig genConfig, List<ColumnInfo> columns) {
        if(genConfig.getId() == null){
            throw new BadRequestException("请先配置生成器");
        }
        try {
            GenUtil.generatorCode(columns, genConfig);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BadRequestException("生成失败，请手动处理已生成的文件");
        }
    }

    @Override
    public ResponseEntity preview(GenConfig genConfig, List<ColumnInfo> columns) {
        if(genConfig.getId() == null){
            throw new BadRequestException("请先配置生成器");
        }
        List<Map<String,Object>> genList =  GenUtil.preview(columns, genConfig);
        return new ResponseEntity<>(genList, HttpStatus.OK);
    }

    @Override
    public void download(GenConfig genConfig, List<ColumnInfo> columns, HttpServletRequest request, HttpServletResponse response) {
        if(genConfig.getId() == null){
            throw new BadRequestException("请先配置生成器");
        }
        try {
            File file = new File(GenUtil.download(columns, genConfig));
            String zipPath = file.getPath()  + ".zip";
            ZipUtil.zip(file.getPath(), zipPath);
            FileUtil.downloadFile(request, response, new File(zipPath), true);
        } catch (IOException e) {
            throw new BadRequestException("打包失败");
        }
    }
}

