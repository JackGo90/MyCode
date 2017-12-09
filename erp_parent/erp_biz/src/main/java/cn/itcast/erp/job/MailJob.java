package cn.itcast.erp.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.entity.Storealert;
import cn.itcast.erp.mail.MailUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 库存预警任务
 *
 */
public class MailJob {
	
	private MailUtil mailUtil;
	
	private Configuration freeMarker;
	
	public void setFreeMarker(Configuration freeMarker) {
		this.freeMarker = freeMarker;
	}

	private IStoredetailDao storedetailDao;
	
	private String subject; // 标题
	private String to; // 收件人
	//private String content; // 内容

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setTo(String to) {
		this.to = to;
	}

	/*public void setContent(String content) {
		this.content = content;
	}*/

	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
	}

	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
	}

	public void doJob(){
		List<Storealert> list = storedetailDao.storealertList();
		if(null != list && list.size() > 0){
			//存在需要预警的商品
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String _subject = subject.replace("[time]", sdf.format(new Date()));
			
			//String _content = content.replace("[count]", list.size() + "");
			try {
				//获取模板
				Template template = freeMarker.getTemplate("email.html");
				//模型的数据
				Map<String,Object> dataModel = new HashMap<String,Object>();
				dataModel.put("storealertList", list);
				//把数据模型里的数据填充到模板中,再转字符串
				String _content = FreeMarkerTemplateUtils.processTemplateIntoString(template, dataModel);
				mailUtil.sendMail(_subject, to, _content);
				System.out.println("发送邮件成功");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("发送邮件失败");
			}
		}
	}
}
