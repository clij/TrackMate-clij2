package plugin.trackmate.clij2;

import java.awt.Dimension;
import java.util.Map;

import javax.swing.JLabel;

import fiji.plugin.trackmate.gui.components.ConfigurationPanel;
import static fiji.plugin.trackmate.detection.DetectorKeys.KEY_TARGET_CHANNEL;
import static fiji.plugin.trackmate.gui.Fonts.BIG_FONT;
import static fiji.plugin.trackmate.gui.Fonts.FONT;
import static fiji.plugin.trackmate.gui.Fonts.SMALL_FONT;
import static fiji.plugin.trackmate.gui.Icons.PREVIEW_ICON;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.Settings;
import fiji.plugin.trackmate.detection.DetectionUtils;
import fiji.plugin.trackmate.detection.SpotDetectorFactoryBase;
import fiji.plugin.trackmate.gui.GuiUtils;
import fiji.plugin.trackmate.util.JLabelLogger;

public class CLIJ2VoronoiOtsuLabelingDetectorConfigurationPanel extends ConfigurationPanel
{
	protected static final ImageIcon ICON = new ImageIcon( getResource( "images/clij2_logo.png" ) );

	private static final String TITLE = CLIJ2VoronoiOtsuLabelingDetectorFactory.NAME;

	private static final NumberFormat THRESHOLD_FORMAT = new DecimalFormat( "#.##" );

	private final JSlider sliderChannel;
	private static final long serialVersionUID = 1L;
	private final JFormattedTextField ftfSpotSigma;
	private final JFormattedTextField ftfOutlineSigma;

	public CLIJ2VoronoiOtsuLabelingDetectorConfigurationPanel(final Settings settings, final Model model)
	{

		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 144, 0, 32 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 84, 0, 27, 0, 0, 0, 0, 37, 23 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0 };
		setLayout( gridBagLayout );

		final JLabel lblSettingsForDetector = new JLabel( "Settings for detector:" );
		lblSettingsForDetector.setFont( FONT );
		final GridBagConstraints gbcLblSettingsForDetector = new GridBagConstraints();
		gbcLblSettingsForDetector.gridwidth = 3;
		gbcLblSettingsForDetector.insets = new Insets( 5, 5, 5, 0 );
		gbcLblSettingsForDetector.fill = GridBagConstraints.HORIZONTAL;
		gbcLblSettingsForDetector.gridx = 0;
		gbcLblSettingsForDetector.gridy = 0;
		add( lblSettingsForDetector, gbcLblSettingsForDetector );

		final JLabel lblDetector = new JLabel( TITLE, ICON, JLabel.RIGHT );
		lblDetector.setFont( BIG_FONT );
		lblDetector.setHorizontalAlignment( SwingConstants.CENTER );
		final GridBagConstraints gbcLblDetector = new GridBagConstraints();
		gbcLblDetector.gridwidth = 3;
		gbcLblDetector.insets = new Insets( 5, 5, 5, 0 );
		gbcLblDetector.fill = GridBagConstraints.HORIZONTAL;
		gbcLblDetector.gridx = 0;
		gbcLblDetector.gridy = 1;
		add( lblDetector, gbcLblDetector );

		/*
		 * Help text.
		 */
		final JLabel lblHelptext = new JLabel( CLIJ2VoronoiOtsuLabelingDetectorFactory.INFO_TEXT
				.replace( "<br>", "" )
				.replace( "<p>", "<p align=\"justify\">" )
				.replace( "<html>", "<html><p align=\"justify\">" ) );
		lblHelptext.setFont( FONT.deriveFont( Font.ITALIC ) );
		final GridBagConstraints gbcLblHelptext = new GridBagConstraints();
		gbcLblHelptext.anchor = GridBagConstraints.NORTH;
		gbcLblHelptext.fill = GridBagConstraints.HORIZONTAL;
		gbcLblHelptext.gridwidth = 3;
		gbcLblHelptext.insets = new Insets( 5, 10, 5, 10 );
		gbcLblHelptext.gridx = 0;
		gbcLblHelptext.gridy = 2;
		add( lblHelptext, gbcLblHelptext );


		/*
		 * Channel selector.
		 */

		final JLabel lblSegmentInChannel = new JLabel( "Segment in channel:" );
		lblSegmentInChannel.setFont( SMALL_FONT );
		final GridBagConstraints gbcLblSegmentInChannel = new GridBagConstraints();
		gbcLblSegmentInChannel.anchor = GridBagConstraints.EAST;
		gbcLblSegmentInChannel.insets = new Insets( 5, 5, 5, 5 );
		gbcLblSegmentInChannel.gridx = 0;
		gbcLblSegmentInChannel.gridy = 3;
		add( lblSegmentInChannel, gbcLblSegmentInChannel );

		sliderChannel = new JSlider();
		final GridBagConstraints gbcSliderChannel = new GridBagConstraints();
		gbcSliderChannel.fill = GridBagConstraints.HORIZONTAL;
		gbcSliderChannel.insets = new Insets( 5, 5, 5, 5 );
		gbcSliderChannel.gridx = 1;
		gbcSliderChannel.gridy = 3;
		add( sliderChannel, gbcSliderChannel );

		final JLabel labelChannel = new JLabel( "1" );
		labelChannel.setHorizontalAlignment( SwingConstants.CENTER );
		labelChannel.setFont( SMALL_FONT );
		final GridBagConstraints gbcLabelChannel = new GridBagConstraints();
		gbcLabelChannel.insets = new Insets( 5, 5, 5, 0 );
		gbcLabelChannel.gridx = 2;
		gbcLabelChannel.gridy = 3;
		add( labelChannel, gbcLabelChannel );

		sliderChannel.addChangeListener( l -> labelChannel.setText( "" + sliderChannel.getValue() ) );

		/*
		 * SpotSigma
		 */

		final JLabel lblSpotSigma = new JLabel( "Spot Sigma:" );
		lblSpotSigma.setFont( SMALL_FONT );
		final GridBagConstraints gbcLblSpotSigma = new GridBagConstraints();
		gbcLblSpotSigma.anchor = GridBagConstraints.EAST;
		gbcLblSpotSigma.insets = new Insets( 5, 5, 5, 5 );
		gbcLblSpotSigma.gridx = 0;
		gbcLblSpotSigma.gridy = 4;
		add( lblSpotSigma, gbcLblSpotSigma );

		ftfSpotSigma = new JFormattedTextField( THRESHOLD_FORMAT );
		ftfSpotSigma.setFont( SMALL_FONT );
		ftfSpotSigma.setMinimumSize( new Dimension( 60, 20 ) );
		ftfSpotSigma.setHorizontalAlignment( SwingConstants.CENTER );
		final GridBagConstraints gbcSpotSigma = new GridBagConstraints();
		gbcSpotSigma.gridwidth = 2;
		gbcSpotSigma.fill = GridBagConstraints.HORIZONTAL;
		gbcSpotSigma.insets = new Insets( 5, 5, 5, 5 );
		gbcSpotSigma.gridx = 1;
		gbcSpotSigma.gridy = 4;
		add( ftfSpotSigma, gbcSpotSigma );

		/*
		 * OutlineSigma
		 */

		final JLabel lblOutlineSigma = new JLabel( "Outline Sigma:" );
		lblOutlineSigma.setFont( SMALL_FONT );
		final GridBagConstraints gbcLblOutlineSigma = new GridBagConstraints();
		gbcLblOutlineSigma.anchor = GridBagConstraints.EAST;
		gbcLblOutlineSigma.insets = new Insets( 5, 5, 5, 5 );
		gbcLblOutlineSigma.gridx = 0;
		gbcLblOutlineSigma.gridy = 5;
		add( lblOutlineSigma, gbcLblOutlineSigma );

		ftfOutlineSigma = new JFormattedTextField(THRESHOLD_FORMAT);
		ftfOutlineSigma.setFont( SMALL_FONT );
		ftfOutlineSigma.setMinimumSize( new Dimension( 60, 20 ) );
		ftfOutlineSigma.setHorizontalAlignment( SwingConstants.CENTER );
		final GridBagConstraints gbcOutlineSigma = new GridBagConstraints();
		gbcOutlineSigma.gridwidth = 2;
		gbcOutlineSigma.fill = GridBagConstraints.HORIZONTAL;
		gbcOutlineSigma.insets = new Insets( 5, 5, 5, 5 );
		gbcOutlineSigma.gridx = 1;
		gbcOutlineSigma.gridy = 5;
		add( ftfOutlineSigma, gbcOutlineSigma );

		/*
		 * Logger.
		 */

		final JLabelLogger labelLogger = new JLabelLogger();
		final GridBagConstraints gbcLabelLogger = new GridBagConstraints();
		gbcLabelLogger.gridwidth = 3;
		gbcLabelLogger.gridx = 0;
		gbcLabelLogger.gridy = 10;
		add( labelLogger, gbcLabelLogger );

		/*
		 * Preview.
		 */

		final JButton btnPreview = new JButton( "Preview", PREVIEW_ICON );
		btnPreview.setFont( FONT );
		final GridBagConstraints gbcBtnPreview = new GridBagConstraints();
		gbcBtnPreview.gridwidth = 1;
		gbcBtnPreview.anchor = GridBagConstraints.SOUTHEAST;
		gbcBtnPreview.insets = new Insets( 5, 5, 5, 0 );
		gbcBtnPreview.gridx = 2;
		gbcBtnPreview.gridy = 9;
		add( btnPreview, gbcBtnPreview );

		/*
		 * Listeners and specificities.
		 */

		GuiUtils.selectAllOnFocus( ftfSpotSigma );
		GuiUtils.selectAllOnFocus( ftfOutlineSigma );
		btnPreview.addActionListener( e -> DetectionUtils.preview(
				model,
				settings,
				getDetectorFactory(),
				getSettings(),
				settings.imp.getFrame() - 1,
				labelLogger.getLogger(),
				b -> btnPreview.setEnabled( b ) ) );

		/*
		 * Deal with channels: the slider and channel labels are only visible if
		 * we find more than one channel.
		 */
		if ( null != settings.imp )
		{
			final int n_channels = settings.imp.getNChannels();
			sliderChannel.setMaximum( n_channels );
			sliderChannel.setMinimum( 1 );
			sliderChannel.setValue( settings.imp.getChannel() );

			if ( n_channels <= 1 )
			{
				labelChannel.setVisible( false );
				lblSegmentInChannel.setVisible( false );
				sliderChannel.setVisible( false );
			}
			else
			{
				labelChannel.setVisible( true );
				lblSegmentInChannel.setVisible( true );
				sliderChannel.setVisible( true );
			}
		}
	}

	@Override
	public void setSettings( final Map< String, Object > settings )
	{
		sliderChannel.setValue( ( Integer ) settings.get( KEY_TARGET_CHANNEL ) );
		ftfSpotSigma.setValue( settings.get( CLIJ2VoronoiOtsuLabelingDetectorFactory.KEY_SPOT_SIGMA) );
		ftfOutlineSigma.setValue( settings.get( CLIJ2VoronoiOtsuLabelingDetectorFactory.KEY_OUTLINE_SIGMA) );
	}

	@Override
	public Map< String, Object > getSettings()
	{
		final HashMap< String, Object > settings = new HashMap<>( 4 );

		final int targetChannel = sliderChannel.getValue();
		settings.put( KEY_TARGET_CHANNEL, targetChannel );

		final double spot_sigma = ( ( Number ) ftfSpotSigma.getValue() ).doubleValue();
		settings.put( CLIJ2VoronoiOtsuLabelingDetectorFactory.KEY_SPOT_SIGMA, spot_sigma );

		final double outline_sigma = ( ( Number ) ftfOutlineSigma.getValue() ).doubleValue();
		settings.put( CLIJ2VoronoiOtsuLabelingDetectorFactory.KEY_OUTLINE_SIGMA, outline_sigma );

		return settings;
	}

	private SpotDetectorFactoryBase< ? > getDetectorFactory()
	{
		return new CLIJ2VoronoiOtsuLabelingDetectorFactory<>();
	}

	@Override
	public void clean() {}

	protected static URL getResource( final String name )
	{
		return CLIJ2VoronoiOtsuLabelingDetectorFactory.class.getClassLoader().getResource( name );
	}

}
