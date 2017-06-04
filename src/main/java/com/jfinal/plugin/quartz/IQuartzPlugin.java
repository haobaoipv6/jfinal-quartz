package com.jfinal.plugin.quartz;

import org.quartz.JobDetail;
import com.jfinal.plugin.IPlugin;

/**
 * QuartzPlugin定时任务接口
 * @author ERROR
 */
public interface IQuartzPlugin extends IPlugin{
	/**
	 * 添加一个新定时任务
	 * @param jobName 任务名称，目前任务名称和组名称是同一个值
	 * @param jobDetail	
	 * @param cls	job类
	 * @param time	设置执行时间
	 */
	public void addJob(String jobName,JobDetail jobDetail,Class cls, String time);
	
	/**
	 * 获取JobDetail 
	 * @param jobName 任务名称
	 * @return
	 */
	public JobDetail getJobDetail(String jobName);
	
	/**
	 * 修改任务
	 * @param jobName
	 * @param jobDetail
	 * @param time
	 */
	public void modifyJobTime(String jobName,JobDetail jobDetail,String time);
	
	/**
	 * 移除一个任务
	 * @param jobName
	 */
	public void removeJob(String jobName);
	
	/**
	 * 启动Scheduler
	 */
	public void startScheduler();
	
	/**
	 * 停止Scheduler
	 */
	public void shutdownScheduler();
}
