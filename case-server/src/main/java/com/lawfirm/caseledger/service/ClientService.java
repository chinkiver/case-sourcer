package com.lawfirm.caseledger.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lawfirm.caseledger.common.BusinessException;
import com.lawfirm.caseledger.dto.ClientRequest;
import com.lawfirm.caseledger.entity.Client;
import com.lawfirm.caseledger.mapper.ClientMapper;
import com.lawfirm.caseledger.util.IdCardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
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
        List<Client> list = clientMapper.selectList(wrapper);
        for (Client c : list) {
            c.setAge(computeAge(c.getBirthDate()));
        }
        return list;
    }

    public Client createClient(ClientRequest request) {
        Client client = new Client();
        BeanUtils.copyProperties(request, client);

        // 身份证号：先做校验位，再回填出生日期与性别
        if (client.getIdCard() != null && !client.getIdCard().isBlank()) {
            if (!IdCardUtil.isValid(client.getIdCard())) {
                throw new BusinessException("身份证号校验失败，请检查");
            }
            client.setBirthDate(IdCardUtil.getBirthDate(client.getIdCard()));
            client.setGender(IdCardUtil.getGender(client.getIdCard()));
        } else {
            client.setIdCard(null);
        }

        clientMapper.insert(client);
        client.setAge(computeAge(client.getBirthDate()));
        return client;
    }

    private Integer computeAge(LocalDate birthDate) {
        if (birthDate == null) {
            return null;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}