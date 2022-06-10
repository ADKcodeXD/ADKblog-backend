package com.myblog.adkblog.controller;


import com.myblog.adkblog.service.MessageService;
import com.myblog.adkblog.vo.Params.MessageParams;
import com.myblog.adkblog.vo.Params.PageParams;
import com.myblog.adkblog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("message")
@Api(tags="留言板Api")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("findmsg")
    @ApiOperation("获取留言信息Api")
    public Result getMessage(@RequestBody PageParams pageParams){
        return messageService.getMessage(pageParams);
    }

    @PostMapping("addmsg")
    @ApiOperation("添加留言的Api")
    public Result addMessage(@RequestBody MessageParams messageParams){
        return messageService.addMessage(messageParams);
    }
}
