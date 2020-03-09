package com.ihrm.company;

import com.ihrm.company.dao.CompanyDao;
import com.ihrm.company.service.CompanyService;
import com.ihrm.domain.company.Company;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class CompanyDaoTest {
    @Autowired
    private CompanyService companyService;

    @Test
    public void test() {

        //save(company) ;  保存或更新（id）
        //deleteByIid); 根据id删除
        //findById（id）；根据id查询
        //findAll() 查询全部

        Company company = new Company();
        company.setName("传智播客");
        company.setManagerId("123456");
        company.setBalance(5000d);
        company.setBusinessLicenseId("license123");
        company.setCompanyAddress("成都高新区");
        company.setCompanyArea("成都高新");
        company.setCompanySize("500");
        company.setCompanyPhone("400-500-600");
        company.setCreateTime(new Date());
        companyService.add(company);
        System.out.println("success");
    }
}
