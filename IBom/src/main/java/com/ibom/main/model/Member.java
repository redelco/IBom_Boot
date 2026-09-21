package com.ibom.main.model;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="USERS")/*DB_USERS에 연결*/
public class Member {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")/*컬럼 ID*/
	private Long id;
	
	@Column(name = "LOGIN_ID")/*컬럼 LOGIN_ID*/
	private String LOGIN_ID;
	
	@Column(name = "PASSWORD")/*컬럼 PASSWORD*/
	private String PASSWORD;
	
	@Column(name = "NICKNAME")/*컬럼 NICKNAME*/
	private String NICKNAME;
	
	@Column(name = "EMAIL")/*컬럼 EMAIL*/
	private String EMAIL;
	
	@Column(name = "PHONE")/*컬럼 PHONE*/
	private String PHONE;
	
	@Column(name = "REGION")/*컬럼 REGION*/
	private String REGION;
	
	@Column(name = "PROfILE_IMAGE")/*컬럼 PROFILE_IMAGE*/
	private String profileImage;

    @Column(name = "IS_ACTIVE")/*컬럼 IS_ACTIVE*/
    private Integer isActive;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLOGIN_ID() {
		return LOGIN_ID;
	}

	public void setLOGIN_ID(String lOGIN_ID) {
		LOGIN_ID = lOGIN_ID;
	}

	public String getPASSWORD() {
		return PASSWORD;
	}

	public void setPASSWORD(String pASSWORD) {
		PASSWORD = pASSWORD;
	}

	public String getNICKNAME() {
		return NICKNAME;
	}

	public void setNICKNAME(String nICKNAME) {
		NICKNAME = nICKNAME;
	}

	public String getEMAIL() {
		return EMAIL;
	}

	public void setEMAIL(String eMAIL) {
		EMAIL = eMAIL;
	}

	public String getPHONE() {
		return PHONE;
	}

	public void setPHONE(String pHONE) {
		PHONE = pHONE;
	}

	public String getREGION() {
		return REGION;
	}

	public void setREGION(String rEGION) {
		REGION = rEGION;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}
    
    
}
