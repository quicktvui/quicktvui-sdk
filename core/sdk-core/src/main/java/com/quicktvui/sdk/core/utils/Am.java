package com.quicktvui.sdk.core.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.sunrain.toolkit.utils.log.L;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import com.quicktvui.sdk.base.EsException;

public class Am {

    public static final String TYPE_ACTIVITY = "start";
    public static final String TYPE_BROADCAST = "broadcast";
    public static final String TYPE_SERVICE = "startservice";

    private static final byte TYPE_ACTIVITY_INT = 0;
    private static final byte TYPE_BROADCAST_INT = 1;
    private static final byte TYPE_SERVICE_INT = 2;

    private String[] mArgs;
    private int mNextArg;
    private String mCurArgData;
    private byte mCurrentType = -1;

    public EsIntent makeIntent(String cmd) throws Exception {
        try {
            int amIndex = cmd.indexOf("am ");
            if (amIndex < 0) {
                throw new EsException("cmd not found 'am'");
            }
            cmd = Pattern.compile("\\s+").matcher(cmd).replaceAll(" ");
            String cutAm = cmd.substring(amIndex + 3);
            mArgs = cutAm.split(" ");
            mNextArg = 1;
            makeIntentType(mArgs[0]);
            return makeIntent();
        } catch (Throwable e) {
            reset();
            L.logW("am make intent", e);
            throw e;
        }
    }

    public EsIntent makeIntent(String type, List<String[]> cmd) throws Exception {
        makeArgs(cmd);
        makeIntentType(type);
        return makeIntent();
    }

    public void execute(Context context, EsIntent intent) throws Exception {
        try {
            switch (mCurrentType) {
                case TYPE_ACTIVITY_INT:
                    context.startActivity(intent);
                    break;
                case TYPE_BROADCAST_INT:
                    context.sendBroadcast(intent);
                    break;
                case TYPE_SERVICE_INT:
                    if (intent.hasCustomFlag(EsIntent.FLAG_FOREGROUND_SERVICE)) { // 需要启动前台服务
                        L.logIF("startForegroundService");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(intent);
                            break;
                        }
                    }
                    context.startService(intent);
                    break;
                default:
                    break;
            }
        } finally {
            reset();
        }
    }

    private void makeArgs(List<String[]> cmd) {
        int size = 0;
        for (String[] line : cmd) {
            size += line.length;
        }

        mArgs = new String[size];
        int index = 0;
        for (String[] line : cmd) {
            for (String opt : line) {
                mArgs[index++] = opt;
            }
        }
    }

    private void makeIntentType(String type) {
        switch (type) {
            case TYPE_ACTIVITY:
                mCurrentType = TYPE_ACTIVITY_INT;
                break;
            case TYPE_BROADCAST:
                mCurrentType = TYPE_BROADCAST_INT;
                break;
            case TYPE_SERVICE:
                mCurrentType = TYPE_SERVICE_INT;
                break;
            default:
                throw new EsException(-1, "not support cmd type :" + type);
        }
    }

    private void reset() {
        mArgs = null;
        mCurArgData = null;
        mNextArg = 0;
        mCurrentType = -1;
    }

    private EsIntent makeIntent() throws URISyntaxException {
        EsIntent intent = new EsIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        EsIntent baseIntent = intent;
        boolean hasIntentInfo = false;
        Uri data = null;
        String type = null;
        String opt;
        while ((opt = nextOption()) != null) {
            if (opt.equals("-a")) {
                intent.setAction(nextArgRequired());
                if (intent == baseIntent) {
                    hasIntentInfo = true;
                }
            } else if (opt.equals("-p")) {
                String pkg = nextArgRequired();
                if (!TextUtils.isEmpty(pkg)) {
                    intent.setPackage(pkg);
                }
                if (intent == baseIntent) {
                    hasIntentInfo = true;
                }
            } else if (opt.equals("-d")) {
                data = Uri.parse(nextArgRequired());
                if (intent == baseIntent) {
                    hasIntentInfo = true;
                }
            } else if (opt.equals("-t")) {
                type = nextArgRequired();
                if (intent == baseIntent) {
                    hasIntentInfo = true;
                }
            } else if (opt.equals("-c")) {
                intent.addCategory(nextArgRequired());
                if (intent == baseIntent) {
                    hasIntentInfo = true;
                }
            } else if (opt.equals("-e") || opt.equals("--es")) {
                String key = nextArgRequired();
                String value = nextArgRequired();
                intent.putExtra(key, value);
            } else if (opt.equals("--esn")) {
                String key = nextArgRequired();
                intent.putExtra(key, (String) null);
            } else if (opt.equals("--ei")) {
                String key = nextArgRequired();
                String value = nextArgRequired();
                intent.putExtra(key, Integer.valueOf(value));
            } else if (opt.equals("--eu")) {
                String key = nextArgRequired();
                String value = nextArgRequired();
                intent.putExtra(key, Uri.parse(value));
            } else if (opt.equals("--eia")) {
                String key = nextArgRequired();
                String value = nextArgRequired();
                String[] strings = value.split(",");
                int[] list = new int[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    list[i] = Integer.valueOf(strings[i]);
                }
                intent.putExtra(key, list);
            } else if (opt.equals("--el")) {
                String key = nextArgRequired();
                String value = nextArgRequired();
                intent.putExtra(key, Long.valueOf(value));
            } else if (opt.equals("--ela")) {
                String key = nextArgRequired();
                String value = nextArgRequired();
                String[] strings = value.split(",");
                long[] list = new long[strings.length];
                for (int i = 0; i < strings.length; i++) {
                    list[i] = Long.valueOf(strings[i]);
                }
                intent.putExtra(key, list);
            } else if (opt.equals("--ez")) {
                String key = nextArgRequired();
                String value = nextArgRequired();
                intent.putExtra(key, Boolean.valueOf(value));
            } else if (opt.equals("-n")) {
                String str = nextArgRequired();
                ComponentName cn = ComponentName.unflattenFromString(str);
                if (cn == null) throw new IllegalArgumentException("Bad component name: " + str);
                intent.setComponent(cn);
                if (intent == baseIntent) {
                    hasIntentInfo = true;
                }
            } else if (opt.equals("-f")) {
                String str = nextArgRequired();
                intent.setFlags(Integer.decode(str).intValue());
            } else if (opt.equals("--grant-read-uri-permission")) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else if (opt.equals("--grant-write-uri-permission")) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else if (opt.equals("--exclude-stopped-packages")) {
                intent.addFlags(Intent.FLAG_EXCLUDE_STOPPED_PACKAGES);
            } else if (opt.equals("--include-stopped-packages")) {
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            } else if (opt.equals("--debug-log-resolution")) {
                intent.addFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
            } else if (opt.equals("--activity-brought-to-front")) {
                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            } else if (opt.equals("--activity-clear-top")) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            } else if (opt.equals("--activity-clear-when-task-reset")) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            } else if (opt.equals("--activity-exclude-from-recents")) {
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            } else if (opt.equals("--activity-launched-from-history")) {
                intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
            } else if (opt.equals("--activity-multiple-task")) {
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            } else if (opt.equals("--activity-no-animation")) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            } else if (opt.equals("--activity-no-history")) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            } else if (opt.equals("--activity-no-user-action")) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            } else if (opt.equals("--activity-previous-is-top")) {
                intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            } else if (opt.equals("--activity-reorder-to-front")) {
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            } else if (opt.equals("--activity-reset-task-if-needed")) {
                intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            } else if (opt.equals("--activity-single-top")) {
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            } else if (opt.equals("--activity-clear-task")) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            } else if (opt.equals("--activity-task-on-home")) {
                intent.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            } else if (opt.equals("--receiver-registered-only")) {
                intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
            } else if (opt.equals("--receiver-replace-pending")) {
                intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
            } else if (opt.equals("--foreground-service")) {
                L.logIF("addCustomFlag");
                intent.addCustomFlag(EsIntent.FLAG_FOREGROUND_SERVICE);
            } else if (opt.equals("--selector")) {
                intent.setDataAndType(data, type);
                intent = new EsIntent();
            }
//      else if (opt.equals("-D")) {
//        mDebugOption = true;
//      } else if (opt.equals("-W")) {
//        mWaitOption = true;
//      } else if (opt.equals("-P")) {
//        mProfileFile = nextArgRequired();
//        mProfileAutoStop = true;
//      } else if (opt.equals("--start-profiler")) {
//        mProfileFile = nextArgRequired();
//        mProfileAutoStop = false;
//      } else if (opt.equals("-R")) {
//        mRepeat = Integer.parseInt(nextArgRequired());
//      } else if (opt.equals("-S")) {
//        mStopOption = true;
//      }
            else {
                System.err.println("Error: Unknown option: " + opt);
//                return null;
            }
        }
        intent.setDataAndType(data, type);
        final boolean hasSelector = intent != baseIntent;
        if (hasSelector) {
            // A selector was specified; fix up.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                baseIntent.setSelector(intent);
            }
            intent = baseIntent;
        }
        String arg = nextArg();
        baseIntent = null;
        if (arg == null) {
            if (hasSelector) {
                // If a selector has been specified, and no arguments
                // have been supplied for the main Intent, then we can
                // assume it is ACTION_MAIN CATEGORY_LAUNCHER; we don't
                // need to have a component name specified yet, the
                // selector will take care of that.
                baseIntent = new EsIntent(Intent.ACTION_MAIN);
                baseIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            }
        } else if (arg.indexOf(':') >= 0) {
            // The argument is a URI.  Fully parse it, and use that result
            // to fill in any data not specified so far.
            baseIntent = EsIntent.parseUri(arg, Intent.URI_INTENT_SCHEME);
        } else if (arg.indexOf('/') >= 0) {
            // The argument is a component name.  Build an Intent to launch
            // it.
            baseIntent = new EsIntent(Intent.ACTION_MAIN);
            baseIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            baseIntent.setComponent(ComponentName.unflattenFromString(arg));
        } else {
            // Assume the argument is a package name.
            baseIntent = new EsIntent(Intent.ACTION_MAIN);
            baseIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            baseIntent.setPackage(arg);
        }
        if (baseIntent != null) {
            Bundle extras = intent.getExtras();
            intent.replaceExtras((Bundle) null);
            Bundle uriExtras = baseIntent.getExtras();
            baseIntent.replaceExtras((Bundle) null);
            if (intent.getAction() != null && baseIntent.getCategories() != null) {
                HashSet<String> cats = new HashSet<String>(baseIntent.getCategories());
                for (String c : cats) {
                    baseIntent.removeCategory(c);
                }
            }
            intent.fillIn(baseIntent, Intent.FILL_IN_COMPONENT | Intent.FILL_IN_SELECTOR);
            if (extras == null) {
                extras = uriExtras;
            } else if (uriExtras != null) {
                uriExtras.putAll(extras);
                extras = uriExtras;
            }
            intent.replaceExtras(extras);
            hasIntentInfo = true;
        }
        if (!hasIntentInfo) throw new IllegalArgumentException("No intent supplied");
        return intent;
    }

    private String nextOption() {
        if (mCurArgData != null) {
            String prev = mArgs[mNextArg - 1];
            throw new IllegalArgumentException("No argument expected after \"" + prev + "\"");
        }
        if (mNextArg >= mArgs.length) {
            return null;
        }
        String arg = mArgs[mNextArg];
        if (!arg.startsWith("-")) {
            return null;
        }
        mNextArg++;
        if (arg.equals("--")) {
            return null;
        }
        if (arg.length() > 1 && arg.charAt(1) != '-') {
            if (arg.length() > 2) {
                mCurArgData = arg.substring(2);
                return arg.substring(0, 2);
            } else {
                mCurArgData = null;
                return arg;
            }
        }
        mCurArgData = null;
        return arg;
    }

    private String nextArg() {
        if (mCurArgData != null) {
            String arg = mCurArgData;
            mCurArgData = null;
            return arg;
        } else if (mNextArg < mArgs.length) {
            return mArgs[mNextArg++];
        } else {
            return null;
        }
    }

    private String nextArgRequired() {
        String arg = nextArg();
        if (arg == null) {
            String prev = mArgs[mNextArg - 1];
            throw new IllegalArgumentException("Argument expected after \"" + prev + "\"");
        }
        return arg;
    }

}