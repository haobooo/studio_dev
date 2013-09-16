package com.pipi.studio.dev.net;

import java.util.HashMap;

public class WebServiceErrorCode {

	public static HashMap<String,String> WS_ERR_CODE = new HashMap<String,String>();

	static{
		WS_ERR_CODE.put("100", "执行成功");
		WS_ERR_CODE.put("101", "执没有接受到数据");
		WS_ERR_CODE.put("102", "数据内有特殊字符，请进行确认");
		WS_ERR_CODE.put("104", "没有操作值，请进行确认");
		WS_ERR_CODE.put("105", "操作值无法识别，请进行确认");
		WS_ERR_CODE.put("106", "账户或密码错误，请进行确认");
		WS_ERR_CODE.put("107", "账号错误，请进行确认");
		WS_ERR_CODE.put("108", "密码错误，请进行确认");
		WS_ERR_CODE.put("109", "原密码错误，请进行确认");
		WS_ERR_CODE.put("110", "用户名已存在");
		WS_ERR_CODE.put("111", "修改失败，联系管理员");
		WS_ERR_CODE.put("112", "新密码长度不合格，请进行确认");
		WS_ERR_CODE.put("202", "合作商家不存在");
		WS_ERR_CODE.put("203", "合作伙伴不存在");
		WS_ERR_CODE.put("205", "消息签名不一致");
		WS_ERR_CODE.put("208", "未查到数据，请进行确认");
		WS_ERR_CODE.put("220", "数据不一致");
		WS_ERR_CODE.put("400", "程序错误，请联系管理员");
		WS_ERR_CODE.put("401", "注册失败，请联系管理员");
		WS_ERR_CODE.put("-1", "资源接口，类型不对");
		WS_ERR_CODE.put("-2", "资源接口，  经纬度必填");
		WS_ERR_CODE.put("-3", "资源接口，关键字、主题id、区域id至少有一个字段不为空");
		WS_ERR_CODE.put("-4", "数据错误");
		WS_ERR_CODE.put("-5", "没有数据");
	}

}
