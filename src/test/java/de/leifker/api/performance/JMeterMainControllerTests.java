package de.leifker.api.performance;

import static org.assertj.core.api.Assertions.assertThat;
import static us.abstracta.jmeter.javadsl.JmeterDsl.htmlReporter;
import static us.abstracta.jmeter.javadsl.JmeterDsl.httpSampler;
import static us.abstracta.jmeter.javadsl.JmeterDsl.jtlWriter;
import static us.abstracta.jmeter.javadsl.JmeterDsl.rpsThreadGroup;
import static us.abstracta.jmeter.javadsl.JmeterDsl.testPlan;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import us.abstracta.jmeter.javadsl.core.TestPlanStats;
import us.abstracta.jmeter.javadsl.core.threadgroups.BaseThreadGroup;

public class JMeterMainControllerTests {

	@Test
	@DisplayName("Test high load of fast concurrent service implemented with java-class")
	void testHighLoadStructuredTaskConcurrencyJava() throws IOException {
		var testname = "structuredtask_java";
		var reportName = testname + "-" + new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new Date());
		var htmlReportName = "html-report-" + reportName;

		var endpoint = "http://localhost:8080/api/fast/java-class/infos/kai";

		BaseThreadGroup.ThreadGroupChild[] scenario = { httpSampler(endpoint), //
				jtlWriter("C:/Users/kaile/Documents/JMeter/jtl-" + reportName + ".jtl") };

		TestPlanStats stats = testPlan(//
				rpsThreadGroup(testname + "-java-class") //
						.rampTo(50, Duration.ofSeconds(10)) //
						.holdFor(Duration.ofSeconds(20)) //
						.children(scenario), //
				htmlReporter("C:/Users/kaile/Documents/JMeter/" + htmlReportName) //
		).run();

		System.out.println("java-class - Errors: " + stats.overall().errorsCount());
		System.out.println("java-class - Samples: " + stats.overall().samplesCount());
		System.out.println("java-class - SamplesTime 99Percentile: " + stats.overall().sampleTime().perc99());
		System.out.println("java-class - SamplesTime Max: " + stats.overall().sampleTime().max());
		System.out.println("java-class - startTime: " + stats.overall().firstTime());
		System.out.println("java-class - EndTime: " + stats.overall().endTime());
		checkMaxErrorRate(stats, 1.0);
		assertThat(stats.overall().sampleTime().mean()).isLessThan(Duration.ofMillis(100));
		assertThat(stats.overall().sampleTime().perc90()).isLessThan(Duration.ofMillis(150));
	}

	@Test
	@DisplayName("Test high load of fast concurrent service implemented with own-class")
	void testHighLoadStructuredTaskConcurrencyOwnClass() throws IOException {
		var testname = "structuredtask_ownclass";
		var reportName = testname + "-" + new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new Date());
		var htmlReportName = "html-report-" + reportName;

		var endpoint = "http://localhost:8080/api/fast/own-class/infos/kai";

		BaseThreadGroup.ThreadGroupChild[] scenario = { httpSampler(endpoint), //
				jtlWriter("C:/Users/kaile/Documents/JMeter/jtl-" + reportName + ".jtl") };

		TestPlanStats stats = testPlan(//
				rpsThreadGroup(testname + "-own-class") //
						.rampTo(50, Duration.ofSeconds(10)) //
						.holdFor(Duration.ofSeconds(20)) //
						.children(scenario), //
				htmlReporter("C:/Users/kaile/Documents/JMeter/" + htmlReportName) //
		).run();

		System.out.println("own-class - Errors: " + stats.overall().errorsCount());
		System.out.println("own-class - Samples: " + stats.overall().samplesCount());
		System.out.println("own-class - SamplesTime 99Percentile: " + stats.overall().sampleTime().perc99());
		System.out.println("own-class - SamplesTime Max: " + stats.overall().sampleTime().max());
		System.out.println("own-class - startTime: " + stats.overall().firstTime());
		System.out.println("own-class - EndTime: " + stats.overall().endTime());
		checkMaxErrorRate(stats, 1.0);
		assertThat(stats.overall().sampleTime().mean()).isLessThan(Duration.ofMillis(100));
		assertThat(stats.overall().sampleTime().perc90()).isLessThan(Duration.ofMillis(150));
	}
	
	@Test
	@DisplayName("Test high load of slow synchronous service")
	void testHighLoadStructuredTaskSynchronous() throws IOException {
		var testname = "Synchronous";
		var reportName = testname + "-" + new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new Date());
		var htmlReportName = "html-report-" + reportName;
		
		var endpoint = "http://localhost:8080/api/slow/infos/kai";
		
		BaseThreadGroup.ThreadGroupChild[] scenario = { httpSampler(endpoint), //
				jtlWriter("C:/Users/kaile/Documents/JMeter/jtl-" + reportName + ".jtl") };
		
		TestPlanStats stats = testPlan(//
				rpsThreadGroup(testname + "-synchronous") //
				.rampTo(50, Duration.ofSeconds(10)) //
				.holdFor(Duration.ofSeconds(20)) //
				.children(scenario), //
				htmlReporter("C:/Users/kaile/Documents/JMeter/" + htmlReportName) //
				).run();
		
		System.out.println("synchronous - Errors: " + stats.overall().errorsCount());
		System.out.println("synchronous - Samples: " + stats.overall().samplesCount());
		System.out.println("synchronous - SamplesTime 99Percentile: " + stats.overall().sampleTime().perc99());
		System.out.println("synchronous - SamplesTime Max: " + stats.overall().sampleTime().max());
		System.out.println("synchronous - startTime: " + stats.overall().firstTime());
		System.out.println("synchronous - EndTime: " + stats.overall().endTime());
		checkMaxErrorRate(stats, 1.0);
		assertThat(stats.overall().sampleTime().mean()).isLessThan(Duration.ofMillis(180));
		assertThat(stats.overall().sampleTime().perc90()).isLessThan(Duration.ofMillis(200));
	}

	private void checkMaxErrorRate(TestPlanStats stats, double maxErrorRate) {
		double sampleCount = stats.overall().samplesCount();
		double errorsCount = stats.overall().errorsCount();

		if (errorsCount > 0) {
			double errorRate = (errorsCount * 100) / sampleCount;
			assertThat(errorRate).isLessThan(maxErrorRate);
		}
	}
}