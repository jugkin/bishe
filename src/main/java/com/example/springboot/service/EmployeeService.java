package com.example.springboot.service;

import com.example.springboot.entity.Employee;
import com.example.springboot.mapper.EmployeeMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    @Resource
    private EmployeeMapper employeeMapper;
    public List<Employee> selectAll(Employee employee) {
        List<Employee> list = employeeMapper.selectAll(employee);
        return list;
    }

    public Employee selectByid(Integer id) {
        return employeeMapper.selectByid(id);
    }

    public PageInfo<Employee> selectPage(Employee employee,Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize );
          List<Employee> list =    employeeMapper.selectAll(employee);
          return new PageInfo<>(list);

    }

    public void add(Employee employee) {
        employeeMapper.insert(employee);
    }

    public void update(Employee employee) {
        employeeMapper.updateById(employee);
    }

    public void deleteById(Integer id) {
        employeeMapper.deleteById(id);
    }
}
