package com.lawfirm.caseledger.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lawfirm.caseledger.entity.Lawyer;
import com.lawfirm.caseledger.mapper.LawyerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LawyerService {

    private final LawyerMapper lawyerMapper;

    public List<Lawyer> listAll() {
        QueryWrapper<Lawyer> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0).eq("status", 1);
        return lawyerMapper.selectList(wrapper);
    }
}
