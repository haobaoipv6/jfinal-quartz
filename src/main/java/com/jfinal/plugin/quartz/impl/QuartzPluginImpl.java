package com.jfinal.plugin.quartz.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.quartz.IQuartzPlugin;

/**
 * QuartzPlugin定时任务接口实现类
 * @author ERROR
 */
public class QuartzPluginImpl implements IQuartzPlugin {
	private static Logger logger = Logger.getLogger(QuartzPluginImpl.class);
	private static SchedulerFactory schedulerFactory = new StdSchedulerFactory(); 
	
	/**
	 * 添加一个job到定时任务
	 */
	public void addJob(String jobName,JobDetail jobDetail, Class cls, String time) {  
        try {  
            Scheduler sched = schedulerFactory.getScheduler();  
            CronTrigger trigger = new CronTrigger(jobName, jobName); 
            trigger.setCronExpression(time); 
            Date date = sched.scheduleJob(jobDetail, trigger);  
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			logger.info("定时任务【" + jobName + "】已于" + sdf.format(date)+ "加入定时计划，并且每隔[" + time + "]分钟执行一次。");
			if (!sched.isShutdown()) {  
                sched.start();  
            }  
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException("添加任务异常："+e.getMessage());  
        }  
    }
	
	public JobDetail getJobDetail(String jobName){
		JobDetail  jobDetail;
		try {
			jobDetail = schedulerFactory.getScheduler().getJobDetail(jobName, jobName);
		} catch (SchedulerException e) {
			throw new RuntimeException(e); 
		}
		return jobDetail;
	}
	
	/**
	 * 修改任务
	 */
	public void modifyJobTime(String jobName,JobDetail jobDetail,String time) {  
        try {
            Scheduler sched = schedulerFactory.getScheduler();  
            CronTrigger trigger = (CronTrigger) sched.getTrigger(jobName,jobName);  
            if (trigger == null) {
                return;
            }
            String oldTime = trigger.getCronExpression();  
            if (!oldTime.equalsIgnoreCase(time)) {
                Class objJobClass = jobDetail.getJobClass();  
                removeJob(jobName);  
                addJob(jobName,jobDetail,objJobClass, time);  
            }
        } catch (Exception e) {  
            throw new RuntimeException("修改定时任务异常："+e.getMessage());  
        }
    }
	
	/**
	 * 移除一个job
	 */
	public void removeJob(String jobName) {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            sched.pauseTrigger(jobName, jobName);
            sched.unscheduleJob(jobName, jobName);
            sched.deleteJob(jobName, jobName);
        } catch (Exception e) {
            throw new RuntimeException("删除定时任务异常："+e.getMessage());  
        }
    }
	
	public void startScheduler() {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            sched.start();
        } catch (Exception e) {
            throw new RuntimeException("启动【Scheduler】异常："+e.getMessage());
        }
    }
	
	/**
	 * 停止Scheduler
	 */
	public void shutdownScheduler() {
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            if (!sched.isShutdown()) {
                sched.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException("停止【Scheduler】异常"+e.getMessage());
        }
    }

	public boolean start() {
		try {
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(new File(PathKit.getRootClassPath()+"/quartz_jobs.xml"));
			Element root = document.getRootElement();
			for (Iterator<Element> iter = root.elementIterator(); iter.hasNext();) {
				Element element = iter.next();
				String name = element.getName();
				if (name.equalsIgnoreCase("job")) {
//					String jobName = element.attributeValue("name");
					Element jobDetail = element.element("job-detail");
					String jobDetailName = jobDetail.attributeValue("name");
					String jobDetailGroup = jobDetail.attributeValue("group");
					String jobDetailClass = jobDetail.attributeValue("job-class");
					
					Element trigger = element.element("trigger");
					String triggerName = trigger.attributeValue("name");
					String triggerGroup = trigger.attributeValue("group");
					String triggerCronExpression = trigger.attributeValue("cron-expression");
					
					Class<?> forName = Class.forName(jobDetailClass);
					JobDetail countryStatistic = new JobDetail(jobDetailName,jobDetailGroup,forName);
					this.addJob(triggerName, countryStatistic,forName, triggerCronExpression);
				}
			}
//			startScheduler();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean stop() {
		shutdownScheduler();
		return true;
	}
}
