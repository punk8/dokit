package com.perye.dokit.controller;

import com.perye.dokit.aop.log.Log;
import com.perye.dokit.service.RedisService;
import com.perye.dokit.vo.RedisVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/redis")
@Api(tags = "系统：Redis缓存管理")
public class RedisController {

    private final RedisService redisService;

    public RedisController(RedisService redisService) {
        this.redisService = redisService;
    }

    @Log("查询Redis缓存")
    @GetMapping
    @ApiOperation("查询Redis缓存")
    @PreAuthorize("@dokit.check('redis:list')")
    public ResponseEntity getRedis(String key, Pageable pageable){
        return new ResponseEntity<>(redisService.findByKey(key,pageable), HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@dokit.check('redis:list')")
    public void download(HttpServletResponse response, String key) throws IOException {
        redisService.download(redisService.findByKey(key), response);
    }

    @Log("删除Redis缓存")
    @DeleteMapping
    @ApiOperation("删除Redis缓存")
    @PreAuthorize("@dokit.check('redis:del')")
    public ResponseEntity<Object> delete(@RequestBody RedisVo resources){
        redisService.delete(resources.getKey());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("清空Redis缓存")
    @DeleteMapping(value = "/all")
    @ApiOperation("清空Redis缓存")
    @PreAuthorize("@dokit.check('redis:del')")
    public ResponseEntity<Object> deleteAll(){
        redisService.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

