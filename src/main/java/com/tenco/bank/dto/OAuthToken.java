package com.tenco.bank.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Data
// json 형식에 코딩 컨벤션의 스네이크 케이스를 카멜 노이션으로 변경 하기
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OAuthToken {
	private String accessToken;
	private String tokenType;
	private String refreshToken;
	private String expriesIn;
	private String scope;
	private int refreshTokenExpiresIn;
}
