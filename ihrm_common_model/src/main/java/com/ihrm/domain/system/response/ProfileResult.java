package com.ihrm.domain.system.response;

import com.ihrm.common.utils.PermissionConstants;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.Role;
import com.ihrm.domain.system.User;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class ProfileResult {

    private String mobile;
    private String userName;
    private String company;
    private Map<String,Object> roles = new HashMap<>();


    public ProfileResult(User user, List<Permission> list){
        this.mobile = user.getMobile();
        this.userName = user.getUsername();
        this.company = user.getCompanyName();
        Set<String> menus = new HashSet<>();
        Set<String> points = new HashSet<>();
        Set<String> apis = new HashSet<>();
        for(Permission permission:list){
            Integer type = permission.getType();
            String code = permission.getCode();
            if(type == PermissionConstants.PERMISSION_MENU){
                menus.add(code);
            }else if(type == PermissionConstants.PERMISSION_POINT){
                points.add(code);
            }else  if(type == PermissionConstants.PERMISSION_API){
                apis.add(code);
            }
        }
        this.roles.put("menus",menus);
        this.roles.put("points",points);
        this.roles.put("apis",apis);
    }

    public ProfileResult(User user) {
        this.mobile = user.getMobile();
        this.userName = user.getUsername();
        this.company = user.getCompanyName();
        Set<Role> roles = user.getRoles();
        Set<String> menus = new HashSet<>();
        Set<String> points = new HashSet<>();
        Set<String> apis = new HashSet<>();
        for(Role role:roles){
            Set<Permission> permissions = role.getPermissions();
            for(Permission permission:permissions){
                Integer type = permission.getType();
                String code = permission.getCode();
                if(type == PermissionConstants.PERMISSION_MENU){
                    menus.add(code);
                }else if(type == PermissionConstants.PERMISSION_POINT){
                    points.add(code);
                }else  if(type == PermissionConstants.PERMISSION_API){
                    apis.add(code);
                }
            }
        }


        this.roles.put("menus",menus);
        this.roles.put("points",points);
        this.roles.put("apis",apis);
    }
}
