package gb.esac.montecarlo;

import gb.esac.eventlist.EventList;
import gb.esac.periodogram.FFTPeriodogram;
import gb.esac.periodogram.PeriodogramMaker;
import gb.esac.timeseries.TimeSeries;
import gb.esac.timeseries.TimeSeriesMaker;

import java.util.Arrays;

public class TestMultiComponentRedNoise {

    public static void main(String[] args) throws Exception {

		double mean = 100;
		double duration = 1e4;
		double alpha1 = 1;
		double alpha2 = 2.5;
		double alpha3 = 1;
		double nuBreak1 = 3e-3;
		double nuBreak2 = 1e-2;
		double period = 1000;
		double pulsedFraction = 0.2;

		//// Without period
		double[] times = RedNoiseGenerator.generateArrivalTimes(mean, duration, alpha2);
		//double[] times = RedNoiseGenerator.generateTwoComponentArrivalTimes(mean, duration, alpha1, alpha2, nuBreak1);
		//double[] times = RedNoiseGenerator.generateThreeComponentArrivalTimes(mean, duration, alpha1, alpha2, alpha3, nuBreak1, nuBreak2);

		//// With period
		//double[] times = RedNoiseGenerator.generateModulatedArrivalTimes(mean, duration, alpha1, period, pulsedFraction);
		//double[] times = RedNoiseGenerator.generateTwoComponentModulatedArrivalTimes(mean, duration, alpha1, alpha2, nuBreak1, period, pulsedFraction);
		//double[] times = RedNoiseGenerator.generateThreeComponentModulatedArrivalTimes(mean, duration, alpha1, alpha2, alpha3, nuBreak1, nuBreak2, period, pulsedFraction);		

		EventList evlist = new EventList(times);
		TimeSeries ts = TimeSeriesMaker.makeTimeSeries(evlist, 10d);
		ts.writeCountsAsQDP("ts-multiCompRedNoise.qdp");
		FFTPeriodogram fft = PeriodogramMaker.makePlainFFTPeriodogram(ts);//, "Blackman", "Leahy");
		fft.writeAsQDP("fft-multiCompRedNoise.qdp");

    }

}
