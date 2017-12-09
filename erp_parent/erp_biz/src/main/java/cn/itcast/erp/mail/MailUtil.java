package cn.itcast.erp.mail;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * 邮件工具
 *
 */
public class MailUtil {

	//spring中的发邮件的组件
	private JavaMailSender sender;

	private String from;

	public void setFrom(String from) {
		this.from = from;
	}

	public void setSender(JavaMailSender sender) {
		this.sender = sender;
	}

	public void sendMail(String subject, String to, String content) throws Exception{
		//创建邮件
		MimeMessage message = sender.createMimeMessage();
		//工具类包装
		MimeMessageHelper helper = new MimeMessageHelper(message,"utf-8");
		//设置发件人
		helper.setFrom(from);
		//邮件的标题
		helper.setSubject(subject);
		//收件人
		helper.setTo(to);
		//内容
		helper.setText(content,true);
		
		
		//发送邮件
		sender.send(message);
	}
}
