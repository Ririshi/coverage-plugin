import org.junit.Test;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.jenkinsci.test.acceptance.junit.AbstractJUnitTest;
import org.jenkinsci.test.acceptance.po.Build;
import org.jenkinsci.test.acceptance.po.FreeStyleJob;

import io.jenkins.plugins.coverage.CoveragePublisher;
import io.jenkins.plugins.coverage.CoveragePublisher.Adapter;
import io.jenkins.plugins.coverage.CoverageReport;
import io.jenkins.plugins.coverage.FileCoverageTable;
import io.jenkins.plugins.coverage.JobStatus;

/**
 * Should in the end contain all tests.
 */
public class UITest extends AbstractJUnitTest {
    private static final String JACOCO_ANALYSIS_MODEL_XML = "jacoco-analysis-model.xml";
    private static final String JACOCO_CODINGSTYLE_XML = "jacoco-codingstyle.xml";
    private static final String RESOURCES_FOLDER = "/io.jenkins.plugins.coverage";

    @SuppressFBWarnings("BC")
    private static final String FILE_NAME = "jacoco-analysis-model.xml";


    /**
     * Test for checking the CoverageReport by verifying its CoverageTrend, CoverageOverview,
     * FileCoverageTable and CoverageTrend.
     * Uses a project with two different jacoco files, each one used in another build.
     * Second build uses {@link UITest#JACOCO_ANALYSIS_MODEL_XML},
     * Third build uses {@link UITest#JACOCO_CODINGSTYLE_XML}.
     */
    @Test
    public void verifyingCoveragePlugin() {
        //create project with first build failing due to no reports
        FreeStyleJob job = jenkins.getJobs().create(FreeStyleJob.class);
        CoveragePublisher coveragePublisher = job.addPublisher(CoveragePublisher.class);
        Adapter jacocoAdapter = coveragePublisher.createAdapterPageArea("Jacoco");
        coveragePublisher.setFailNoReports(true);
        job.save();
        JobCreatorUtils.buildWithErrors(job);

        //TODO: tests here for fail on no reports (CoverageSummary?)
        

        //create second and third build (successfully), each one containing another jacoco file
        job.configure();
        JobCreatorUtils.copyResourceFilesToWorkspace(job, RESOURCES_FOLDER);
        jacocoAdapter.setReportFilePath(JACOCO_ANALYSIS_MODEL_XML);
        job.save();
        JobCreatorUtils.buildSuccessfully(job);
        job.configure();
        jacocoAdapter.setReportFilePath(JACOCO_CODINGSTYLE_XML);
        job.save();
        JobCreatorUtils.buildSuccessfully(job);


        //TODO: test trendcharts
        //CoverageReportTest.verify()

        //test CoverageReport
        Build buildContainingTwoCoverageReports = job.getLastBuild();
        CoverageReport report = new CoverageReport(buildContainingTwoCoverageReports);
        CoverageReportTest.verify(report);

        //TODO: test CoverageSummary
        //SummaryTest.verify()

        //create fourth build failing due to tresholds not achieved
        //TODO: überarbeiten und splitten in 4/5/6/7ten build (failUnhealty, failUnstable, skipPublishingChecks, failDecreased, appyrecursively?
        job.configure();
        jacocoAdapter.createGlobalThresholdsPageArea("Instruction", 4, 4, false);
        coveragePublisher.setApplyThresholdRecursively(true);
        coveragePublisher.setFailUnhealthy(true);
        coveragePublisher.setFailUnstable(true);
        coveragePublisher.setSkipPublishingChecks(true);
        coveragePublisher.setFailBuildIfCoverageDecreasedInChangeRequest(true);
        job.save();
        JobCreatorUtils.buildWithErrors(job);

        //TODO: testen, aber vorher diesen build splitten, ggf. mit neuem projekt
        //SummaryTest.verify()
    }







}

