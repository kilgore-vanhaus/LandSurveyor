package com.LandSurveyor;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class LandSurveyorPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(LandSurveyorPlugin.class);
		RuneLite.main(args);
	}
}