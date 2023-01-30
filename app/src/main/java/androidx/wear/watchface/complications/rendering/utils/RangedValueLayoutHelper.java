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
import static androidx.wear.watchface.complications.rendering.utils.LayoutUtils.getTopHalf;
import static androidx.wear.watchface.complications.rendering.utils.LayoutUtils.scaledAroundCenter;
import static androidx.wear.watchface.complications.rendering.utils.ShortTextLayoutHelper.TEXT_OFFSET_BOTTOM;
import static androidx.wear.watchface.complications.rendering.utils.ShortTextLayoutHelper.TEXT_OFFSET_LEFT;
import static androidx.wear.watchface.complications.rendering.utils.ShortTextLayoutHelper.TEXT_OFFSET_RIGHT;
import static androidx.wear.watchface.complications.rendering.utils.ShortTextLayoutHelper.TEXT_OFFSET_TOP;

import android.graphics.Rect;
import android.support.wearable.complications.ComplicationData;
import android.text.Layout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

/**
 * Layout helper for {@link ComplicationData#TYPE_RANGED_VALUE}.
 *
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class RangedValueLayoutHelper extends LayoutHelper {

    /** As ranged value indicator is circle, this is used to calculate the inner square of it. */
    private static final float INNER_SQUARE_SIZE_FRACTION = (float) (1.0f / Math.sqrt(2.0f));

    /** Padding applied to inner square of the ranged value indicator. */
    private static final float INNER_SQUARE_PADDING_FRACTION = 0.15f;

    /** Padding applied to icon inside the ranged value indicator. */
    private static final float ICON_PADDING_FRACTION = 0.15f;

    /** Used to apply padding to ranged value indicator. */
    private static final float RANGED_VALUE_SIZE_FRACTION = 1.0f;

    /** Used to draw a short text complication inside ranged value for non-wide rectangles. */
    private final ShortTextLayoutHelper mShortTextLayoutHelper = new ShortTextLayoutHelper();

    /** Used to avoid calculating inner square of the ranged value every time it's needed. */
    private final Rect mRangedValueInnerSquare = new Rect();

    /** Used to avoid allocating a Rect object whenever needed. */
    private final Rect mBounds = new Rect();

    private void updateShortTextLayoutHelper() {
        if (getComplicationData() != null) {
            getRangedValueBounds(mRangedValueInnerSquare);
            scaledAroundCenter(
                    mRangedValueInnerSquare,
                    mRangedValueInnerSquare,
                    (1 - INNER_SQUARE_PADDING_FRACTION * 2) * INNER_SQUARE_SIZE_FRACTION);
            mShortTextLayoutHelper.update(
                    mRangedValueInnerSquare.width(),
                    mRangedValueInnerSquare.height(),
                    getComplicationData());
        }
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        updateShortTextLayoutHelper();
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        updateShortTextLayoutHelper();
    }

    @Override
    public void setComplicationData(@Nullable ComplicationData data) {
        super.setComplicationData(data);
        updateShortTextLayoutHelper();
    }

    @Override
    public void getRangedValueBounds(@NonNull Rect outRect) {
        getBounds(outRect);
        if (!hasShortText()) {
            getCentralSquare(outRect, outRect);
            scaledAroundCenter(outRect, outRect, RANGED_VALUE_SIZE_FRACTION);
        } else {
            getLeftPart(outRect, outRect);
            scaledAroundCenter(outRect, outRect, RANGED_VALUE_SIZE_FRACTION);
        }
    }

    @Override
    public void getIconBounds(@NonNull Rect outRect) {
        if (!hasIcon()) {
            outRect.setEmpty();
        } else {
            getBounds(outRect);
            if (!hasShortText()) {
                // Show only an icon inside ranged value indicator
                scaledAroundCenter(outRect, mRangedValueInnerSquare, 1 - ICON_PADDING_FRACTION * 2);
            } else {
                // Draw a short text complication inside ranged value bounds
                mShortTextLayoutHelper.getIconBounds(outRect);
                outRect.offset(mRangedValueInnerSquare.left, 0);
            }
        }
    }

    @NonNull
    @Override
    public Layout.Alignment getShortTextAlignment() {
        getBounds(mBounds);
        return mShortTextLayoutHelper.getShortTextAlignment();
    }

    @Override
    public int getShortTextGravity() {
        getBounds(mBounds);
        return mShortTextLayoutHelper.getShortTextGravity();
    }

    @Override
    public void getShortTextBounds(@NonNull Rect outRect) {
        getBounds(outRect);
        if (!hasShortText()) {
            outRect.setEmpty();
        } else {
            getBounds(outRect);

            int offsetLeft = (int)((outRect.width() / 2) * TEXT_OFFSET_LEFT);
            int offsetRight = (int)((outRect.width() / 2) * TEXT_OFFSET_RIGHT);
            int offsetBottom = (int)((outRect.height() / 2) * TEXT_OFFSET_BOTTOM);
            int offsetTop = (int)((outRect.height() / 2) * TEXT_OFFSET_TOP);

            if (hasShortTitle()) {
                outRect.set(
                        outRect.left + offsetLeft,
                        outRect.top + offsetTop,
                        outRect.right - offsetRight,
                        outRect.bottom - offsetBottom
                );

                getTopHalf(outRect, outRect);
            } else {
                getCentralSquare(outRect, outRect);
            }
        }
    }

    @NonNull
    @Override
    public Layout.Alignment getShortTitleAlignment() {
        return getShortTextAlignment();
    }

    @Override
    public int getShortTitleGravity() {
        return mShortTextLayoutHelper.getShortTitleGravity();
    }

    @Override
    public void getShortTitleBounds(@NonNull Rect outRect) {
        getBounds(outRect);
        // As title is meaningless without text, return empty rectangle in that case too.
        if (!hasShortTitle() || !hasShortText()) {
            outRect.setEmpty();
        } else {
            getBounds(outRect);

            int offsetLeft = (int)((outRect.width() / 2) * TEXT_OFFSET_LEFT);
            int offsetRight = (int)((outRect.width() / 2) * TEXT_OFFSET_RIGHT);
            int offsetBottom = (int)((outRect.height() / 2) * TEXT_OFFSET_BOTTOM);
            int offsetTop = (int)((outRect.height() / 2) * TEXT_OFFSET_TOP);

            outRect.set(
                    outRect.left + offsetLeft,
                    outRect.top + offsetTop,
                    outRect.right - offsetRight,
                    outRect.bottom - offsetBottom
            );

            getBottomHalf(outRect, outRect);
        }
    }
}
