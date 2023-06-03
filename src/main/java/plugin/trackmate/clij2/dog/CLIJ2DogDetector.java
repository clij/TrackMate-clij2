package plugin.trackmate.clij2.dog;

import fiji.plugin.trackmate.detection.DetectionUtils;
import fiji.plugin.trackmate.detection.LogDetector;
import fiji.plugin.trackmate.util.TMUtils;
import ij.ImagePlus;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij2.CLIJ2;
import net.imagej.ImgPlus;
import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.dog.DifferenceOfGaussian;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class CLIJ2DogDetector< T extends RealType< T > & NativeType< T > > extends LogDetector< T >
{

	public CLIJ2DogDetector( final RandomAccessible< T > img, final Interval interval, final double[] calibration, final double radius, final double threshold, final boolean doSubPixelLocalization, final boolean doMedianFilter )
	{
		super( img, interval, calibration, radius, threshold, doSubPixelLocalization, doMedianFilter );
	}

	@Override
	public boolean process()
	{
		final long start = System.currentTimeMillis();

		final RandomAccessibleInterval< T > rai = Views.interval( img, interval );
		final ImagePlus tmp = ImageJFunctions.wrap( rai, "tmpwrapped" );

		final CLIJ2 clij2 = CLIJ2.getInstance();

		final ClearCLBuffer input = clij2.push( tmp );
		final ClearCLBuffer output = clij2.create( input.getDimensions(), NativeTypeEnum.Float );

		final double sigma1 = radius / Math.sqrt( interval.numDimensions() ) * 0.9;
		final double sigma2 = radius / Math.sqrt( interval.numDimensions() ) * 1.1;
		/*
		 * Gotcha: The calibration array used as input for
		 * DifferenceOfGaussian#computeSigmas() must be of the same dimension
		 * that the input image.
		 */
		final double[] cal = new double[ img.numDimensions() ];
		for ( int d = 0; d < cal.length; d++ )
			cal[ d ] = calibration[ d ];
		final double[][] sigmas = DifferenceOfGaussian.computeSigmas( 0.5, 2, cal, sigma1, sigma2 );

		if (DetectionUtils.is2D( tmp ))
			clij2.differenceOfGaussian2D( input, output, sigmas[ 0 ][ 0 ], sigmas[ 0 ][ 1 ], sigmas[ 1 ][ 0 ], sigmas[ 1 ][ 1 ] );
		else
			clij2.differenceOfGaussian3D( input, output, sigmas[ 0 ][ 0 ], sigmas[ 0 ][ 1 ], sigmas[ 0 ][ 2 ], sigmas[ 1 ][ 0 ], sigmas[ 1 ][ 1 ], sigmas[ 0 ][ 2 ] );

		final ImagePlus out = clij2.pull( output );
		@SuppressWarnings( "unchecked" )
		final ImgPlus< FloatType > dog = TMUtils.rawWraps( out );

		spots = DetectionUtils.findLocalMaxima( dog, threshold, calibration, radius, doSubPixelLocalization, numThreads );

		final long end = System.currentTimeMillis();
		this.processingTime = end - start;

		return true;

	}
}
