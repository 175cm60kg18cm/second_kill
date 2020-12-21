package com.mz.kill.server.controller;

import com.mz.api.enums.StatusCode;
import com.mz.api.response.BaseResponse;
import com.mz.kill.model.dto.KillSuccessUserInfo;
import com.mz.kill.model.mapper.ItemKillSuccessMapper;
import com.mz.kill.server.dto.KillDto;
import com.mz.kill.server.service.IKillService;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

@Controller
public class KillController {

    private static final Logger log = LoggerFactory.getLogger(KillController.class);

    private static final String prefix = "kill";

    @Autowired
    private IKillService killService;

    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;

    /***
     * 商品秒杀核心业务逻辑
     * @param dto
     * @param result
     * @return
     */
    @RequestMapping(value = prefix + "/execute", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public BaseResponse execute(@RequestBody @Validated KillDto dto, BindingResult result, HttpSession session) {
        if (result.hasErrors() || dto.getKillId() <= 0) {
            return new BaseResponse(StatusCode.InvalidParams);
        }
        Integer userId = (Integer) session.getAttribute("uid");
        if(userId==null) {
            return new BaseResponse(StatusCode.UserNotLogin);
        }
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            Boolean res = killService.killItemV3(dto.getKillId(), userId);
            if (!res) {
                return new BaseResponse(StatusCode.Fail.getCode(), "哈哈~商品已抢购完毕或者不在抢购时间段哦!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }

        return response;
    }
    @GetMapping("kill/execute/success")
    public String success(){
        return "executeSuccess";
    }
    @GetMapping(prefix+"/record/detail/{orderNum}")
    public String getKillRecordDetails(@PathVariable("orderNum") String orderNum, ModelMap modelMap){
        KillSuccessUserInfo info = itemKillSuccessMapper.selectByCode(orderNum);
        modelMap.put("info",info);
        ArrayList<Integer> integers = new ArrayList<>();
        
        return "killRecord";
    }
    /**
     * 用于jmeter压力测试
     * */
    @RequestMapping(value = prefix + "/execute/lock", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public BaseResponse executeLock(@RequestBody @Validated KillDto dto, BindingResult result, HttpSession session) {
        if (result.hasErrors() || dto.getKillId() <= 0) {
            return new BaseResponse(StatusCode.InvalidParams);
        }
        Integer userId = dto.getUserId();

        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            Boolean res = killService.killItemV2(dto.getKillId(), userId);
            if (!res) {
                return new BaseResponse(StatusCode.Fail.getCode(), "哈哈~商品已抢购完毕或者不在抢购时间段哦!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }

        return response;


    }


}