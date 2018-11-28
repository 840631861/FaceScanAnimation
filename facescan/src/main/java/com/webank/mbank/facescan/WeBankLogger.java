package com.webank.mbank.facescan;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.util.Log;

public class WeBankLogger {
    private static final String ROOT_TAT = "WeBank";
    private static final String ROOT_TAG_PREFIX = "WeBank-";

    private static WeBankLogger.ILog logger;
    private static int logLevel = 3;

    static {
        if (!com.webank.mbank.facescan.BuildConfig.DEBUG) {
            closeLog();
        }
    }


    public WeBankLogger() {
    }

    public static void setLogLevel(int level) {
        logLevel = level;
    }

    public static void closeLog() {
        logLevel = 10;
    }

    public static void setLogger(WeBankLogger.ILog log) {
        logger = log;
    }

    public static void v(String s, Object... args) {
        v(null, null, s, args);
    }

    public static void d(String s, Object... args) {
        d(null, null, s, args);

    }

    public static void i(String s, Object... args) {
        i(null, null, s, args);

    }

    public static void w(String s, Object... args) {
        w(null, null, s, args);
    }

    public static void e(String s, Object... args) {
        e(null, null, s, args);

    }

    public static void wtf(String s, Object... args) {
        wtf(null, null, s, args);

    }

    public static void v(String tag, String s, Object... args) {
        v(tag, null, s, args);
    }

    public static void d(String tag, String s, Object... args) {
        d(tag, null, s, args);

    }

    public static void i(String tag, String s, Object... args) {
        i(tag, null, s, args);
    }

    public static void w(String tag, String s, Object... args) {
        w(tag, null, s, args);

    }

    public static void e(String tag, String s, Object... args) {
        e(tag, null, s, args);

    }

    public static void wtf(String tag, String s, Object... args) {
        wtf(tag, null, s, args);

    }


    public static void v(String tag, Throwable t, String s, Object... args) {
        tag = getTag(tag);
        if (logger != null) {
            logger.v(tag, t, s, args);
        } else if (logLevel <= 2) {
            if (args.length > 0) {
                Log.v(tag, String.format(s, args), t);
            } else {
                Log.v(tag, s, t);
            }
        }

    }

    public static void d(String tag, Throwable t, String s, Object... args) {
        tag = getTag(tag);
        if (logger != null) {
            logger.d(tag, t, s, args);
        } else if (logLevel <= 3) {
            if (args.length > 0) {
                Log.d(tag, String.format(s, args), t);
            } else {
                Log.d(tag, s, t);
            }
        }

    }

    public static void i(String tag, Throwable t, String s, Object... args) {
        tag = getTag(tag);
        if (logger != null) {
            logger.i(tag, t, s, args);
        } else if (logLevel <= 4) {
            if (args.length > 0) {
                Log.i(tag, String.format(s, args), t);
            } else {
                Log.i(tag, s, t);
            }
        }

    }


    private static String getTag(String tag) {
        if (tag == null) {
            return ROOT_TAT;
        } else {
            return ROOT_TAG_PREFIX + tag;
        }
    }

    public static void w(String tag, Throwable t, String s, Object... args) {
        tag = getTag(tag);
        if (logger != null) {
            logger.w(tag, t, s, args);
        } else if (logLevel <= 5) {
            if (args.length > 0) {
                Log.w(tag, String.format(s, args), t);
            } else {
                Log.w(tag, s, t);
            }
        }

    }

    public static void e(String tag, Throwable t, String s, Object... args) {
        tag = getTag(tag);
        if (logger != null) {
            logger.e(tag, null, s, args);
        } else if (logLevel <= 6) {
            if (args.length > 0) {
                Log.e(tag, String.format(s, args), t);
            } else {
                Log.e(tag, s, t);
            }
        }

    }

    public static void wtf(String tag, Throwable t, String s, Object... args) {
        tag = getTag(tag);
        if (logger != null) {
            logger.wtf(tag, t, s, args);
        } else if (logLevel <= 7) {
            if (args.length > 0) {
                Log.wtf(tag, String.format(s, args), t);
            } else {
                Log.wtf(tag, s, t);
            }
        }

    }


    public abstract static class ILog {
        public ILog() {
        }

        public abstract void v(String tag, Throwable t, String msg, Object... args);

        public abstract void d(String tag, Throwable t, String msg, Object... args);

        public abstract void i(String tag, Throwable t, String msg, Object... args);

        public abstract void w(String tag, Throwable t, String msg, Object... args);

        public abstract void e(String tag, Throwable t, String msg, Object... args);


        public void wtf(String tag, Throwable t, String msg, Object... args) {
        }
    }
}
