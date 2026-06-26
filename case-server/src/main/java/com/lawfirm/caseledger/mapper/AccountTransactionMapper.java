package com.lawfirm.caseledger.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lawfirm.caseledger.entity.AccountTransaction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountTransactionMapper extends BaseMapper<AccountTransaction> {
}
