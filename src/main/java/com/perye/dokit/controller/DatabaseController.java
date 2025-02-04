package com.perye.dokit.controller;

import com.perye.dokit.annotation.AnonymousAccess;
import com.perye.dokit.aop.log.Log;
import com.perye.dokit.dto.DatabaseDto;
import com.perye.dokit.dto.DatabaseQueryCriteria;
import com.perye.dokit.entity.Database;
import com.perye.dokit.exception.BadRequestException;
import com.perye.dokit.service.DatabaseService;
import com.perye.dokit.utils.FileUtil;
import com.perye.dokit.utils.SqlUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

@Api(tags = "数据库管理")
@RestController
@RequestMapping("/api/database")
public class DatabaseController {

    private String fileSavePath = System.getProperty("java.io.tmpdir");

    private final DatabaseService databaseService;

    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Log("查询数据库")
    @ApiOperation(value = "查询数据库")
    @GetMapping
    @PreAuthorize("@dokit.check('database:list')")
    public ResponseEntity getDatabases(DatabaseQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(databaseService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("新增数据库")
    @ApiOperation(value = "新增数据库")
    @PostMapping
    @PreAuthorize("@dokit.check('database:add')")
    public ResponseEntity create(@Validated @RequestBody Database resources){
        return new ResponseEntity<>(databaseService.create(resources),HttpStatus.CREATED);
    }

    @Log("修改数据库")
    @ApiOperation(value = "修改数据库")
    @PutMapping
    @PreAuthorize("@dokit.check('database:edit')")
    public ResponseEntity update(@Validated @RequestBody Database resources){
        databaseService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Log("删除数据库")
    @ApiOperation(value = "删除数据库")
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("@dokit.check('database:del')")
    public ResponseEntity delete(@PathVariable String id){
        databaseService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Log("测试数据库链接")
    @ApiOperation(value = "测试数据库链接")
    @PostMapping("/testConnect")
    @PreAuthorize("@dokit.check('database:testConnect')")
    public ResponseEntity testConnect(@Validated @RequestBody Database resources){
        return new ResponseEntity<>(databaseService.testConnection(resources),HttpStatus.CREATED);
    }

    @Log("执行SQL脚本")
    @ApiOperation(value = "执行SQL脚本")
    @PostMapping(value = "/upload")
//    @PreAuthorize("@dokit.check('database:add')")
    @AnonymousAccess
    public ResponseEntity upload(@RequestBody MultipartFile file, HttpServletRequest request)throws Exception{
        String id = request.getParameter("id");
        DatabaseDto database = databaseService.findById(id);
        String fileName = "";
        if(database != null){
            fileName = file.getOriginalFilename();
            File executeFile = new File(fileSavePath+fileName);
            FileUtil.del(executeFile);
            file.transferTo(executeFile);
            String result = SqlUtils.executeFile(database.getJdbcUrl(), database.getUserName(), database.getPwd(), executeFile);
            return new ResponseEntity<>(result,HttpStatus.OK);
        }else{
            throw new BadRequestException("Database not exist");
        }
    }
}
