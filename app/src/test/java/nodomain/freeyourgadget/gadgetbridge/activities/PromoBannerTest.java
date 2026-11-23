package nodomain.freeyourgadget.gadgetbridge.activities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PromoBannerTest {
    private static final long NOW = 1_800_000_000_000L;

    @Test public void hiddenOnOtherFlavors() { assertFalse(PromoBanner.shouldShow(false, false, 0, NOW)); }
    @Test public void hiddenWhenWeatherInstalled() { assertFalse(PromoBanner.shouldShow(true, true, 0, NOW)); }
    @Test public void shownWhenNeverDismissed() { assertTrue(PromoBanner.shouldShow(true, false, 0, NOW)); }
    @Test public void hiddenWithinSnooze() { assertFalse(PromoBanner.shouldShow(true, false, NOW - 1000, NOW)); }
    @Test public void shownAfterSnooze() { assertTrue(PromoBanner.shouldShow(true, false, NOW - PromoBanner.SNOOZE_MS, NOW)); }
}
