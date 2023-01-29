/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.wear.watchface.complications.rendering.utils;

import static androidx.wear.watchface.complications.rendering.utils.LayoutUtils.getBottomHalf;
import static androidx.wear.watchface.complications.rendering.utils.LayoutUtils.getCentralSquare;
import static androidx.wear.watchface.complications.rendering.utils.LayoutUtils.getLeftPart;
import static androidx.wear.watchface.complications.rendering.utils.LayoutUtils.getRightPart;
import static androidx.wear.watchface.complications.rendering.utils.LayoutUtils.getTopHalf;
import static androidx.wear.watchface.complications.rendering.utils.LayoutUtils.isWideRectangle;
import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.support.wearable.complications.ComplicationData;
import android.text.Layout;
import android.view.Gravity;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

/**
 * Layout helper for {@link ComplicationData#TYPE_SHORT_TEXT}.
 *
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
@SuppressLint("RestrictedApi")
public class ShortTextLayoutHelper extends LayoutHelper {

    private final static int MAX_ICON_SIZE = 24;

    /** Used to avoid allocating a Rect object whenever needed. */
    private final Rect mBounds = new Rect();

    @Override
    public void getIconBounds(@NonNull Rect outRect) {
        if (!hasIcon()) {
            outRect.setEmpty();
        } else {
            getBounds(outRect);
            if (isWideRectangle(outRect)) {
                // Left square part of the inner bounds
                getLeftPart(outRect, outRect);
            } else {
                do {
                    getCentralSquare(outRect, outRect);
                    getTopHalf(outRect, outRect);
                } while (outRect.height() > MAX_ICON_SIZE);
            }
        }
    }

    @NonNull
    @Override
    public Layout.Alignment getShortTextAlignment() {
        getBounds(mBounds);
        if (isWideRectangle(mBounds) && hasIcon()) {
            // Wide rectangle with an icon available, align normal
            return Layout.Alignment.ALIGN_NORMAL;
        } else {
            // Otherwise, align center
            return Layout.Alignment.ALIGN_CENTER;
        }
    }

    @Override
    public int getShortTextGravity() {
        if (hasShortTitle() && !hasIcon()) {
            // If title is shown, align to bottom.
            return Gravity.BOTTOM;
        } else {
            // Otherwise, center text vertically
            return Gravity.CENTER_VERTICAL;
        }
    }

    @Override
    public void getShortTextBounds(@NonNull Rect outRect) {
        getBounds(outRect);
        if (hasIcon()) {
            if (isWideRectangle(outRect)) {
                // Text to the right of icon
                getRightPart(outRect, outRect);
            } else {
                // Text on bottom half of central square
                getCentralSquare(outRect, outRect);
                getBottomHalf(outRect, outRect);
            }
        } else if (hasShortTitle()) {
            // Text above title
            getTopHalf(outRect, outRect);
        }
        // Text only, no-op here.
    }

    @NonNull
    @Override
    public Layout.Alignment getShortTitleAlignment() {
        return getShortTextAlignment();
    }

    @Override
    public int getShortTitleGravity() {
        return Gravity.TOP;
    }

    @Override
    public void getShortTitleBounds(@NonNull Rect outRect) {
        if (hasIcon() || !hasShortTitle()) {
            outRect.setEmpty();
        } else {
            // Title is always on bottom half
            getBounds(outRect);
            getBottomHalf(outRect, outRect);
        }
    }
}
