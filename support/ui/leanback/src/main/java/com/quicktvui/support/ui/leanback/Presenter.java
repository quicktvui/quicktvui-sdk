package com.quicktvui.support.ui.leanback;

/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class Presenter implements FacetProvider {
    /**
     * ViewHolder can be subclassed and used to cache any view accessors needed
     * to improve binding performance (for example, results of findViewById)
     * without needing to subclass a View.
     */
    public static class ViewHolder implements FacetProvider {
        public final View view;
        private Map<Class, Object> mFacets;

        public ViewHolder(View view) {
            this.view = view;
        }

        @Override
        public final Object getFacet(Class<?> facetClass) {
            if (mFacets == null) {
                return null;
            }
            return mFacets.get(facetClass);
        }

        public final void setFacet(Class<?> facetClass, Object facetImpl) {
            if (mFacets == null) {
                mFacets = new HashMap<Class, Object>();
            }
            mFacets.put(facetClass, facetImpl);
        }
    }

    /**
     * Base class to perform a task on Presenter.ViewHolder.
     */
    public static abstract class ViewHolderTask {
        /**
         * Called to perform a task on view holder.
         * @param holder The view holder to perform task.
         */
        public void run(Presenter.ViewHolder holder) {
        }
    }

    private Map<Class, Object> mFacets;

    /**
     * Creates a new {@link View}.
     */
    public abstract ViewHolder onCreateViewHolder(ViewGroup parent);

    /**
     * Binds a {@link View} to an item.
     */
    public abstract void onBindViewHolder(ViewHolder viewHolder, Object item);

    /**
     * Binds a {@link View} to an item with a list of payloads.
     * @param viewHolder  The ViewHolder which should be updated to represent the contents of the
     *                    item at the given position in the data set.
     * @param item        The item which should be bound to view holder.
     * @param payloads    A non-null list of merged payloads. Can be empty list if requires full
     *                    update.
     */
    public void onBindViewHolder(ViewHolder viewHolder, Object item, List<Object> payloads) {
        onBindViewHolder(viewHolder, item);
    }

    /**
     * Unbinds a {@link View} from an item. Any expensive references may be
     * released here, and any fields that are not bound for every item should be
     * cleared here.
     */
    public abstract void onUnbindViewHolder(ViewHolder viewHolder);

    /**
     * Called when a view created by this presenter has been attached to a window.
     *
     * <p>This can be used as a reasonable signal that the view is about to be seen
     * by the user. If the adapter previously freed any resources in
     * {@link #onViewDetachedFromWindow(ViewHolder)}
     * those resources should be restored here.</p>
     *
     * @param holder Holder of the view being attached
     */
    public void onViewAttachedToWindow(ViewHolder holder) {
    }

    /**
     * Called when a view created by this presenter has been detached from its window.
     *
     * <p>Becoming detached from the window is not necessarily a permanent condition;
     * the consumer of an presenter's views may choose to cache views offscreen while they
     * are not visible, attaching and detaching them as appropriate.</p>
     *
     * Any view property animations should be cancelled here or the view may fail
     * to be recycled.
     *
     * @param holder Holder of the view being detached
     */
    public void onViewDetachedFromWindow(ViewHolder holder) {
        // If there are view property animations running then RecyclerView won't recycle.
        cancelAnimationsRecursive(holder.view);
    }

    /**
     * Utility method for removing all running animations on a view.
     */
    protected static void cancelAnimationsRecursive(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (view != null && view.hasTransientState()) {
                view.animate().cancel();
                if (view instanceof ViewGroup) {
                    final int count = ((ViewGroup) view).getChildCount();
                    for (int i = 0; view.hasTransientState() && i < count; i++) {
                        cancelAnimationsRecursive(((ViewGroup) view).getChildAt(i));
                    }
                }
            }
        }
    }

    /**
     * Called to set a click listener for the given view holder.
     *
     * The default implementation sets the click listener on the root view in the view holder.
     * If the root view isn't focusable this method should be overridden to set the listener
     * on the appropriate focusable child view(s).
     *
     * @param holder The view holder containing the view(s) on which the listener should be set.
     * @param listener The click listener to be set.
     */
    public void setOnClickListener(ViewHolder holder, View.OnClickListener listener) {
        holder.view.setOnClickListener(listener);
    }

    @Override
    public final Object getFacet(Class<?> facetClass) {
        if (mFacets == null) {
            return null;
        }
        return mFacets.get(facetClass);
    }


    public final void setFacet(Class<?> facetClass, Object facetImpl) {
        if (mFacets == null) {
            mFacets = new HashMap<Class, Object>();
        }
        mFacets.put(facetClass, facetImpl);
    }
}
