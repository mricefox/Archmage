package com.mricefox.archmage.runtime;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import static com.mricefox.archmage.runtime.Utils.checkNull;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/1/17
 */

public final class Transfer {
    private final Uri uri;
    private final TargetUriParser urlParser;
    private static final DependencyLookupHook dlHook = ArchmageAspectPlugins.inst().getDependencyLookupHook();

    Transfer(Uri uri, TargetUriParser urlParser) {
        checkNull(uri, "Uri");
        checkNull(urlParser, "TargetUriParser");

        if (!urlParser.checkUri(uri)) {
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.urlParser = urlParser;
    }

    /**
     * @throws TargetNotFoundException
     */
    public TargetActivity activity() {
        try {
            Class target = findTarget(Activity.class);

            return new TargetActivity(target);
        } catch (TargetNotFoundException e) {
            if (!dlHook.onTargetNotFound(uri)) {
                throw new TargetNotFoundException(e.getMessage());
            }
        }
        return null;
    }

    public void activity(TargetFindCallback<TargetActivity> callback) {
        checkNull(callback, "TargetFindCallback");

        try {
            Class target = findTarget(Activity.class);

            callback.found(new TargetActivity(target));
        } catch (TargetNotFoundException ignored) {
            if (!dlHook.onTargetNotFound(uri)) {
                callback.notFound(uri);
            }
        }
    }

    /**
     * @throws TargetNotFoundException
     */
    public TargetFragment fragment() {
        try {
            Class target = findTarget(Fragment.class);

            return new TargetFragment(target);
        } catch (TargetNotFoundException e) {
            if (!dlHook.onTargetNotFound(uri)) {
                throw new TargetNotFoundException(e.getMessage());
            }
        }
        return null;
    }

    public void fragment(TargetFindCallback<TargetFragment> callback) {
        checkNull(callback, "TargetFindCallback");

        try {
            Class target = findTarget(Fragment.class);

            callback.found(new TargetFragment(target));
        } catch (TargetNotFoundException ignored) {
            if (!dlHook.onTargetNotFound(uri)) {
                callback.notFound(uri);
            }
        }
    }

    /**
     * @throws TargetNotFoundException
     */
    public TargetFragmentV4 fragmentV4() {
        try {
            Class target = findTarget(android.support.v4.app.Fragment.class);

            return new TargetFragmentV4(target);
        } catch (TargetNotFoundException e) {
            if (!dlHook.onTargetNotFound(uri)) {
                throw new TargetNotFoundException(e.getMessage());
            }
        }
        return null;
    }

    public void fragmentV4(TargetFindCallback<TargetFragmentV4> callback) {
        checkNull(callback, "TargetFindCallback");

        try {
            Class target = findTarget(android.support.v4.app.Fragment.class);

            callback.found(new TargetFragmentV4(target));
        } catch (TargetNotFoundException ignored) {
            if (!dlHook.onTargetNotFound(uri)) {
                callback.notFound(uri);
            }
        }
    }

    private static boolean intentHasComponent(Intent intent) {
        return intent != null && intent.getComponent() != null;
    }

    private <T> Class<T> findTarget(Class<? super T> parent) {
        return TargetProviderManager.inst()
                .findTarget(parent, urlParser.getGroup(uri), urlParser.getPath(uri));
    }

    public static final class TargetFragment {
        private Fragment fragment;

        private TargetFragment(Class<? extends Fragment> fragmentClass) {
            try {
                this.fragment = fragmentClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        public TargetFragment arguments(Bundle args) {
            checkNull(args, "Bundle");

            fragment.setArguments(args);
            return this;
        }

        public Fragment get() {
            return fragment;
        }
    }

    public static final class TargetFragmentV4 {
        private android.support.v4.app.Fragment fragment;

        private TargetFragmentV4(Class<? extends android.support.v4.app.Fragment> fragmentClass) {
            try {
                this.fragment = fragmentClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        public TargetFragmentV4 arguments(Bundle args) {
            checkNull(args, "Bundle");

            fragment.setArguments(args);
            return this;
        }

        public android.support.v4.app.Fragment get() {
            return fragment;
        }
    }

    public static final class TargetActivity {
        private final Class<? extends Activity> activityClass;

        private Intent noClassIntent;

        private TargetActivity(Class<? extends Activity> activityClass) {
            this.activityClass = activityClass;
        }

        /**
         *
         * @param noClassIntent intent without component
         * @return
         */
        public TargetActivity intent(Intent noClassIntent) {
            checkNull(noClassIntent, "Intent");

            if (intentHasComponent(noClassIntent)) {
                throw new IllegalArgumentException("Illegal intent with component:" +
                        noClassIntent.getComponent());
            }
            this.noClassIntent = noClassIntent;
            return this;
        }

        public void start(Context context) {
            checkNull(context, "Context");

            if (noClassIntent == null) {
                noClassIntent = new Intent();
            }
            noClassIntent.setClass(context, activityClass);
            context.startActivity(noClassIntent);
        }

        public void startForResult(Fragment fragment, int requestCode) {
            checkNull(fragment, "Fragment");

            if (noClassIntent == null) {
                noClassIntent = new Intent();
            }
            noClassIntent.setClass(fragment.getActivity(), activityClass);
            fragment.startActivityForResult(noClassIntent, requestCode);
        }

        public void startForResult(android.support.v4.app.Fragment fragmentV4, int requestCode) {
            checkNull(fragmentV4, "Fragment");

            if (noClassIntent == null) {
                noClassIntent = new Intent();
            }
            noClassIntent.setClass(fragmentV4.getActivity(), activityClass);
            fragmentV4.startActivityForResult(noClassIntent, requestCode);
        }

        public void startForResult(Activity activity, int requestCode) {
            checkNull(activity, "Activity");

            if (noClassIntent == null) {
                noClassIntent = new Intent();
            }
            noClassIntent.setClass(activity, activityClass);
            activity.startActivityForResult(noClassIntent, requestCode);
        }
    }
}
