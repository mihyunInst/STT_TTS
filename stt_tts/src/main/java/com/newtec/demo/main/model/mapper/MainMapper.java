package com.newtec.demo.main.model.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MainMapper {

	int sendMessage(String message);
	
}
