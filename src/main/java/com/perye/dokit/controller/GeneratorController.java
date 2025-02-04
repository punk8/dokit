package com.perye.dokit.controller;

import com.perye.dokit.entity.ColumnInfo;
import com.perye.dokit.exception.BadRequestException;
import com.perye.dokit.service.GenConfigService;
import com.perye.dokit.service.GeneratorService;
import com.perye.dokit.utils.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/generator")
@Api(tags = "系统：代码生成管理")
public class GeneratorController {

    private final GeneratorService generatorService;

    private final GenConfigService genConfigService;

    public GeneratorController(GeneratorService generatorService, GenConfigService genConfigService) {
        this.generatorService = generatorService;
        this.genConfigService = genConfigService;
    }

    @Value("${generator.enabled}")
    private Boolean generatorEnabled;

    @ApiOperation("查询数据库数据")
    @GetMapping(value = "/tables/all")
    public ResponseEntity<Object> getTables(){
        return new ResponseEntity<>(generatorService.getTables(), HttpStatus.OK);
    }


    @ApiOperation("查询数据库数据")
    @GetMapping(value = "/tables")
    public ResponseEntity<Object> getTables(@RequestParam(defaultValue = "") String name,
                                    @RequestParam(defaultValue = "0")Integer page,
                                    @RequestParam(defaultValue = "10")Integer size){
        int[] startEnd = PageUtil.transToStartEnd(page+1, size);
        return new ResponseEntity<>(generatorService.getTables(name,startEnd), HttpStatus.OK);
    }

    @ApiOperation("查询字段数据")
    @GetMapping(value = "/columns")
    public ResponseEntity<Object> getTables(@RequestParam String tableName){
        List<ColumnInfo> columnInfos = generatorService.getColumns(tableName);
        // 异步同步表信息
        generatorService.sync(columnInfos);
        return new ResponseEntity<>(PageUtil.toPage(columnInfos,columnInfos.size()), HttpStatus.OK);
    }

    @ApiOperation("保存字段数据")
    @PutMapping
    public ResponseEntity<HttpStatus>  save(@RequestBody List<ColumnInfo> columnInfos){
        // 异步同步表信息
        generatorService.save(columnInfos);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("生成代码")
    @PostMapping(value = "/{tableName}/{type}")
    public ResponseEntity<Object>  generator(@PathVariable String tableName, @PathVariable Integer type, HttpServletRequest request, HttpServletResponse response){
        if(!generatorEnabled && type == 0){
            throw new BadRequestException("此环境不允许生成代码，请选择预览或者下载查看！");
        }
        switch (type){
            // 生成代码
            case 0: generatorService.generator(genConfigService.find(tableName), generatorService.getColumns(tableName));
                break;
            // 预览
            case 1: return generatorService.preview(genConfigService.find(tableName), generatorService.getColumns(tableName));
            // 打包
            case 2: generatorService.download(genConfigService.find(tableName), generatorService.getColumns(tableName), request, response);
                break;
            default: throw new BadRequestException("没有这个选项");
        }        return new ResponseEntity<>(HttpStatus.OK);
    }
}

