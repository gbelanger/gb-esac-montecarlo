package gb.esac.montecarlo;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import gb.esac.io.AsciiDataFileWriter;
import gb.esac.tools.MinMax;
import hep.aida.IAnalysisFactory;
import hep.aida.IHistogram1D;
import hep.aida.IHistogramFactory;
import hep.aida.ITree;
import java.io.IOException;
import java.util.Enumeration;
import org.apache.log4j.Logger;



/**
 * The class <code>TestWhiteNoiseGenerator</code> is used to verify that the statistical properties of the white noise generated by the WhiteNoiseGenerator are as expected. For instance, the number of events in the list should be Gaussian distributed with mean equal to the nominal number of events given by duration*meanRate, (the mean count rate should be Gaussian distributed centred on the specified meanRate), the inter-arrival times should be distributed as an exponential with mean equal to the inverse of the specified meanRate, the average power spectrum should be flat. 

 *
 * @author <a href="mailto: guilaume.belanger@esa.int">Guillaume Belanger</a>
 * @version 1.0 (May 2010, ESAC)
 * 
 * Update history:
 * 2021 May 14
 *  - Updated references to old classes and methods
 *  
 */
public class TestWhiteNoiseGenerator {

     static Logger logger  = Logger.getLogger(TestWhiteNoiseGenerator.class);

    public static void main(String[] args) throws IOException {


	//  Create AIDA factories
	IAnalysisFactory af = IAnalysisFactory.create();
	ITree tree = af.createTreeFactory().create();
	IHistogramFactory hf = af.createHistogramFactory(tree);

	//  Generate white noise event lists and make histos of various quantities
	int nLists = (int) 1e2;
	double duration = 10;
	double[] meanRates = new double[] {1., 2.0}; //, 10.};//, 50, 100., 500.};

	for ( int j=0; j < meanRates.length; j++ ) {

	    double meanRate = meanRates[j];
	    double[] numberOfEvents = new double[nLists];
	    //double[] meanCountRates = new double[nLists];
	    DoubleArrayList interArrivalTimes = new DoubleArrayList();

	    for ( int i=0; i < nLists; i++ ) {
		
		//  Generate the arrival times
		double[] times = WhiteNoiseGenerator.generateArrivalTimes(meanRate, duration);

		//  Record the number of events and the count rate for each list
		int nEvents = times.length;
		numberOfEvents[i] = nEvents;
		//meanCountRates[i] = nEvents/duration;

		//  Add inter-arrival times
		for ( int k=0; k < nEvents-1; k++ ) {

		    double dt = times[k+1] - times[k];
		    interArrivalTimes.add(dt);
		}
		
	    }
	    interArrivalTimes.trimToSize();

	    //  Make Histo of inter-arrival times
	    int nBins = 40;
	    double lowerEdge = 0.; //Descriptive.min(interArrivalTimes);
	    double upperEdge = 5/meanRate; //Descriptive.max(interArrivalTimes);
	    IHistogram1D dtHisto = hf.createHistogram1D("histoOfInterArrivalTimes", nBins, lowerEdge, upperEdge);

	    double[] dts = interArrivalTimes.elements();
	    for ( int i=0; i < dts.length; i++ ) 
		dtHisto.fill(dts[i]);

	    //  Make histos of numberOfEvents and meanCountRates
	    lowerEdge = MinMax.getMin(numberOfEvents) -1;
	    upperEdge = MinMax.getMax(numberOfEvents) +1;
	    nBins = (int) Math.ceil((upperEdge - lowerEdge)/2);
	    IHistogram1D nEventsHisto = hf.createHistogram1D("histoOfNumberOfEvents", nBins, lowerEdge, upperEdge);

	    //lowerEdge = Stats.getMin(meanCountRates);
	    //upperEdge = Stats.getMax(meanCountRates);
	    //IHistogram1D meanRatesHisto = hf.createHistogram1D("histoOfMeanCountRates", nBins, lowerEdge, upperEdge);

	    for ( int i=0; i < nLists; i++ ) {
		nEventsHisto.fill(numberOfEvents[i]);
		//meanRatesHisto.fill(meanCountRates[i]);
	    }

	    
	    //  Write all histos as QDP
	    AsciiDataFileWriter dtHistogram = new AsciiDataFileWriter("histoOfInterArrivalTimes"+meanRate+"cps.qdp");
	    dtHistogram.writeHisto(dtHisto, "Inter-arrival Time (s)");

	    AsciiDataFileWriter nEventsHistogram = new AsciiDataFileWriter("histoOfNumOfEvents"+meanRate+"cps.qdp");
	    nEventsHistogram.writeHisto(nEventsHisto, "Number of events per list");

	    //AsciiDataFileWriter meanRatesHistogram = new AsciiDataFileWriter("histoOfMeanCountRates"+meanRate+"cps.qdp");
	    //meanRatesHistogram.writeHisto(meanRatesHisto, "Mean count rate (cps)");

	}

    }

}
