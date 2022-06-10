package com.myblog.adkblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myblog.adkblog.dao.mapper.TagMapper;
import com.myblog.adkblog.pojo.Tag;
import com.myblog.adkblog.service.TagService;
import com.myblog.adkblog.vo.ListInfoVo;
import com.myblog.adkblog.vo.Params.PageParams;
import com.myblog.adkblog.vo.Result;
import com.myblog.adkblog.vo.TagVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;
    @Override
    public Result findAll() {
        List<Tag> tags = tagMapper.selectList(new LambdaQueryWrapper<>());
        List<TagVo> tagVos = copyList(tags);
        return Result.success(tagVos);
    }

    @Override
    public List<TagVo> findTagByArticleId(Long articleid) {
        List<Tag> tags = tagMapper.findTagByArticleId(articleid);
        return copyList(tags);
    }

    @Override
    public Result addTag(String tagName) {
        tagMapper.addTag(tagName);
        return Result.success(null);
    }

    @Override
    public Result tagList(PageParams pageParams) {
        //分页查询标签
        Page<Tag> page=new Page<>(pageParams.getPage(),pageParams.getPagesize());
        LambdaQueryWrapper<Tag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        IPage<Tag> tagIPage = tagMapper.selectPage(page,tagLambdaQueryWrapper);
        List<Tag> records = tagIPage.getRecords();
        List<TagVo> articleVoList = copyList(records);
        ListInfoVo<TagVo> result = new ListInfoVo<>();
        result.setResults(articleVoList);
        result.setLength(tagIPage.getTotal());

        return Result.success(result);
    }

    /**
     * copy类
     * @param tag
     * @return
     */
    public TagVo copy(Tag tag){
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(tag,tagVo);
        return tagVo;
    }
    public List<TagVo> copyList(List<Tag> tagList){
        List<TagVo> tagVoList = new ArrayList<>();
        for (Tag tag : tagList) {
            tagVoList.add(copy(tag));
        }
        return tagVoList;
    }
}
