package com.sure.reggie;

import com.sure.reggie.service.impl.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReggieTest {

    @Autowired
    private EmployeeService service;

    @Test
    public void employeeServiceTest() {
        service.toString();
    }


}
