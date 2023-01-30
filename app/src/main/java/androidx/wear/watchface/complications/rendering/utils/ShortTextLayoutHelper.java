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
import static androidx.wear.watchface.complications.rendering.utils.LayoutUtils.getTopHalf;
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

    private final static int MAX_ICON_SIZE = 36;
    private final static float ICON_Y_BOTTOM_OFFSET = 0.77f;

    final static float TEXT_OFFSET_LEFT = 0.38f;
    final static float TEXT_OFFSET_RIGHT = 0.38f;
    final static float TEXT_OFFSET_BOTTOM = 0.38f;
    final static float TEXT_OFFSET_TOP = 0.7f;

    @Override
    public void getIconBounds(@NonNull Rect outRect) {
        if (!hasIcon()) {
            outRect.setEmpty();
        } else {
            getBounds(outRect);
            int offsetX = MAX_ICON_SIZE / 2;

            outRect.set(
                outRect.centerX() - offsetX,
                    (int)(-1 * (1 - ICON_Y_BOTTOM_OFFSET) * MAX_ICON_SIZE),
                    outRect.centerX() + offsetX,
                    (int)(MAX_ICON_SIZE * ICON_Y_BOTTOM_OFFSET)
            );
        }
    }

    @NonNull
    @Override
    public Layout.Alignment getShortTextAlignment() {
        return Layout.Alignment.ALIGN_CENTER;
    }

    @Override
    public int getShortTextGravity() {
        if (hasShortTitle()) {
            return Gravity.BOTTOM;
        } else {
            return Gravity.CENTER_VERTICAL;
        }
    }

    @Override
    public void getShortTextBounds(@NonNull Rect outRect) {
        getBounds(outRect);

        int offsetLeft = (int)((outRect.width() / 2) * TEXT_OFFSET_LEFT);
        int offsetRight = (int)((outRect.width() / 2) * TEXT_OFFSET_RIGHT);
        int offsetBottom = (int)((outRect.height() / 2) * TEXT_OFFSET_BOTTOM);
        int offsetTop = (int)((outRect.height() / 2) * TEXT_OFFSET_TOP);

        if (hasShortTitle()) {
            outRect.set(
                    outRect.left + offsetLeft,
                    outRect.top + ((hasIcon()) ? offsetTop : offsetBottom),
                    outRect.right - offsetRight,
                    outRect.bottom - offsetBottom
            );

            getTopHalf(outRect, outRect);
        } else {
            getCentralSquare(outRect, outRect);
        }
    }

    @NonNull
    @Override
    public Layout.Alignment getShortTitleAlignment() {
        return getShortTextAlignment();
    }

    @Override
    public int getShortTitleGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    public void getShortTitleBounds(@NonNull Rect outRect) {
        if (!hasShortTitle()) {
            outRect.setEmpty();
        } else {
            getBounds(outRect);

            int offsetLeft = (int)((outRect.width() / 2) * TEXT_OFFSET_LEFT);
            int offsetRight = (int)((outRect.width() / 2) * TEXT_OFFSET_RIGHT);
            int offsetBottom = (int)((outRect.height() / 2) * TEXT_OFFSET_BOTTOM);
            int offsetTop = (int)((outRect.height() / 2) * TEXT_OFFSET_TOP);

            outRect.set(
                    outRect.left + offsetLeft,
                    outRect.top + ((hasIcon()) ? offsetTop : offsetBottom),
                    outRect.right - offsetRight,
                    outRect.bottom - offsetBottom
            );

            getBottomHalf(outRect, outRect);
        }
    }
}
