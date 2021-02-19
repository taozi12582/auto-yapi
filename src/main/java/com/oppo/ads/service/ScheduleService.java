package com.oppo.ads.service;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.oppo.ads.extensions.GitPrePushHandler;
import com.oppo.ads.utils.ScheduleUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务线程池
 *
 * @author 80275131
 * @version 1.0
 * @date 2021/2/19 16:39
 * @since 1.0
 **/
public class ScheduleService implements Disposable, StartupActivity.Background {

	private static final Logger LOG = Logger.getInstance(GitPrePushHandler.class);

	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
	private static final List<ScheduledFuture> futureList = new ArrayList<>();

	@Override
	public void dispose() {
		executorService.shutdown();
		LOG.info("shutdown schedule pool.");
	}

	public static ScheduleService getInstance() {
		return ServiceManager.getService(ScheduleService.class);
	}

	public void scheduleAtFixedRate(Runnable command,
	                                long initialDelay,
	                                long period,
	                                TimeUnit unit) {
		ScheduledFuture<?> scheduledFuture = executorService.scheduleAtFixedRate(command, initialDelay, period, unit);
		futureList.add(scheduledFuture);
	}

	public void clearSchedule() {
		futureList.forEach(future -> future.cancel(true));
		futureList.clear();
		LOG.info("clear schedule pool.");
	}


	@Override
	public void runActivity(@NotNull Project project) {
		ScheduleUtil.configSchedule(project);
	}
}
