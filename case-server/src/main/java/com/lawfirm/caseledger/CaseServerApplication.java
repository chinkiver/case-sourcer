package com.lawfirm.caseledger;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lawfirm.caseledger.mapper")
public class CaseServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaseServerApplication.class, args);
	}

}
