package im.toduck.global.config.quartz;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class QuartzConfig {

	private final DataSource dataSource;
	private final ApplicationContext applicationContext;
	private final QuartzProperties quartzProperties;

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() {
		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(applicationContext);
		schedulerFactoryBean.setJobFactory(jobFactory);

		schedulerFactoryBean.setDataSource(dataSource);
		schedulerFactoryBean.setOverwriteExistingJobs(true);
		schedulerFactoryBean.setAutoStartup(true);

		Properties properties = new Properties();
		properties.putAll(quartzProperties.getProperties());

		properties.put("org.quartz.scheduler.instanceName", "ToduckScheduler");
		properties.put("org.quartz.scheduler.instanceId", "AUTO");

		properties.put("org.quartz.threadPool.threadCount", "10");
		properties.put("org.quartz.threadPool.threadPriority", "5");

		properties.put("org.quartz.jobStore.class", "org.springframework.scheduling.quartz.LocalDataSourceJobStore");
		properties.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
		properties.put("org.quartz.jobStore.tablePrefix", "qrtz_");
		properties.put("org.quartz.jobStore.isClustered", "true");
		properties.put("org.quartz.jobStore.clusterCheckinInterval", "20000");
		properties.put("org.quartz.jobStore.misfireThreshold", "60000");

		schedulerFactoryBean.setQuartzProperties(properties);
		schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true);

		return schedulerFactoryBean;
	}
}
