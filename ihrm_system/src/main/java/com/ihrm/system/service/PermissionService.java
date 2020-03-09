package com.ihrm.system.service;

import com.ihrm.common.entity.ResultCode;
import com.ihrm.common.exception.CommonException;
import com.ihrm.common.utils.BeanMapUtils;
import com.ihrm.common.utils.IdWorker;
import com.ihrm.common.utils.PermissionConstants;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.PermissionApi;
import com.ihrm.domain.system.PermissionMenu;
import com.ihrm.domain.system.PermissionPoint;
import com.ihrm.domain.system.User;
import com.ihrm.system.dao.PermissionApiDao;
import com.ihrm.system.dao.PermissionDao;
import com.ihrm.system.dao.PermissionMenuDao;
import com.ihrm.system.dao.PermissionPointDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PermissionService {

    @Autowired
    private PermissionDao permissionDao;
    @Autowired
    private PermissionMenuDao permissionMenuDao;
    @Autowired
    private PermissionPointDao permissionPointDao;
    @Autowired
    private PermissionApiDao permissionApiDao;
    @Autowired
    private IdWorker idWorker;

    /**
     * 保存权限
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(Map<String, Object> map) throws Exception {
        String id = idWorker.nextId() + "";
        Permission permission = BeanMapUtils.mapToBean(map, Permission.class);
        permission.setId(id);
        int type = permission.getType();
        switch (type) {
            case PermissionConstants.PERMISSION_MENU:
                PermissionMenu permissionMenu = BeanMapUtils.mapToBean(map, PermissionMenu.class);
                permissionMenu.setId(id);
                permissionMenuDao.save(permissionMenu);
                break;
            case PermissionConstants.PERMISSION_POINT:
                PermissionPoint permissionPoint = BeanMapUtils.mapToBean(map, PermissionPoint.class);
                permissionPoint.setId(id);
                permissionPointDao.save(permissionPoint);
                break;
            case PermissionConstants.PERMISSION_API:
                PermissionApi permissionApi = BeanMapUtils.mapToBean(map, PermissionApi.class);
                permissionApi.setId(id);
                permissionApiDao.save(permissionApi);
                break;
            default:
                throw new CommonException(ResultCode.FAIL);
        }
        permissionDao.save(permission);
    }
    /**
     * 修改权限
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Map<String, Object> map) throws Exception {
        Permission bean = BeanMapUtils.mapToBean(map, Permission.class);
        Optional<Permission> optional = permissionDao.findById(bean.getId());
        if(optional.isPresent()){
            Permission permission = optional.get();
            permission.setCode(bean.getCode());
            permission.setDescription(bean.getDescription());
            permission.setEnVisible(bean.getEnVisible());
            permission.setName(bean.getName());

            int type = bean.getType();
            switch (type) {
                case PermissionConstants.PERMISSION_MENU:
                    PermissionMenu permissionMenu = BeanMapUtils.mapToBean(map, PermissionMenu.class);
                    permissionMenu.setId(bean.getId());
                    permissionMenuDao.save(permissionMenu);
                    break;
                case PermissionConstants.PERMISSION_POINT:
                    PermissionPoint permissionPoint = BeanMapUtils.mapToBean(map, PermissionPoint.class);
                    permissionPoint.setId(bean.getId());
                    permissionPointDao.save(permissionPoint);
                    break;
                case PermissionConstants.PERMISSION_API:
                    PermissionApi permissionApi = BeanMapUtils.mapToBean(map, PermissionApi.class);
                    permissionApi.setId(bean.getId());
                    permissionApiDao.save(permissionApi);
                    break;
                default:
                    throw new CommonException(ResultCode.FAIL);
            }
            permissionDao.save(permission);
        }

    }
    /*
     *
     */
    public List<Permission> findAll(Map param) {
        Specification<Permission> spc = (Specification<Permission>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(!StringUtils.isEmpty(param.get("pid"))){
                predicates.add(criteriaBuilder.equal(root.get("pid").as(String.class),param.get("pid")));
            }
            if(!StringUtils.isEmpty(param.get("enVisible"))){
                predicates.add(criteriaBuilder.equal(root.get("enVisible").as(String.class),param.get("enVisible")));
            }
            if(!StringUtils.isEmpty(param.get("type"))){
                Object tp =  param.get("type");
                CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("type"));
                if("0".equals(tp)){
                    in.value(1).value(2);
                }else {
                    in.value(Integer.parseInt((String) tp));
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        return permissionDao.findAll(spc);

    }

    public Map<String,Object> findById(String id) throws CommonException {
        Optional<Permission> optional = permissionDao.findById(id);
        if(optional.isPresent()){
            Permission permission = optional.get();
            Integer type = permission.getType();
            Object obj = null;
            if(PermissionConstants.PERMISSION_MENU == type){
                  obj =  permissionMenuDao.findById(id);
            }else if(PermissionConstants.PERMISSION_POINT == type){
                obj =  permissionPointDao.findById(id);
            }else if(PermissionConstants.PERMISSION_API == type){
                obj =  permissionApiDao.findById(id);
            }else {
                throw new CommonException(ResultCode.FAIL);
            }
            Map<String, Object> map = BeanMapUtils.beanToMap(obj);
            Map<String, Object> pMap = BeanMapUtils.beanToMap(permission);
            map.putAll(pMap);
            return map;
        }
        return null;

    }
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String id) throws CommonException {
        Optional<Permission> optional = permissionDao.findById(id);
        if(optional.isPresent()){
            Permission permission = optional.get();
            int type = permission.getType();
            switch (type) {
                case PermissionConstants.PERMISSION_MENU:
                    permissionMenuDao.deleteById(id);
                    break;
                case PermissionConstants.PERMISSION_POINT:
                    permissionPointDao.deleteById(id);
                    break;
                case PermissionConstants.PERMISSION_API:
                    permissionApiDao.deleteById(id);
                    break;
                default:
                    throw new CommonException(ResultCode.FAIL);
            }
            permissionDao.deleteById(id);
        }
    }
}
