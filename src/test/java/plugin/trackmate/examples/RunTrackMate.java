package plugin.trackmate.examples;

import fiji.plugin.trackmate.TrackMatePlugIn;
import ij.ImageJ;

public class RunTrackMate
{

	public static void main( final String[] args )
	{
		ImageJ.main( args );
		new TrackMatePlugIn().run( "samples/FakeTracks.tif" );
	}
}
