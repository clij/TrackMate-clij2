package plugin.trackmate.clij2;

import java.util.ArrayList;
import java.util.List;

import fiji.plugin.trackmate.util.TMUtils;
import ij.ImagePlus;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.CLIJ2;
import net.imagej.ImgPlus;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.detection.SpotDetector;
import fiji.plugin.trackmate.detection.LabelImageDetector;


public class CLIJ2VoronoiOtsuLabelingDetector< T extends RealType< T > & NativeType< T >> implements SpotDetector< T >
{
	private final static String BASE_ERROR_MESSAGE = "CLIJ2 Voronoi-Otsu-Labeling Detector: ";

	protected final Interval interval;

	/** The frame we operate in. */
	private final int frame;
	private final double outline_sigma;
	private final double spot_sigma;
	private final ImgPlus<T> img;

	/** Holder for the results of detection. */
	private List< Spot > spots;

	/** Error message holder. */
	private String errorMessage;

	/** Holder for the processing time. */
	private long processingTime;

	/*
	 * CONSTRUCTOR
	 */

	public CLIJ2VoronoiOtsuLabelingDetector(final ImgPlus< T > img, final Interval interval, double spot_sigma, double outline_sigma, final int frame )
	{
		this.img = img;

		// Take the ROI box from the interval parameter.
		this.interval = interval;

		this.spot_sigma = spot_sigma;
		this.outline_sigma = outline_sigma;

		// We need to know what frame we are in.
		this.frame = frame;
	}

	/*
	 * METHODS
	 */

	@Override
	public List< Spot > getResult()
	{
		return spots;
	}

	@Override
	public boolean checkInput()
	{
		if ( null == img )
		{
			errorMessage = BASE_ERROR_MESSAGE + "Image is null.";
			return false;
		}
		return true;
	}

	@Override
	public boolean process()
	{
		final long start = System.currentTimeMillis();

		final ImagePlus tmp = ImageJFunctions.wrap( img, "tmpwrapped" );

		CLIJ2 clij2 = CLIJ2.getInstance();

		ClearCLBuffer input_image = clij2.push(tmp);
		ClearCLBuffer output_labels = clij2.create_like(input_image);

		clij2.voronoiOtsuLabeling(input_image, output_labels, spot_sigma, outline_sigma);

		RandomAccessibleInterval labelImage = clij2.pullRAI(output_labels);

		output_labels.close();
		input_image.close();

		final double[] calibration = TMUtils.getSpatialCalibration( img );
		boolean simplify = false;

		final LabelImageDetector< T > lbldetector = new LabelImageDetector<T>( labelImage, interval, calibration, simplify );
		if ( !lbldetector.checkInput() || !lbldetector.process() )
		{
			errorMessage = BASE_ERROR_MESSAGE + lbldetector.getErrorMessage();
			return false;
		}
		spots = lbldetector.getResult();

		final long end = System.currentTimeMillis();
		this.processingTime = end - start;
		return true;
	}



	@Override
	public String getErrorMessage()
	{
		/*
		 * If something wrong happens while you #checkInput() or #process(),
		 * state it in the errorMessage field.
		 */
		return errorMessage;
	}

	@Override
	public long getProcessingTime()
	{
		return processingTime;
	}

}
