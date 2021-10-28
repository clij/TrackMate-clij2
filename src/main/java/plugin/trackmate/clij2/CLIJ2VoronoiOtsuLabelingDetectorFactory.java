package plugin.trackmate.clij2;

import java.util.*;

import javax.swing.ImageIcon;

import org.jdom2.Element;
import org.scijava.plugin.Plugin;
import static fiji.plugin.trackmate.detection.DetectorKeys.DEFAULT_TARGET_CHANNEL;
import static fiji.plugin.trackmate.detection.DetectorKeys.KEY_TARGET_CHANNEL;
import static fiji.plugin.trackmate.io.IOUtils.*;
import static fiji.plugin.trackmate.util.TMUtils.checkMapKeys;
import static fiji.plugin.trackmate.util.TMUtils.checkParameter;

import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.Settings;
import fiji.plugin.trackmate.TrackMatePlugIn;
import fiji.plugin.trackmate.detection.SpotDetector;
import fiji.plugin.trackmate.detection.SpotDetectorFactory;
import fiji.plugin.trackmate.gui.components.ConfigurationPanel;
import fiji.plugin.trackmate.util.TMUtils;
import ij.ImageJ;
import ij.ImagePlus;
import net.imagej.ImgPlus;
import net.imglib2.Interval;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

@Plugin( type = SpotDetectorFactory.class )
public class CLIJ2VoronoiOtsuLabelingDetectorFactory< T extends RealType< T > & NativeType< T >> implements SpotDetectorFactory< T >
{

	static final String INFO_TEXT = "<html>This detector uses the Voronoi-Otsu-Labeling alorithm from CLIJ2 to label objects for later tracking. Read more:  <a href=\"https://clij.github.io/clij2-docs/md/voronoi_otsu_labeling/\">https://clij.github.io/clij2-docs/md/voronoi_otsu_labeling/</a</html>";

	private static final String KEY = "CLIJ2_Voronoi_Otsu_Labeling_Detector";

	static final String NAME = "CLIJ2 Voronoi-Otsu-Labeling";

	static final String KEY_SPOT_SIGMA= "CLIJ2_VOL_SPOT_SIGMA";
	static final String KEY_OUTLINE_SIGMA= "CLIJ2_VOL_OUTLINE_SIGMA";

	private static final Float DEFAULT_SPOT_SIGMA = 2.0f;
	private static final Float DEFAULT_OUTLINE_SIGMA = 2.0f;

	private String errorMessage;


	protected Map< String, Object > settings;

	@Override
	public String getInfoText()
	{
		return INFO_TEXT;
	}

	@Override
	public ImageIcon getIcon()
	{
		return null;
	}

	@Override
	public String getKey()
	{
		return KEY;
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	protected ImgPlus< T > img;

	@Override
	public boolean setTarget( final ImgPlus< T > img, final Map< String, Object > settings )
	{
		this.img = img;
		this.settings = settings;
		return checkSettings( settings );
	}

	@Override
	public SpotDetector< T > getDetector( final Interval interval, final int frame )
	{
		final int channel = ( Integer ) settings.get( KEY_TARGET_CHANNEL ) - 1;
		final ImgPlus< T > input = TMUtils.hyperSlice( img, channel, frame );

		final double spot_sigma = ( double ) settings.get( KEY_SPOT_SIGMA );
		final double outline_sigma = ( double ) settings.get( KEY_OUTLINE_SIGMA );

		final CLIJ2VoronoiOtsuLabelingDetector< T > detector = new CLIJ2VoronoiOtsuLabelingDetector<T>(
				input,
				interval,
				spot_sigma,
				outline_sigma,
				frame);
		return detector;
	}

	@Override
	public String getErrorMessage()
	{
		/*
		 * If something is not right when calling #setTarget (i.e. the settings
		 * maps is not right), this is how we get an error message.
		 */
		return errorMessage;
	}

	@Override
	public boolean marshall( final Map< String, Object > settings, final Element element )
	{
		final StringBuilder errorHolder = new StringBuilder();
		boolean ok = writeTargetChannel( settings, element, errorHolder );
		ok = ok && writeAttribute( settings, element, KEY_TARGET_CHANNEL, Integer.class, errorHolder );
		ok = ok && writeAttribute( settings, element, KEY_SPOT_SIGMA, Double.class, errorHolder );
		ok = ok && writeAttribute( settings, element, KEY_OUTLINE_SIGMA, Double.class, errorHolder );

		if ( !ok )
			errorMessage = errorHolder.toString();

		return ok;
	}

	@Override
	public boolean unmarshall( final Element element, final Map< String, Object > settings )
	{
		settings.clear();
		final StringBuilder errorHolder = new StringBuilder();
		boolean ok = true;
		ok = ok && readIntegerAttribute( element, settings, KEY_TARGET_CHANNEL, errorHolder );
		ok = ok && readDoubleAttribute( element, settings, KEY_SPOT_SIGMA, errorHolder );
		ok = ok && readDoubleAttribute( element, settings, KEY_OUTLINE_SIGMA, errorHolder );

		if ( !ok )
		{
			errorMessage = errorHolder.toString();
			return false;
		}
		return checkSettings( settings );
	}

	@Override
	public ConfigurationPanel getDetectorConfigurationPanel( final Settings settings, final Model model )
	{
		// We return a simple configuration panel.
		return new CLIJ2VoronoiOtsuLabelingDetectorConfigurationPanel(settings, model);
	}

	@Override
	public Map< String, Object > getDefaultSettings()
	{
		final Map< String, Object > settings = new HashMap<>();
		settings.put( KEY_TARGET_CHANNEL, DEFAULT_TARGET_CHANNEL );
		settings.put( KEY_SPOT_SIGMA, DEFAULT_SPOT_SIGMA );
		settings.put( KEY_OUTLINE_SIGMA, DEFAULT_OUTLINE_SIGMA );
		return settings;
	}

	@Override
	public boolean checkSettings( final Map< String, Object > settings )
	{
		boolean ok = true;
		final StringBuilder errorHolder = new StringBuilder();
		ok = ok & checkParameter( settings, KEY_TARGET_CHANNEL, Integer.class, errorHolder );
		ok = ok & checkParameter( settings, KEY_SPOT_SIGMA, Double.class, errorHolder );
		ok = ok & checkParameter( settings, KEY_OUTLINE_SIGMA, Double.class, errorHolder );

		final List< String > mandatoryKeys = new ArrayList<>();
		mandatoryKeys.add( KEY_TARGET_CHANNEL );
		mandatoryKeys.add( KEY_SPOT_SIGMA );
		mandatoryKeys.add( KEY_OUTLINE_SIGMA );

		ok = ok & checkMapKeys( settings, mandatoryKeys, null, errorHolder );
		if ( !ok )
			errorMessage = errorHolder.toString();

		return ok;
	}


	@Override
	public boolean has2Dsegmentation()
	{
		return true;
	}

	@Override
	public CLIJ2VoronoiOtsuLabelingDetectorFactory< T > copy()
	{
		return new CLIJ2VoronoiOtsuLabelingDetectorFactory<>();
	}

	/*
	 * MAIN METHOD
	 */

	public static void main( final String[] args )
	{
		ImageJ.main( args );
		new ImagePlus( "samples/FakeTracks.tif" ).show();
		new TrackMatePlugIn().run( "" );
	}
}
