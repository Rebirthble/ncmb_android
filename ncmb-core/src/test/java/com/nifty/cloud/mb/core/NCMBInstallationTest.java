package com.nifty.cloud.mb.core;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import com.squareup.okhttp.mockwebserver.MockWebServer;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.res.builder.RobolectricPackageManager;
import org.robolectric.shadows.ShadowLog;

import java.text.DateFormat;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
 * Test for NCMBInstallationTest
 */
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
@RunWith(NCMBTestRunner.class)
public class NCMBInstallationTest {

    private MockWebServer mServer;

    @Before
    public void setup() throws Exception {

        //set application information
        RobolectricPackageManager rpm = (RobolectricPackageManager) Robolectric.application.getPackageManager();
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = NCMBInstallationServiceTest.PACKAGE_NAME;
        packageInfo.versionName = NCMBInstallationServiceTest.APP_VERSION;
        packageInfo.applicationInfo = new ApplicationInfo();
        packageInfo.applicationInfo.packageName = NCMBInstallationServiceTest.PACKAGE_NAME;
        packageInfo.applicationInfo.name = NCMBInstallationServiceTest.APP_NAME;
        rpm.addPackage(packageInfo);

        //setup mocServer
        mServer = new MockWebServer();
        mServer.setDispatcher(NCMBDispatcher.dispatcher);
        mServer.start();
        String mockServerUrl = mServer.getUrl("/").toString();

        //initialization
        NCMB.initialize(Robolectric.application,
                "applicationKey",
                "clientKey",
                mockServerUrl,
                null);

        MockitoAnnotations.initMocks(this);

        ShadowLog.stream = System.out;
    }

    @After
    public void teardown() throws Exception {
        NCMBInstallationService.clearCurrentInstallation();
        mServer.shutdown();
    }

    /**
     * getRegistrationInBackgroundを呼び出すとdeviceTokenがプロパティにセットされる事
     *
     * @throws Exception
     */
    @Test
    public void getRegistrationID_with_valid_senderID() throws Exception {

        NCMBInstallation current = NCMBInstallation.getCurrentInstallation();
        NCMBInstallation mockInstallation = spy(current);
        doReturn("testDeviceToken").when(mockInstallation).getDeviceTokenFromGCM(anyString());
        doReturn(true).when(mockInstallation).checkPlayServices(any(Context.class));
        mockInstallation.getRegistrationIdInBackground("test", new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                //本来であれば保存処理を書く
                if (e != null) {
                    e.printStackTrace();
                }

            }
        });

        Assert.assertEquals("testDeviceToken", mockInstallation.getDeviceToken());
    }

    //region save test

    /**
     * - 内容：save(POST)が成功することを確認する
     * - 結果：同期でオブジェクトの保存が出来る事
     */
    @Test
    public void save_post() throws Exception {
        //post
        NCMBException error = null;
        NCMBInstallation installation = new NCMBInstallation();
        installation.setDeviceToken("xxxxxxxxxxxxxxxxxxx");
        try {
            installation.save();
        } catch (NCMBException e) {
            error = e;
        }

        //check
        Assert.assertNull(error);
        Assert.assertEquals("7FrmPTBKSNtVjajm", installation.getObjectId());
        Assert.assertEquals("xxxxxxxxxxxxxxxxxxx", installation.getDeviceToken());
        DateFormat format = NCMBDateFormat.getIso8601();
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), installation.getCreateDate());
    }

    /**
     * - 内容：save(PUT)が成功することを確認する
     * - 結果：同期でオブジェクトの更新が出来る事
     */
    @Test
    public void save_put() throws Exception {
        NCMBException error = null;

        //post
        NCMBInstallation installation = new NCMBInstallation();
        installation.setDeviceToken("xxxxxxxxxxxxxxxxxxx");
        installation.put("key", "value1");
        try {
            installation.save();
        } catch (NCMBException e) {
            error = e;
        }
        Assert.assertNull(error);
        Assert.assertEquals("7FrmPTBKSNtVjajm", installation.getObjectId());
        Assert.assertEquals("value1", installation.getString("key"));
        Assert.assertEquals("xxxxxxxxxxxxxxxxxxx", installation.getDeviceToken());
        DateFormat format = NCMBDateFormat.getIso8601();
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), installation.getCreateDate());
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), installation.getUpdateDate());

        //put
        try {
            installation.put("key", "value_update");
            installation.save();
        } catch (NCMBException e) {
            error = e;
        }

        //check
        Assert.assertNull(error);
        Assert.assertEquals("7FrmPTBKSNtVjajm", installation.getObjectId());
        Assert.assertEquals("value_update", installation.getString("key"));
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), installation.getCreateDate());
        Assert.assertEquals(format.parse("2014-06-04T11:28:30.348Z"), installation.getUpdateDate());
    }

    /**
     * - 内容：saveInBackground(POST)が成功することを確認する
     * - 結果：非同期でオブジェクトの保存が出来る事
     */
    @Test
    public void saveInBackground_post() throws Exception {
        //post
        final NCMBInstallation installation = new NCMBInstallation();
        installation.setDeviceToken("xxxxxxxxxxxxxxxxxxx");
        installation.saveInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                Assert.assertNull(e);
            }
        });

        //check
        Assert.assertEquals("7FrmPTBKSNtVjajm", installation.getObjectId());
        Assert.assertEquals("xxxxxxxxxxxxxxxxxxx", installation.getDeviceToken());
        DateFormat format = NCMBDateFormat.getIso8601();
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), installation.getCreateDate());
    }

    /**
     * - 内容：saveInBackground(PUT)が成功することを確認する
     * - 結果：非同期でオブジェクトの更新が出来る事
     */
    @Test
    public void saveInBackground_put() throws Exception {
        //post
        NCMBInstallation installation = new NCMBInstallation();
        installation.setDeviceToken("xxxxxxxxxxxxxxxxxxx");
        installation.put("key", "value1");
        installation.saveInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                Assert.assertNull(e);
            }
        });

        //check
        Assert.assertEquals("7FrmPTBKSNtVjajm", installation.getObjectId());
        Assert.assertEquals("value1", installation.getString("key"));
        Assert.assertEquals("xxxxxxxxxxxxxxxxxxx", installation.getDeviceToken());
        DateFormat format = NCMBDateFormat.getIso8601();
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), installation.getCreateDate());
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), installation.getUpdateDate());

        //put
        installation.put("key", "value_update");
        installation.saveInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                Assert.assertNull(e);
            }
        });

        //check
        Assert.assertEquals("7FrmPTBKSNtVjajm", installation.getObjectId());
        Assert.assertEquals("value_update", installation.getString("key"));
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), installation.getCreateDate());
        Assert.assertEquals(format.parse("2014-06-04T11:28:30.348Z"), installation.getUpdateDate());
    }

    /**
     * - 内容：saveInBackground(callback無し)が成功することを確認する
     * - 結果：非同期でオブジェクトの保存が出来る事
     */
    @Test
    public void saveInBackground_none_callback() throws Exception {
        //post
        NCMBInstallation installation = new NCMBInstallation();
        installation.setDeviceToken("xxxxxxxxxxxxxxxxxxx");
        installation.put("key", "value1");
        installation.saveInBackground();

        //check
        Assert.assertEquals("7FrmPTBKSNtVjajm", installation.getObjectId());
        Assert.assertEquals("value1", installation.getString("key"));
        Assert.assertEquals("xxxxxxxxxxxxxxxxxxx", installation.getDeviceToken());
        DateFormat format = NCMBDateFormat.getIso8601();
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), installation.getCreateDate());
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), installation.getUpdateDate());
    }

    /**
     * - 内容：deviceTokenを取得していない状態で保存を実行した場合にエラーが出ることを確認する
     * - 結果：エラーが発生すること
     */
    @Test
    public void saveInBackground_error_none_deviceToken() throws Exception {
        //post
        NCMBException error = null;
        NCMBInstallation installation = new NCMBInstallation();
        try {
            installation.save();
        } catch (NCMBException e) {
            error = e;
        }

        //check
        Assert.assertNotNull(error);
        Assert.assertEquals("java.lang.IllegalArgumentException: registrationId is must not be null.", error.getMessage());
    }

    // endregion

    //region fetch test

    /**
     * - 内容：fetchが成功することを確認する
     * - 結果：同期でオブジェクトの取得が出来る事
     */
    @Test
    public void fetch() throws Exception {
        //post
        NCMBException error = null;
        NCMBInstallation installation = new NCMBInstallation();
        installation.setObjectId("7FrmPTBKSNtVjajm");
        try {
            installation.fetch();
        } catch (NCMBException e) {
            error = e;
        }

        //check
        Assert.assertNull(error);
        Assert.assertEquals("7FrmPTBKSNtVjajm", installation.getObjectId());
        Assert.assertEquals("xxxxxxxxxxxxxxxxxxx", installation.getDeviceToken());
        DateFormat format = NCMBDateFormat.getIso8601();
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), installation.getCreateDate());
    }

    /**
     * - 内容：fetchInBackgroundが成功することを確認する
     * - 結果：非同期でオブジェクトの取得が出来る事
     */
    @Test
    public void fetchInBackground() throws Exception {
        //post
        final NCMBInstallation installation = new NCMBInstallation();
        installation.setObjectId("7FrmPTBKSNtVjajm");
        installation.fetchInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                Assert.assertNull(e);
            }
        });

        //check
        Assert.assertEquals("7FrmPTBKSNtVjajm", installation.getObjectId());
        Assert.assertEquals("xxxxxxxxxxxxxxxxxxx", installation.getDeviceToken());
        DateFormat format = NCMBDateFormat.getIso8601();
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), installation.getCreateDate());
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), installation.getUpdateDate());
    }

    /**
     * - 内容：fetchInBackground(callback無し)が成功することを確認する
     * - 結果：非同期でオブジェクトの取得が出来る事
     */
    @Test
    public void fetchInBackground_none_callback() throws Exception {
        //post
        NCMBInstallation installation = new NCMBInstallation();
        installation.setDeviceToken("xxxxxxxxxxxxxxxxxxx");
        installation.setObjectId("7FrmPTBKSNtVjajm");
        installation.fetchInBackground();

        //check
        Assert.assertEquals("7FrmPTBKSNtVjajm", installation.getObjectId());
        Assert.assertEquals("value", installation.getString("key"));
        Assert.assertEquals("xxxxxxxxxxxxxxxxxxx", installation.getDeviceToken());
        DateFormat format = NCMBDateFormat.getIso8601();
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), installation.getCreateDate());
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), installation.getUpdateDate());
    }

    /**
     * - 内容：objectIdを設定していない状態で検索を実行した場合にエラーが出ることを確認する
     * - 結果：エラーが発生すること
     */
    @Test
    public void fetch_error_none_objectId() throws Exception {
        //post
        NCMBException error = null;
        NCMBInstallation installation = new NCMBInstallation();
        try {
            installation.fetch();
        } catch (NCMBException e) {
            error = e;
        }

        //check
        Assert.assertNotNull(error);
        Assert.assertEquals("java.lang.IllegalArgumentException: objectId is must not be null.", error.getMessage());
    }
    //endregion

    //region delete test

    /**
     * - 内容：deleteが成功することを確認する
     * - 結果：同期でオブジェクトの削除が出来る事
     */
    @Test
    public void delete() throws Exception {
        //post
        NCMBException error = null;
        NCMBInstallation installation = new NCMBInstallation();
        installation.setObjectId("7FrmPTBKSNtVjajm");
        try {
            installation.delete();
        } catch (NCMBException e) {
            error = e;
        }

        //check
        Assert.assertNull(error);
    }

    /**
     * - 内容：deleteInBackgroundが成功することを確認する
     * - 結果：非同期でオブジェクトの削除が出来る事
     */
    @Test
    public void deleteInBackground() throws Exception {
        //post
        final NCMBInstallation installation = new NCMBInstallation();
        installation.setObjectId("7FrmPTBKSNtVjajm");
        installation.deleteInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                //check
                Assert.assertNull(e);
            }
        });
    }

    /**
     * - 内容：deleteInBackground(callback無し)が成功することを確認する
     * - 結果：非同期でオブジェクトの削除が出来る事
     */
    @Test
    public void deleteInBackground_none_callback() throws Exception {
        //post
        NCMBInstallation installation = new NCMBInstallation();
        installation.setObjectId("7FrmPTBKSNtVjajm");
        installation.fetchInBackground();
    }

    /**
     * - 内容：objectIdを設定していない状態で削除を実行した場合にエラーが出ることを確認する
     * - 結果：エラーが発生すること
     */
    @Test
    public void delete_error_none_objectId() throws Exception {
        //post
        NCMBException error = null;
        NCMBInstallation installation = new NCMBInstallation();
        try {
            installation.fetch();
        } catch (NCMBException e) {
            error = e;
        }

        //check
        Assert.assertNotNull(error);
        Assert.assertEquals("java.lang.IllegalArgumentException: objectId is must not be null.", error.getMessage());
    }
    //endregion


}
