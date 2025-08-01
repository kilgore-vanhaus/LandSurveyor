package com.LandSurveyor;

import javax.inject.Inject;
import java.awt.*;
import net.runelite.api.Point;
import net.runelite.api.Client;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.api.Perspective;


public class ElevationOverlay extends Overlay {

    private final LandSurveyorConfig config;

    private final Client client;

    @Inject
    public ElevationOverlay(Client client, LandSurveyorConfig config)
    {
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (client.getLocalPlayer() == null)
        {
            return null;
        }

        LocalPoint localLocation = client.getLocalPlayer().getLocalLocation();
        int plane = client.getPlane();
        int radius = config.tileDistance();

        for (int dx = -radius; dx <= radius; dx++)
        {
            for (int dy = -radius; dy <= radius; dy++)
            {
                int x = localLocation.getSceneX() + dx;
                int y = localLocation.getSceneY() + dy;

                if (x < 0 || x >= 104 || y < 0 || y >= 104)
                    continue;

                LocalPoint lp = LocalPoint.fromScene(x, y);
                if (lp == null)
                    continue;

                int height = -Perspective.getTileHeight(client, lp, plane);

                // Draw filled tile if mode is TILE or BOTH
                if (config.hypsometricMode() == HypsometricMode.TILE || config.hypsometricMode() == HypsometricMode.BOTH)
                {
                    Polygon tilePoly = Perspective.getCanvasTilePoly(client, lp);
                    if (tilePoly != null)
                    {
                        Color baseColor = getColorForHeight(height);
                        int alpha = Math.max(0, Math.min(255, (int)(config.tileOpacityPercent() / 100.0 * 255)));

                        Color transparentColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha);

                        graphics.setColor(transparentColor);
                        graphics.fillPolygon(tilePoly);
                    }
                }

                // Draw text if mode is TEXT or BOTH
                if (config.hypsometricMode() == HypsometricMode.TEXT || config.hypsometricMode() == HypsometricMode.BOTH)
                {
                    Point canvasPoint = Perspective.getCanvasTextLocation(client, graphics, lp, String.valueOf(height), 0);
                    if (canvasPoint != null)
                    {
                        graphics.setComposite(AlphaComposite.SrcOver.derive(1.0f));
                        graphics.setColor(getColorForHeight(height));
                        graphics.drawString(String.valueOf(height), canvasPoint.getX(), canvasPoint.getY());
                    }
                }
                else
                {
                    // Not in TEXT or BOTH mode, but still show text in user-selected color
                    Point canvasPoint = Perspective.getCanvasTextLocation(client, graphics, lp, String.valueOf(height), 0);
                    if (canvasPoint != null)
                    {
                        graphics.setColor(config.textColor());
                        graphics.drawString(String.valueOf(height), canvasPoint.getX(), canvasPoint.getY());
                    }
                }
            }
        }

        return null;
    }
    private Color getColorForHeight(int height)
    {
        final int min = 0;
        final int max = 500;

        int clamped = Math.max(min, Math.min(max, height));
        float ratio = (float)(clamped - min) / (max - min);

        // Apply user-controlled sensitivity
        ratio = Math.min(1.0f, ratio * (float) config.gradientSensitivity());

        Color low = config.hypsometricColorLow();
        Color high = config.hypsometricColorHigh();

        // Convert RGB colors to HSB
        float[] lowHSB = Color.RGBtoHSB(low.getRed(), low.getGreen(), low.getBlue(), null);
        float[] highHSB = Color.RGBtoHSB(high.getRed(), high.getGreen(), high.getBlue(), null);

        // Interpolate hue circularly (hue is angle 0..1)
        float hue = interpolateHue(lowHSB[0], highHSB[0], ratio);
        // Interpolate saturation and brightness linearly
        float saturation = lerp(lowHSB[1], highHSB[1], ratio);
        float brightness = lerp(lowHSB[2], highHSB[2], ratio);

        Color hsbColor = Color.getHSBColor(hue, saturation, brightness);

        // Return fully opaque color
        return new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), 255);
    }

    private float lerp(float start, float end, float t)
    {
        return start + t * (end - start);
    }

    private float interpolateHue(float h1, float h2, float t)
    {
        // Handle hue wrap-around (hue ranges from 0 to 1, circular)
        float dh = h2 - h1;

        if (Math.abs(dh) > 0.5f)
        {
            if (h2 > h1)
            {
                h1 += 1.0f;  // Wrap h1 around
            }
            else
            {
                h2 += 1.0f;  // Wrap h2 around
            }
        }

        float h = lerp(h1, h2, t);
        if (h > 1.0f)
            h -= 1.0f;

        return h;
    }

}
