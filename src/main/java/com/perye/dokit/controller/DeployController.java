package com.perye.dokit.controller;

import com.perye.dokit.aop.log.Log;
import com.perye.dokit.dto.DeployQueryCriteria;
import com.perye.dokit.entity.Deploy;
import com.perye.dokit.entity.DeployHistory;
import com.perye.dokit.service.DeployService;
import com.perye.dokit.utils.FileUtil;
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
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author perye
 * @email peryedev@gmail.com
 * @date 2019/12/10 11:29 下午
 */
@Api(tags = "部署管理")
@RestController
@RequestMapping("/api/deploy")
public class DeployController {

    private String fileSavePath = System.getProperty("java.io.tmpdir");

    private final DeployService deployService;

    public DeployController(DeployService deployService) {
        this.deployService = deployService;
    }

    @Log("查询部署")
    @ApiOperation(value = "查询部署")
    @GetMapping
    @PreAuthorize("@dokit.check('deploy:list')")
    public ResponseEntity<Object> getDeploys(DeployQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(deployService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("新增部署")
    @ApiOperation(value = "新增部署")
    @PostMapping
    @PreAuthorize("@dokit.check('deploy:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Deploy resources){
        return new ResponseEntity<>(deployService.create(resources),HttpStatus.CREATED);
    }

    @Log("修改部署")
    @ApiOperation(value = "修改部署")
    @PutMapping
    @PreAuthorize("@dokit.check('deploy:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody Deploy resources){
        deployService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除部署")
    @ApiOperation(value = "删除部署")
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("@dokit.check('deploy:del')")
    public ResponseEntity<Object> delete(@PathVariable Long id){
        deployService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("上传文件部署")
    @ApiOperation(value = "上传文件部署")
    @PostMapping(value = "/upload")
    @PreAuthorize("@dokit.check('deploy:edit')")
    public ResponseEntity<Object> upload(@RequestBody MultipartFile file, HttpServletRequest request)throws Exception{
        Long id = Long.valueOf(request.getParameter("id"));
        String fileName = "";
        if(file != null){
            fileName = file.getOriginalFilename();
            File deployFile = new File(fileSavePath+fileName);
            FileUtil.del(deployFile);
            file.transferTo(deployFile);
            //文件下一步要根据文件名字来
            deployService.deploy(fileSavePath+fileName ,id);
        }else{
            System.out.println("没有找到相对应的文件");
        }
        System.out.println("文件上传的原名称为:"+ Objects.requireNonNull(file).getOriginalFilename());
        Map<String,Object> map = new HashMap<>(2);
        map.put("errno",0);
        map.put("id",fileName);
        return new ResponseEntity<>(map,HttpStatus.OK);
    }
    @Log("系统还原")
    @ApiOperation(value = "系统还原")
    @PostMapping(value = "/serverReduction")
    @PreAuthorize("@dokit.check('deploy:edit')")
    public ResponseEntity<Object> serverReduction(@Validated @RequestBody DeployHistory resources){
        String result = deployService.serverReduction(resources);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
    @Log("服务运行状态")
    @ApiOperation(value = "服务运行状态")
    @PostMapping(value = "/serverStatus")
    @PreAuthorize("@dokit.check('deploy:edit')")
    public ResponseEntity<Object> serverStatus(@Validated @RequestBody Deploy resources){
        String result = deployService.serverStatus(resources);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
    @Log("启动服务")
    @ApiOperation(value = "启动服务")
    @PostMapping(value = "/startServer")
    @PreAuthorize("@dokit.check('deploy:edit')")
    public ResponseEntity<Object> startServer(@Validated @RequestBody Deploy resources){
        String result = deployService.startServer(resources);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
    @Log("停止服务")
    @ApiOperation(value = "停止服务")
    @PostMapping(value = "/stopServer")
    @PreAuthorize("@dokit.check('deploy:edit')")
    public ResponseEntity<Object> stopServer(@Validated @RequestBody Deploy resources){
        String result = deployService.stopServer(resources);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
}
