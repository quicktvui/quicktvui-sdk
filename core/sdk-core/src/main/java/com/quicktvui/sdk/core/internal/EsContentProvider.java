package com.quicktvui.sdk.core.internal;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import com.quicktvui.sdk.core.EsManager;
import com.quicktvui.sdk.core.udp.EsUdpServer;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.model.ThirdEvent;

/**
 * Create by weipeng on 2022/06/03 10:46
 * Describe
 */

abstract class AbstractContentProvider extends ContentProvider {

    public static final int INSERT = 1;
    public static final int DELETE = 2;
    public static final int UPDATE = 3;
    public static final int QUERY = 4;

    public static final UriMatcher mUriMatcher;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    }

    protected final void registerUri(String path, int code) {
        mUriMatcher.addURI(getAuthority(), path, code);
    }

    abstract String getAuthority();

    abstract MatrixCursor onQuery(int code);

    abstract Uri onInsert(ContentValues values);

    private int matchUri(Uri uri) {
        return mUriMatcher.match(uri);
    }

    @Override
    public boolean onCreate() {
        registerUri("insert", INSERT);
        registerUri("delete", DELETE);
        registerUri("update", UPDATE);
        registerUri("query", QUERY);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int code = matchUri(uri);
        return code == UriMatcher.NO_MATCH ? null : onQuery(code);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int code = matchUri(uri);
        return code == UriMatcher.NO_MATCH ? null : onInsert(values);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

}

public class EsContentProvider extends AbstractContentProvider {

    private static final String AUTHORITIES_SUFFIX = ".content.provider.EsContentProvider";

    private static final int QUERY_RUNNING = 10;
    private static final int QUERY_NET_INFO = 11;

    public static final String K_CID = "cid";
    public static final String K_DEVICE_NAME = "name";

    public static final String K_RUNNING = "running";

    public static final String K_NET_IP = "ip";
    public static final String K_NET_PORT = "port";

    @Override
    public boolean onCreate() {
        boolean success = super.onCreate();
        registerUri("query/running", QUERY_RUNNING);
        registerUri("query/net_info", QUERY_NET_INFO);
        return success;
    }

    @Override
    String getAuthority() {
        return getContext().getPackageName() + AUTHORITIES_SUFFIX;
    }

    @Override
    MatrixCursor onQuery(int code) {

        MatrixCursor cursor = null;

        switch (code) {
            case QUERY:
                cursor = new MatrixCursor(new String[]{
                        K_CID, K_DEVICE_NAME
                });

                cursor.addRow(new Object[]{
                        EsContext.get().getClientId(),
                        EsContext.get().getDeviceName()
                });
                break;
            case QUERY_RUNNING:
                cursor = new MatrixCursor(new String[]{
                        K_RUNNING
                });

                cursor.addRow(new Object[]{
                        EsManager.get().isEsRunning() ? 1 : -1
                });

                break;
            case QUERY_NET_INFO:
                cursor = new MatrixCursor(new String[]{
                        K_NET_IP, K_NET_PORT
                });

                cursor.addRow(new Object[]{
                        EsUdpServer.get().getIp(),
                        EsUdpServer.get().getPort()
                });
                break;
        }

        return cursor;
    }

    @Override
    Uri onInsert(ContentValues values) {
        if(values != null){
            ThirdEvent event = new ThirdEvent();
            event.data = values.getAsString("data");
            event.ip = values.getAsString("ip");
            event.port = values.getAsInteger("port");
            event.from = values.getAsString("from");
            EsProxy.get().receiveThirdEvent(event);
        }
        return null;
    }
}
