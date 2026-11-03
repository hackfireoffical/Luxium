package dev.hackfire.aetherlux.config;

import dev.hackfire.aetherlux.render.ShadowMap;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "aetherlux")
public class AetherluxConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public QualityPreset qualityPreset = QualityPreset.PERFORMANCE;

    // === Sky ===
    @ConfigEntry.Category("sky")
    @ConfigEntry.Gui.Tooltip
    public boolean customSky = true;

    @ConfigEntry.Category("sky")
    public boolean milkyWay = true;

    @ConfigEntry.Category("sky")
    public boolean andromeda = true;

    @ConfigEntry.Category("sky")
    public boolean comets = true;

    @ConfigEntry.Category("sky")
    @ConfigEntry.BoundedDiscrete(min = 50, max = 150)
    public int starDensityPercent = 100;

    // === Celestial ===
    @ConfigEntry.Category("celestial")
    public boolean improvedSun = true;

    @ConfigEntry.Category("celestial")
    public boolean improvedMoon = true;

    @ConfigEntry.Category("celestial")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int sunGlowPercent = 75;

    // === Shadows ===
    @ConfigEntry.Category("shadows")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public ShadowMap.Quality shadowQuality = ShadowMap.Quality.PERFORMANCE;

    // === Water ===
    @ConfigEntry.Category("water")
    public boolean waterEffects = true;

    @ConfigEntry.Category("water")
    public boolean betterUnderwater = true;

    // === Lightning ===
    @ConfigEntry.Category("lightning")
    public boolean lightningFlash = true;

    // === God Rays ===
    @ConfigEntry.Category("godrays")
    public boolean godRays = true;

    @ConfigEntry.Category("godrays")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int godRayIntensityPercent = 55;

    public enum QualityPreset {
        PERFORMANCE,
        BALANCED,
        HIGH
    }

    /** Apply the selected quality preset to individual options */
    public void applyPreset() {
        switch (qualityPreset) {
            case PERFORMANCE -> {
                starDensityPercent = 85;
                shadowQuality = ShadowMap.Quality.PERFORMANCE;
                sunGlowPercent = 60;
                godRayIntensityPercent = 40;
                comets = true;
            }
            case BALANCED -> {
                starDensityPercent = 100;
                shadowQuality = ShadowMap.Quality.BALANCED;
                sunGlowPercent = 75;
                godRayIntensityPercent = 55;
            }
            case HIGH -> {
                starDensityPercent = 130;
                shadowQuality = ShadowMap.Quality.HIGH;
                sunGlowPercent = 90;
                godRayIntensityPercent = 70;
            }
        }
    }
}
