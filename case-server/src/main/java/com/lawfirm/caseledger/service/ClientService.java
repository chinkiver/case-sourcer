package com.lawfirm.caseledger.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lawfirm.caseledger.entity.Client;
import com.lawfirm.caseledger.mapper.ClientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientMapper clientMapper;

    public List<Client> listAll(Integer type) {
        QueryWrapper<Client> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        if (type != null) {
            wrapper.eq("client_type", type);
        }
        return clientMapper.selectList(wrapper);
    }
}
