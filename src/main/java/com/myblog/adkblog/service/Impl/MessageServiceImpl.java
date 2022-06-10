package com.myblog.adkblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myblog.adkblog.dao.mapper.MessageMapper;
import com.myblog.adkblog.pojo.Message;
import com.myblog.adkblog.service.MessageService;
import com.myblog.adkblog.vo.ErrorCode;
import com.myblog.adkblog.vo.MessageVo;
import com.myblog.adkblog.vo.Params.MessageParams;
import com.myblog.adkblog.vo.Params.PageParams;
import com.myblog.adkblog.vo.Result;
import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Data
class MessageResult{
    Long length;
    List<MessageVo> results;
}
@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Override
    public Result addMessage(MessageParams messageParams) {
        if(messageParams.getAuthorName()==null ||messageParams.getContent()==null || messageParams.getContact()==null){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }
        Message message = new Message();
        //头像为空 默认设置为该路径
        if(messageParams.getAvatar()==null){
            messageParams.setAvatar("/static/img/avatar.png");
        }
        BeanUtils.copyProperties(messageParams,message);
        message.setCreateDate(System.currentTimeMillis());
        messageMapper.insert(message);
        return Result.success(null);
    }

    @Override
    public Result getMessage(PageParams pageParams) {
        //分页插件
        Page<Message> page=new Page<>(pageParams.getPage(),pageParams.getPagesize());
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        //如果 指定按时间排序 那么按照倒序
        if(pageParams.getOrderRole()!=null&& pageParams.getOrderRole().equals("a")){
            queryWrapper.orderByAsc(Message::getCreateDate);
        }else {
            queryWrapper.orderByDesc(Message::getCreateDate);
        }
        //分页 不需要条件 选择十条即可
        IPage<Message> messageIPage = messageMapper.selectPage(page,queryWrapper);
        //获取到records
        List<Message> records = messageIPage.getRecords();
        List<MessageVo> messageVoList=new ArrayList<>();
        //复制到vo里面
        for(Message record : records){
            messageVoList.add(copy(record));
        }

        //返回一个带总数的list
        Long total=messageIPage.getTotal();
        MessageResult result = new MessageResult();
        result.setLength(total);
        result.setResults(messageVoList);
        return Result.success(result);
    }

    public MessageVo copy(Message message){
        MessageVo messageVo = new MessageVo();
        BeanUtils.copyProperties(message,messageVo);
        messageVo.setId(message.getId().toString());
        messageVo.setCreateDate(new DateTime(message.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        return messageVo;
    }
}
