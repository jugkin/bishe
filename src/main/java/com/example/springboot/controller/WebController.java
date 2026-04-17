package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.exception.CustomException;
import org.apache.ibatis.ognl.internal.HashMapCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class WebController {
    @GetMapping("/hello")
    public Result hello() {
        return Result.success("hello");
    } @GetMapping("/weather")
    public Result weather() {
        return Result.success("weather");
    }@GetMapping("/count")
    public Result count() {
         throw new CustomException("400","错误请求");
        //return Result.success(10);
    }@GetMapping("/map")
    public Result map() {
        HashMap<String,Object> map=new HashMap<>();
        map.put("name","wu");
        map.put("age",22);
        return Result.success(map);
    }


}
