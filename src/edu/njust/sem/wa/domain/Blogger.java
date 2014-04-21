package edu.njust.sem.wa.domain;

public class Blogger {
	private String id;
	private String url;
	private String nikeName;
	// 博主认证类型 0：未认证 1：个人认证 2：机构认证
	private int verify;
	// 是否为微博达人
	private boolean isDaren;
	// 是否为微博会员
	private boolean isMember;
	// 博主一句话简介
	private String introduce = "";
	private String address = "";
	private String school = "";
	private String company = "";
	private int followNum;
	private int fansNum;
	private int weiboNum;
	private String gender;

	@Override
	public String toString() {
		return "Blogger [id=" + id + ", url=" + url + ", nikeName=" + nikeName
				+ ", verify=" + verify + ", isDaren=" + isDaren + ", isMember="
				+ isMember + ", introduce=" + introduce + ", address="
				+ address + ", school=" + school + ", company=" + company
				+ ", followNum=" + followNum + ", fansNum=" + fansNum
				+ ", weiboNum=" + weiboNum + ", gender=" + gender + "]";
	}

	public int getFollowNum() {
		return followNum;
	}

	public void setFollowNum(int followNum) {
		this.followNum = followNum;
	}

	public int getFansNum() {
		return fansNum;
	}

	public void setFansNum(int fansNum) {
		this.fansNum = fansNum;
	}

	public int getWeiboNum() {
		return weiboNum;
	}

	public void setWeiboNum(int weiboNum) {
		this.weiboNum = weiboNum;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getNikeName() {
		return nikeName;
	}

	public void setNikeName(String nikeName) {
		this.nikeName = nikeName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}


	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public boolean isDaren() {
		return isDaren;
	}

	public void setDaren(boolean isDaren) {
		this.isDaren = isDaren;
	}

	public boolean isMember() {
		return isMember;
	}

	public void setMember(boolean isMember) {
		this.isMember = isMember;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public int getVerify() {
		return verify;
	}

	public void setVerify(int verify) {
		this.verify = verify;
	}

}
