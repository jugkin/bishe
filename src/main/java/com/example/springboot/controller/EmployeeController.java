package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Employee;
import com.example.springboot.service.EmployeeService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
   @Resource
    private EmployeeService employeeService;
//查询所有
   @GetMapping("/selectAll")
    public Result selectAll(Employee employee) {
       List<Employee> list= employeeService.selectAll(employee);
       return Result.success(list);
   }
//查询单个
   @GetMapping("/selectAllById/{id}")
    public Result selectAllById(@PathVariable Integer id) {
       Employee employee= employeeService.selectByid(id);
       return Result.success(employee);
   }
   //分页查询pagenum 当前的页码，pagesize没页的个数
    @GetMapping("/selectPage")
    public Result selectPage(Employee employee,
            @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
      PageInfo<Employee> pageInfo = employeeService.selectPage(employee,pageNum,pageSize);
        return Result.success(pageInfo);
    }
    //新增加
    @PostMapping("/add")
    public Result add(@RequestBody Employee employee) {
       employeeService.add(employee);
       return Result.success();

    }
  //更新数据
    @PutMapping("/update")
    public Result update(@RequestBody Employee employee) {
       employeeService.update(employee);
       return Result.success();

    }//删除数据
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
       employeeService.deleteById(id);
       return Result.success();

    }





}
