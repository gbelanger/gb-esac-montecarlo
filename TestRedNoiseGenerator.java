package gb.esac.montecarlo;

import gb.esac.periodogram.FFTPeriodogram;
import gb.esac.timeseries.TimeSeriesMaker;
import gb.esac.timeseries.TimeSeries;
import gb.esac.eventlist.EventList;
import gb.esac.periodogram.PeriodogramMaker;
import gb.esac.io.AsciiDataFileWriter;
import gb.esac.binner.Binner;
import gb.esac.binner.BinningUtils;

public class TestRedNoiseGenerator {

    public static void main(String[] args) throws Exception {

	double mean = 10;
	double duration = (new Double(args[0])).doubleValue();
	double alpha = 2;
	double bintime = 1d;
	
	
	boolean generate = true;
	if (generate) {
	    double[] times = RedNoiseGenerator.generateArrivalTimes(mean, duration, alpha);
	    EventList evlist = new EventList(times);
	    TimeSeries ts = TimeSeriesMaker.makeTimeSeries(evlist, bintime);
	    FFTPeriodogram fft = PeriodogramMaker.makePlainFFTPeriodogram(ts);
	    evlist.writeTimesAsQDP("evlist.qdp");
	    ts.writeRatesAsQDP("ts.qdp");
	    fft.writeAsQDP("fft.qdp");
	}
	
	boolean compare = true;
	if (compare) {
	    EventList evlist_cdf = new EventList("tk_times_cdf.qdp");
	    EventList evlist_rates = new EventList("tk_times_rates.qdp");
	    TimeSeries ts_cdf = (TimeSeries) TimeSeriesMaker.makeTimeSeries(evlist_cdf, bintime);
	    TimeSeries ts_rates = (TimeSeries) TimeSeriesMaker.makeTimeSeries(evlist_rates, bintime);
	    ts_cdf.writeCountsAsQDP("ts_cdf.qdp");
	    ts_rates.writeCountsAsQDP("ts_rates.qdp");
	}
    }
}
