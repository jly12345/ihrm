package com.ihrm.common.service;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class BaseService<T> {

    protected Specification<T> getSpc(String companyId){
        Specification<T> spec = (Specification<T>) (root, cq, cb) -> {
            //根据企业id查询
            return cb.equal(root.get("companyId").as(String.class),companyId);
        };
        return spec;
    }
}
