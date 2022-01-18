package com.example.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.order.entity.SendMessage;
import com.example.order.entity.User;
import com.example.order.service.PrefileService;
import com.example.order.util.RanStringUtil;
import com.example.order.util.SendMessageUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

import static com.example.order.util.TokenUtils.token;

@RestController
@CrossOrigin //跨域
@RequestMapping("/orderapi")
public class PersonController {

    @Resource
    private PrefileService prefileService;

//    /**
//     * 测试接口：http://localhost:8080/queryPersonById/1
//     * 请求方式：get    入参：查询id        返回值：查询的一行数据Person（JSON格式）
//     */
//    @GetMapping("/queryPersonById/{id}")
//    public Person queryPersonById(@PathVariable Integer id) {
//        return personService.queryPersonById(id);
//    }
//
//    /**
//     * 测试接口：http://localhost:8080/addPerson
//     * 请求方式：put       入参：JSON数据      返回值：添加成功影响行数
//     */
//    @RequestMapping(value = "/addPerson", method = RequestMethod.POST)
//    public int addPerson(@RequestBody Person person) {
//        return personService.add(person);
//    }
//
//
//    /**
//     * 测试接口：http://localhost:8080/queryListPerson
//     * 请求方式：GET      返回值：查询结果集
//     */
//    @RequestMapping(value = "/queryListPerson", method = RequestMethod.GET)
//    public List<Person> queryListPerson() {
//        return personService.queryListPerson();
//    }
//
//    /**
//     * 测试接口：http://localhost:8080/update
//     * 请求方式：POST      返回值：更新影响行数
//     */
//    @RequestMapping(value = "/update", method = RequestMethod.POST)
//    public int update(@RequestBody Person person) {
//        return personService.update(person);
//    }
//
//    /**
//     * 测试接口：http://localhost:8080/queryListPersonPage
//     * 请求方式：GET      返回值：查询结果集，分页查询
//     */
////    @ApiOperation(value="获取用户", notes="获取用户")
//    @ResponseBody
//    @PostMapping("/queryListPagePerson")
//    public PageInfo<Person> queryListPagePerson(@RequestBody JSONObject json) {
//        Integer pageNum = json.getInteger("pageNum");
//        Integer pageSize = json.getInteger("pageSize");
//
//        PageHelper.startPage(pageNum, pageSize); //重点在这里
//        List<Person> person = personService.queryListPagePerson();
//        PageInfo<Person> pageInfo = new PageInfo<>(person);
//        return pageInfo;
//    }


    //login
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user, HttpServletRequest request) {
        HttpSession session = request.getSession();
        user.setUsername(user.getUsername().toLowerCase());

        User u = prefileService.queryLogin(user);

        Map<String, String> info = new HashMap<String, String>();
        if (u != null) {
//            生成token
            String token = token(user.username, user.password);
            session.setAttribute(user.username + user.password, token);
            info.put("msg", "Login ok");
            info.put("status", "200");
            info.put("token", token);
        } else {
            info.put("msg", "Login error");
            info.put("status", "404");
            info.put("token", "null");
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("data", u);
        result.put("meta", info);

        return result;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody JSONObject json, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String email = json.getString("email").toLowerCase();

        User regU = new User();
        regU.setUsername(email);

        User queryU = prefileService.queryLogin(regU);

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        if (queryU != null) {
            info.put("msg", "User already exists");
            info.put("status", "303");
            info.put("token", "null");
        } else {
            regU.setPassword(RanStringUtil.generateByRandom(8));
            regU.setStatus(0);
            regU.setType(0);
            regU.setCreatedate(new Date());
            regU.setLastupdate(new Date());

            regU.setName(email.split("@")[0]);

            Integer resultAdd = prefileService.registerUser(regU);
            if (resultAdd > 0) {
                String token = token(regU.username, regU.password);
                session.setAttribute(regU.username + regU.password, token);


                SendMessage sendMessage = new SendMessage();
                sendMessage.setSubject("Password is Initial state");
                sendMessage.setSentdate(new Date().toString());
                sendMessage.setSendtype("forgotpassword");
                sendMessage.setUser(regU);

                SendMessageUtil.setSendMessageQueue(sendMessage);

                info.put("msg", "Login ok");
                info.put("status", "200");
                info.put("token", token);

                result.put("data", regU);
            }
        }

        result.put("meta", info);

        return result;
    }

    @PostMapping("/pswretrieval")
    public Map<String, Object> pswRetrieval(@RequestBody JSONObject json) {
        String email = json.getString("email");

        User user = new User();
        user.setUsername(email);

        User uResult = prefileService.queryLogin(user);

        Map<String, String> info = new HashMap<String, String>();
        if (uResult != null) {
            Integer iResult = 0;

            String psw = RanStringUtil.generateByRandom(8);

            uResult.setPassword(psw);
            uResult.setLastupdate(new Date());
            iResult = prefileService.updateUser(uResult);

            if(iResult > 0 ) {
                //发送邮件
//                String content = "<html>\n" +
//                        "<body>\n" +
//                        "    <h3>Password is Initial state</h3>\n" +
//                        "    <h3>New password: " + psw + "</h3>\n" +
//                        "</body>\n" +
//                        "</html>";

                SendMessage sendMessage = new SendMessage();
                sendMessage.setSubject("Password is Initial state");
                sendMessage.setSentdate(new Date().toString());
                sendMessage.setSendtype("forgotpassword");
                sendMessage.setUser(uResult);

                SendMessageUtil.setSendMessageQueue(sendMessage);


//                iResult = prefileService.sendMail(uResult);

                if(iResult == 1) {
                    info.put("msg", "Send Email ok");
                    info.put("status", "200");
                }
            } else {
                info.put("msg", "User is update Error");
                info.put("status", "303");
                info.put("token", "null");
            }
        } else {
            info.put("msg", "User is not register");
            info.put("status", "303");
            info.put("token", "null");
        }

        Map<String, Object> result = new HashMap<String, Object>();
//        result.put("data", u);
        result.put("meta", info);

        return result;
    }

}