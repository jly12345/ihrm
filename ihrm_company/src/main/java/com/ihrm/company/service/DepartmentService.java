package com.ihrm.company.service;

import com.ihrm.common.service.BaseService;
import com.ihrm.common.utils.IdWorker;
import com.ihrm.company.dao.DepartmentDao;
import com.ihrm.domain.company.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService extends BaseService {

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * save Department
     */
    public void save(Department department) {
        String id = idWorker.nextId() + "";
        department.setId(id);
        departmentDao.save(department);
    }

    /**
     * update Department
     */
    public void update(Department department) {
        Optional<Department> optional = departmentDao.findById(department.getId());
        if (optional.isPresent()) {
            Department po = optional.get();
            po.setCode(department.getCode());
            po.setCompanyId(department.getCompanyId());
            po.setCreateTime(department.getCreateTime());
            po.setIntroduce(department.getIntroduce());
            po.setManager(department.getManager());
            po.setManagerId(department.getManagerId());
            po.setName(department.getName());
            po.setPid(department.getPid());
            departmentDao.save(po);
        }

    }

    /**
     * 根基Id查询部门
     */
    public Department findById(String id) {
        Optional<Department> optional = departmentDao.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    /**
     * 查询全部部门列表
     */
    public List<Department> findAll(String companyId) {
        /**
         * 用户构造查询条件
         *      1.只查询companyId
         *      2.很多的地方都需要根据companyId查询
         *      3.很多的对象中都具有companyId
         *
         */

        return departmentDao.findAll(getSpc(companyId));
    }


    /**
     * 根基Id删除
     */
    public void deleteById(String id) {
        departmentDao.deleteById(id);
    }

}
