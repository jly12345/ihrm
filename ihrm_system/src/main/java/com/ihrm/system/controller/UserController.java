package com.ihrm.system.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.PageResult;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.common.exception.CommonException;
import com.ihrm.common.service.BaseService;
import com.ihrm.common.utils.JwtUtils;
import com.ihrm.common.utils.PermissionConstants;
import com.ihrm.domain.company.Company;
import com.ihrm.domain.company.Department;
import com.ihrm.domain.company.response.DeptListResult;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.Role;
import com.ihrm.domain.system.User;
import com.ihrm.domain.system.response.ProfileResult;
import com.ihrm.domain.system.response.UserResult;
import com.ihrm.system.service.PermissionService;
import com.ihrm.system.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping(value="/sys")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 用户登录成功，获取用户信息
     * @return
     */
    @RequestMapping(value="/profile",method = RequestMethod.POST)
    public Result profile() {
        String userId = this.claims.getId();
        User user = userService.findById(userId);

        ProfileResult profileResult = null;
        if(user.getLevel() == null || "user".equals(user.getLevel())){
            //3.企业用户具有当前角色的权限
            profileResult = new ProfileResult(user);
        }else {
            //1.saas平台管理员具有所有权限
            Map<String,String> param = new HashMap();
            param.put("enVisible","1");
            List<Permission> list = permissionService.findAll(param);
            profileResult = new ProfileResult(user,list);
        }

        return new Result(ResultCode.SUCCESS,profileResult);



    }

    /**
     * 分配角色
     * @return
     */
    @RequestMapping(value="/user/assignRoles",method = RequestMethod.PUT)
    public Result assignRoles(@RequestBody Map<String,Object> map){
        String userId = map.get("id") + "";
        List<String> roleIds = (List<String>) map.get("roleIds");
        userService.assignRoles(userId,roleIds);
        return new Result(ResultCode.SUCCESS);
    }

    //保存企业
    @RequestMapping(value="/user",method = RequestMethod.POST)
    public Result save(@RequestBody User user)  {
        //1.设置保存的企业id
        user.setCompanyId(companyId);
        user.setCompanyName(companyName);
        userService.save(user);
        return new Result(ResultCode.SUCCESS);
    }

    //根据id更新企业
    /**
     * 1.方法
     * 2.请求参数
     * 3.响应
     */
    @RequestMapping(value = "/user",method = RequestMethod.GET)
    public Result findAll(int page, int size, @RequestParam Map<String,Object> param) {
        //业务操作
        param.put("companyId",companyId);
        Page<User> pageUser = userService.findAll(param, page, size);
        PageResult<User> pageResult = new PageResult<>(pageUser.getTotalElements(),pageUser.getContent());

        return new Result(ResultCode.SUCCESS,pageResult);
    }

    //根据id
    @RequestMapping(value="/user/{id}",method = RequestMethod.GET)
    public Result get(@PathVariable(value="id") String id) {
        User user = userService.findById(id);
        UserResult userResult = new UserResult(user);
        return new Result(ResultCode.SUCCESS, userResult);
    }

    //根据id
    @RequestMapping(value="/user/{id}",method = RequestMethod.PUT)
    public Result update(@PathVariable(value="id") String id, @RequestBody User user) {
        user.setId(id);
        userService.update(user);
        return new Result(ResultCode.SUCCESS,user);
    }

    /**
     * 根据id删除
     */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE,name = "API-USER-DELETE")
    public Result delete(@PathVariable(value = "id") String id) {
        userService.deleteById(id);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 用户登录
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result login(@RequestBody Map<String,String> param){
        String mobile=param.get("mobile");
        String password=param.get("password");
        if(StringUtils.isEmpty(password) || StringUtils.isEmpty(mobile)){
            return new Result(ResultCode.MOBILEORPASSWORDERROR);
        }
        User user = userService.findbyMobile(mobile);
        if(user==null || !password.equals(user.getPassword())){
            return new Result(ResultCode.MOBILEORPASSWORDERROR);
        }

        Set<Role> roles = user.getRoles();
        StringBuilder sb = new StringBuilder();
        for(Role role:roles){
            for(Permission permission: role.getPermissions()){
                if(permission.getType() == PermissionConstants.PERMISSION_API){
                    sb.append(permission.getCode()).append(",");
                }
            }
        }

        Map<String,Object> map = new HashMap<>();
        map.put("companyId",user.getCompanyId());
        map.put("companyName",user.getCompanyName());
        map.put("apis",sb.toString());
        String token = jwtUtils.createJwt(user.getId(), user.getUsername(), map);
        return  new Result(ResultCode.SUCCESS,token);
    }



}
