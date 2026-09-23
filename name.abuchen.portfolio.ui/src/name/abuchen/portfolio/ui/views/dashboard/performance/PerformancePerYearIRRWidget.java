package name.abuchen.portfolio.ui.views.dashboard.performance;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import name.abuchen.portfolio.model.Dashboard.Widget;
import name.abuchen.portfolio.snapshot.IRRSeries;
import name.abuchen.portfolio.snapshot.ReportingPeriod;
import name.abuchen.portfolio.ui.views.dashboard.DashboardData;
import name.abuchen.portfolio.ui.views.dashboard.DataSeriesConfig;
import name.abuchen.portfolio.ui.views.dashboard.earnings.StartYearConfig;

/**
 * IRR variant of {@link PerformancePerYearWidget}. Each point represents the
 * annualized money-weighted return from the beginning of the corresponding
 * calendar-year reporting period through that date.
 */
public class PerformancePerYearIRRWidget extends PerformancePerYearWidget
{
    public PerformancePerYearIRRWidget(Widget widget, DashboardData dashboardData)
    {
        super(widget, dashboardData);
    }

    @Override
    public Supplier<List<YearSeries>> getUpdateTask()
    {
        var series = get(DataSeriesConfig.class).getDataSeries();
        var startYear = get(StartYearConfig.class).getStartYear();

        return () -> {
            var now = LocalDate.now();
            var answer = new ArrayList<YearSeries>();

            for (int year = startYear; year <= now.getYear(); year++)
            {
                var index = getDashboardData().calculate(series, new ReportingPeriod.YearX(year).toInterval(now));

                if (hasNoHoldings(index.getTotals()))
                    continue;

                answer.add(new YearSeries(year, project(IRRSeries.getDates(index), year), IRRSeries.calculate(index)));
            }

            return answer;
        };
    }
}
