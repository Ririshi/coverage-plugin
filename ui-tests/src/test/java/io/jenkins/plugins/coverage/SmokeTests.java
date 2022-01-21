package io.jenkins.plugins.coverage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import org.jenkinsci.test.acceptance.po.Build;
import org.jenkinsci.test.acceptance.po.FreeStyleJob;

import io.jenkins.plugins.coverage.CoveragePublisher.Adapter;
import io.jenkins.plugins.coverage.CoveragePublisher.CoveragePublisher;
import io.jenkins.plugins.coverage.CoveragePublisher.CoveragePublisher.SourceFileResolver;
import io.jenkins.plugins.coverage.CoveragePublisher.Threshold.AdapterThreshold;
import io.jenkins.plugins.coverage.CoveragePublisher.Threshold.AdapterThreshold.AdapterThresholdTarget;
import io.jenkins.plugins.coverage.CoveragePublisher.Threshold.GlobalThreshold;
import io.jenkins.plugins.coverage.CoveragePublisher.Threshold.GlobalThreshold.GlobalThresholdTarget;
import io.jenkins.plugins.coverage.util.TrendChartTestUtil;

/**
 * Smoke Test to test the most used features of Code Coverage api plugin.
 */
public class SmokeTests extends UiTest {

    /**
     * Creates two successful builds. Tests the reference values in summary, coverage report and main panel.
     */
    @Test
    public void verifyingCodeCoveragePlugin() {
        FreeStyleJob job = getJobWithFirstBuildAndDifferentReports(InCaseCoverageDecreasedConfiguration.DONT_FAIL);
        Build secondBuild = buildSuccessfully(job);

        HashMap<String, Double> expectedCoverageFifthBuild = new HashMap<>();
        expectedCoverageFifthBuild.put("Line", 91.02);
        expectedCoverageFifthBuild.put("Branch", 93.97);
        List<Double> expectedReferenceCoverageFifthBuild = new ArrayList<>();
        expectedReferenceCoverageFifthBuild.add(-0.045);
        expectedReferenceCoverageFifthBuild.add(0.054);

        CoverageSummaryTest.verifySummaryWithReferenceBuild(secondBuild, expectedCoverageFifthBuild,
                expectedReferenceCoverageFifthBuild);

        CoverageReport report = new CoverageReport(secondBuild);
        report.open();

        FileCoverageTable fileCoverageTable = report.openFileCoverageTable();
        CoverageReportTest.verifyFileCoverageTableContent(fileCoverageTable,
                new String[] {"edu.hm.hafner.util", "edu.hm.hafner.util", "edu.hm.hafner.util"},
                new String[] {"Ensure.java", "FilteredLog.java", "Generated.java"},
                new String[] {"80.00%", "100.00%", "n/a"},
                new String[] {"86.96%", "100.00%", "n/a"});
        CoverageReportTest.verifyFileCoverageTableNumberOfMaxEntries(fileCoverageTable, 10);

        String coverageTree = report.getCoverageTree();
        CoverageReportTest.verifyCoverageTreeAfterSomeBuildsWithReports(coverageTree);

        String coverageOverview = report.getCoverageOverview();
        CoverageReportTest.verifyCoverageOverviewAfterSomeBuildsWithReports(coverageOverview);

        String trendChart = report.getCoverageTrend();
        TrendChartTestUtil.verifyTrendChart(trendChart, 1, 2);

        MainPanel mainPanel = new MainPanel(job);
        MainPanelTest.verifyTrendChartWithTwoReports(mainPanel, 1, 2);
    }
}

