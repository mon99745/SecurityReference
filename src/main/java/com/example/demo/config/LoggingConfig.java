package com.example.demo.config;

import com.google.common.collect.Streams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Slf4j
@Configuration
public class LoggingConfig {
	public static final String ONE_LINE_10 = StringUtils.repeat('-', 10);
	public static final String ONE_LINE_30 = StringUtils.repeat('-', 30);
	public static final String ONE_LINE_50 = StringUtils.repeat('-', 50);
	public static final String ONE_LINE_100 = StringUtils.repeat('-', 100);
	public static final String TWO_LINE_10 = StringUtils.repeat('=', 10);
	public static final String TWO_LINE_30 = StringUtils.repeat('=', 30);
	public static final String TWO_LINE_50 = StringUtils.repeat('=', 50);
	public static final String TWO_LINE_100 = StringUtils.repeat('=', 100);

	/**
	 * 로깅 초기화
	 */
	@EventListener
	public void initLogging(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Environment environment = context.getEnvironment();

		logProperties(context, environment);
		logBeans(context, environment);
	}

	@SuppressWarnings("rawtypes")
	private void logProperties(ApplicationContext context, Environment environment) {
		boolean properties = environment.getProperty("logs.properties",
				Boolean.class, false);
		if (!properties) {
			return;
		}

		log.info(ONE_LINE_100);
		MutablePropertySources sources = ((AbstractEnvironment) environment).getPropertySources();
		StreamSupport.stream(sources.spliterator(), false)
				.filter(EnumerablePropertySource.class::isInstance)
				.map(e -> ((EnumerablePropertySource) e).getPropertyNames())
				.flatMap(Arrays::stream)
				.distinct()
				.filter(e -> !(e.equals("Path")
						|| e.endsWith("class.path") || e.endsWith("library.path")
						|| e.endsWith("datasource.password") || e.endsWith("secret")))
				.sorted()
				.forEach(e -> log.info("{}: {}", e, environment.getProperty(e)));
		log.info(ONE_LINE_100);
	}

	@SuppressWarnings("UnstableApiUsage")
	private void logBeans(ApplicationContext context, Environment environment) {
		boolean beans = environment.getProperty("logs.beans", Boolean.class, false);
		if (!beans) {
			return;
		}

		List<String> list = Arrays.asList(context.getBeanDefinitionNames());
		Collections.sort(list);
		log.info(ONE_LINE_100);
		String format = "%-4.4s %-100.100s %s";
		log.info(String.format(format, "no", "name", "class"));
		log.info(ONE_LINE_100);
		Streams.mapWithIndex(list.stream(), (n, i) ->
						String.format(format, i + 1, n, Optional.ofNullable(context.getBean(n))
								.map(a -> a.getClass().getName())
								.orElse("")))
				.forEach(log::info);
		log.info(ONE_LINE_100);
	}
}
