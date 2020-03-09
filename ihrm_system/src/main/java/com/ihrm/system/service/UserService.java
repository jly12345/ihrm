package com.ihrm.system.service;

import com.ihrm.common.utils.IdWorker;
import com.ihrm.domain.company.Department;
import com.ihrm.domain.system.Role;
import com.ihrm.domain.system.User;
import com.ihrm.system.dao.RoleDao;
import com.ihrm.system.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private IdWorker idWorker;

    /**
     * save user
     */
    public void save(User user) {
        String id = idWorker.nextId() + "";
        user.setId(id);
        user.setPassword("123456");
        user.setEnableState(1);
        userDao.save(user);
    }

    public User findbyMobile(String mobile){
        return userDao.findByMobile(mobile);
    }

    /**
     * update user
     */
    public void update(User user) {
        Optional<User> optional = userDao.findById(user.getId());
        if (optional.isPresent()) {
            User po = optional.get();
            po.setUsername(user.getUsername());
            po.setPassword(user.getPassword());
            po.setDepartmentId(user.getDepartmentId());
            po.setDepartmentName(user.getDepartmentName());

            userDao.save(po);
        }

    }

    /**
     * 根基Id查询user
     */
    public User findById(String id) {
        Optional<User> optional = userDao.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    /**
     * 查询全部user
     */
    public Page<User> findAll(Map<String,Object> param, int page,int size) {
        /**
         * 用户构造查询条件
         *      1.只查询companyId
         *      2.很多的地方都需要根据companyId查询
         *      3.很多的对象中都具有companyId
         *
         */
        Specification<User> spc = (Specification<User>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(!StringUtils.isEmpty(param.get("companyId"))){
                predicates.add(criteriaBuilder.equal(root.get("companyId").as(String.class),param.get("companyId")));
            }

            if(!StringUtils.isEmpty(param.get("departmentId"))){
                predicates.add(criteriaBuilder.equal(root.get("departmentId").as(String.class),param.get("departmentId")));
            }
            if(!StringUtils.isEmpty(param.get("hasDept"))) {
                //根据请求的hasDept判断  是否分配部门 0未分配（departmentId = null），1 已分配 （departmentId ！= null）
                if("0".equals(param.get("hasDept"))) {
                    predicates.add(criteriaBuilder.isNull(root.get("departmentId")));
                }else {
                    predicates.add(criteriaBuilder.isNotNull(root.get("departmentId")));
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<User> all = userDao.findAll(spc, PageRequest.of(page-1,size));
        return all;
    }


    /**
     * 根基Id删除
     */
    public void deleteById(String id) {
        userDao.deleteById(id);
    }

    /**
     * 分配角色
     * @param userId
     * @param roleIds
     */
    public void assignRoles(String userId, List<String> roleIds) {
        Optional<User> optional = userDao.findById(userId);
        if(optional.isPresent()){
            User user = optional.get();
            Set<Role> roles = new HashSet<>();
            for(String roleId: roleIds){
                Optional<Role> optionalRole = roleDao.findById(roleId);
                if(optionalRole.isPresent()){
                    roles.add(optionalRole.get());
                }
            }

            user.setRoles(roles);
            userDao.save(user);
        }
    }
}
