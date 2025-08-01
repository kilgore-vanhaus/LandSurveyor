package com.LandSurveyor;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Land Surveyor"
)
public class LandSurveyorPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private LandSurveyorConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ElevationOverlay elevationOverlay;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(elevationOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(elevationOverlay);
	}

	@Provides
	LandSurveyorConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(LandSurveyorConfig.class);
	}
}
