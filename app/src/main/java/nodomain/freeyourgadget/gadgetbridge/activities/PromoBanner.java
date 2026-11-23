/*  Copyright (C) 2026 MINMINWORLD

    This file is part of Gadgetbridge.

    Gadgetbridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gadgetbridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>. */
package nodomain.freeyourgadget.gadgetbridge.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;

import nodomain.freeyourgadget.gadgetbridge.BuildConfig;
import nodomain.freeyourgadget.gadgetbridge.R;

/**
 * Watch Bridge flavor only (admin/watch-bridge#5): a self-made cross-promotion banner for
 * the Watch Bridge Weather app. No ad SDK, no network access — tapping only opens the Play
 * Store page. Hidden when the weather app is installed or after the user dismisses it (30 days).
 */
final class PromoBanner {
    static final String WEATHER_PKG = "com.minminworld.watchbridgeweather";
    private static final String PREFS = "wb_promo";
    private static final String KEY_DISMISSED_AT = "weather_dismissed_at";
    static final long SNOOZE_MS = 30L * 24 * 60 * 60 * 1000;

    private PromoBanner() {
    }

    static boolean shouldShow(boolean isWatchBridgeFlavor, boolean weatherInstalled, long dismissedAt, long now) {
        if (!isWatchBridgeFlavor || weatherInstalled) return false;
        return dismissedAt <= 0 || now - dismissedAt >= SNOOZE_MS;
    }

    static void attach(final Activity activity) {
        final View banner = activity.findViewById(R.id.wb_promo_banner);
        if (banner == null) return;
        final SharedPreferences prefs = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        final boolean show = shouldShow("watchbridge".equals(BuildConfig.FLAVOR),
                isInstalled(activity, WEATHER_PKG),
                prefs.getLong(KEY_DISMISSED_AT, 0L),
                System.currentTimeMillis());
        banner.setVisibility(show ? View.VISIBLE : View.GONE);
        if (!show) return;
        banner.setOnClickListener(v -> openStore(activity, WEATHER_PKG));
        activity.findViewById(R.id.wb_promo_close).setOnClickListener(v -> {
            prefs.edit().putLong(KEY_DISMISSED_AT, System.currentTimeMillis()).apply();
            banner.setVisibility(View.GONE);
            androidx.core.view.ViewCompat.requestApplyInsets(banner.getRootView());
        });
    }

    private static boolean isInstalled(Context context, String pkg) {
        try {
            context.getPackageManager().getPackageInfo(pkg, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static void openStore(Context context, String pkg) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkg)));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + pkg)));
        }
    }
}
