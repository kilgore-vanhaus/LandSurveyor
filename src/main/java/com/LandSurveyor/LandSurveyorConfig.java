package com.LandSurveyor;

import net.runelite.client.config.*;
import java.awt.Color;

@ConfigGroup("LandSurveyor")
public interface LandSurveyorConfig extends Config
{
	@ConfigItem(
		keyName = "tileDistance",
		name = "Tiles from Player",
		description = "How many tiles from the player to obtain elevation for",
		position = 1
	)
	@Range(max = 50)
	default int tileDistance()
	{
		return 5;
	}

	@ConfigItem(
			keyName = "textColor",
			name = "Text Color",
			description = "Color of the elevation text",
			position = 2
	)
	default Color textColor(){
		return Color.YELLOW;
	}

	@ConfigSection(
			name = "Hypsometric Options",
			description = "Settings for Hypsometric coloring",
			position = 100,
			closedByDefault = true
	)
	String hypsometricSection = "hypsometricSection";
	@ConfigItem(
			keyName = "hypsometricMode",
			name = "Hypsometric Mode",
			description = "Choose where to apply elevation-based coloring",
			section = hypsometricSection,
			position = 0
	)
	default HypsometricMode hypsometricMode(){
		return HypsometricMode.OFF;
	}

	@ConfigItem(
			keyName = "hypsometricColorLow",
			name = "Hypso Color Low",
			description = "Low elevation color (default: green)",
			section = hypsometricSection,
			position = 1
	)
	default Color hypsometricColorLow(){
		return Color.GREEN;
	}

	@ConfigItem(
			keyName = "hypsometricColorHigh",
			name = "Hypso Color High",
			description = "High elevation color (default: red)",
			section = hypsometricSection,
			position = 2
	)
	default Color hypsometricColorHigh(){
		return Color.RED;
	}

	@ConfigItem(
			keyName = "gradientSensitivity",
			name = "Gradient Sensitivity",
			description = "Adjust the hypsometric gradient (default = 1.0)",
			section = hypsometricSection,
			position = 3
	)
	@Range(max = 5)
	default double gradientSensitivity(){
		return 1.0;
	}

	@ConfigItem(
			keyName = "tileOpacity",
			name = "Tile Opacity",
			description = "Opacity of the hypsometric tile overlay (0.0 = fully transparent, 1.0 = fully opaque)",
			section = hypsometricSection,
			position = 4
	)
	@Units(Units.PERCENT)
	@Range(max = 100)
	default int tileOpacityPercent()
	{
		return 50;
	}

	@ConfigItem(
			keyName = "writeToFile",
			name = "Write to File",
			description = "Actively writes elevation data to a CSV file in the .runelite/landSurveyor directory",
			position = 3
	)
	default boolean writeToFile() {
		return false;
	}
}
