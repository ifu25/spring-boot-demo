package cn.lttc.redisdemo.web;

import cn.lttc.redisdemo.model.Product;
import cn.lttc.redisdemo.model.User;
import cn.lttc.redisdemo.service.UserService;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户Controller层
 *
 * @author sunjian
 * @create 2021-04-16
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    //region ===========================抽奖程序===========================

    /**
     * 抽奖准备界面，用于添加抽奖用户
     *
     * @param modelAndView mv
     * @return org.springframework.web.servlet.ModelAndView
     */
    @RequestMapping("/lottery/prepare")
    public ModelAndView prepare(ModelAndView modelAndView){
        //从数据库获取所有用户
        List<User> allUser = userService.findAllUser();
        //将用户信息缓存到redis中
        for (User user : allUser) {
            redisTemplate.opsForHash().put("user",user.getId(),user);
        }
        modelAndView.addObject("allUser",allUser);
        modelAndView.setViewName("lottery-adduser");
        return  modelAndView;
    }

    /**
     * 添加抽奖用户
     *
     * @param addUserIdMap 抽奖用户map
     * @return com.alibaba.fastjson.JSONObject
     */
    @RequestMapping("/lottery/addUser")
    @ResponseBody
    public JSONObject addUser(@RequestBody Map<String,List<Integer>> addUserIdMap){
        JSONObject jsonpObject = new JSONObject();
        List<Integer> addUserId = addUserIdMap.get("addUserId");
        try {
            addUserId.forEach((userId)->{
                redisTemplate.opsForSet().add("lottery",userId);
            });
        }catch (Exception e){
            jsonpObject.put("error",e.getMessage());
        }
        return jsonpObject;
    }

    /**
     * 抽奖主界面
     *
     * @return org.springframework.web.servlet.ModelAndView
     */
    @GetMapping("/lottery/main")
    public ModelAndView main(){
        ModelAndView modelAndView = new ModelAndView();
        List<User> userLst = new ArrayList<>();
        Set<Object> lottery = redisTemplate.opsForSet().members("lottery");
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<User>(User.class));
        assert lottery != null;
        for (Object userId : lottery) {
            User user = (User)redisTemplate.opsForHash().get("user", userId);
            userLst.add(user);
        }
        modelAndView.addObject("lotteryUser",userLst);
        modelAndView.setViewName("lottery-main");
        return  modelAndView;
    }

    /**
     * 抽奖操作
     *
     * @param count 需要抽出几位中奖者
     * @return java.util.List<cn.lttc.redisdemo.model.User>
     */
    @GetMapping("/lottery/lotterying/{count}")
    @ResponseBody
    public List<User> lotterying(@PathVariable Integer count){
        List<User> retUserLst = new ArrayList<>();
        JSONObject retObj = new JSONObject();
        List<Object> lotteryUserIdLst = redisTemplate.opsForSet().pop("lottery",count);
        assert lotteryUserIdLst != null;
        if(lotteryUserIdLst.size()>0){
            for (Object lotteryUserId : lotteryUserIdLst) {
                redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<User>(User.class));
                User user = (User)redisTemplate.opsForHash().get("user", (Integer)lotteryUserId);
                retUserLst.add(user);
            }
        }
        return retUserLst;
    }
    //endregion


}
