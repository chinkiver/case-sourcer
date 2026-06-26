package com.lawfirm.caseledger.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lawfirm.caseledger.entity.Lawyer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LawyerMapper extends BaseMapper<Lawyer> {
}
