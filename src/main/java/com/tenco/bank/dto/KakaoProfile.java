
package com.tenco.bank.dto;

import lombok.Data;

@Data
public class KakaoProfile {

	private Long id;
	private String connectedAt;
	private Properties properties;
	private KakaoAccount kakaoAccount;

	@Data
	public class Properties {

		private String nickname;
		private String profileImage;
		private String thumbnailImage;

	}

	@Data
	public class KakaoAccount {

		private Boolean profileNicknameNeedsAgreement;
		private Boolean profileImageNeedsAgreement;
		private Profile profile;

		@Data
		public class Profile {

			private String nickname;
			private String thumbnailImageUrl;
			private String profileImageUrl;
			private Boolean isDefaultImage;

		}

	}

}
