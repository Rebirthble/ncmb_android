package com.nifty.cloud.mb.core;

import android.content.Context;

import com.squareup.okhttp.mockwebserver.MockWebServer;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
@RunWith(NCMBTestRunner.class)
public class NCMBUserServiceTest {
    private MockWebServer mServer;

    @Before
    public void setup() throws Exception {
        Robolectric.getFakeHttpLayer().interceptHttpRequests(false);

        //setup mocServer
        mServer = new MockWebServer();
        mServer.setDispatcher(NCMBDispatcher.dispatcher);
        mServer.start();
        String mocServerUrl = mServer.getUrl("/").toString();

        //initialization
        NCMB.initialize(Robolectric.application,
                "appKey",
                "clientKKey",
                mocServerUrl,
                null);

        ShadowLog.stream = System.out;
    }

    @After
    public void teardown() {
        NCMBUserService.clearCurrentUser();
    }

    protected NCMBUserService getUserService() {
        return (NCMBUserService)NCMB.factory(NCMB.ServiceType.USER);
    }
    /*** Test Case NCMBUserService ***/

    /**
     * - 内容：registerByNameが成功する事を確認する
     * - 結果：NCMBUserオブジェクトが正しく作成されること
     */
    @Test
    public void registerByUser() throws Exception {
        NCMBUserService userService = getUserService();
        String userName = "Nifty Tarou";
        String password = "niftytarou";

        NCMBUser user = userService.registerByName(userName, password);
        Assert.assertEquals("dummyObjectId", user.getObjectId());
        Assert.assertEquals(userName, user.getUserName());
    }

    /**
     * - 内容：registerByNameInBackground が成功する事を確認する
     * - 結果：NCMBUserオブジェクトが正しく作成されること
     */
    @Test
    public void registerByUserInBackground() throws Exception {
        NCMBUserService userService = getUserService();
        final String userName = "Nifty Tarou";
        final String password = "niftytarou";

        userService.registerByNameInBackground(userName, password, new LoginCallback() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                Assert.assertEquals(e, null);
                Assert.assertEquals("dummyObjectId", user.getObjectId());
                Assert.assertEquals(userName, user.getUserName());
            }
        });
    }

    /**
     * - 内容：registerByOauth が Facebook で成功する事を確認する
     * - 結果：NCMBUserオブジェクトが正しく作成されること
     */
    @Test
    public void registerByOauthFacebook() throws Exception {
        JSONObject oauthParams = new JSONObject();
        oauthParams.put("type", "facebook");
        oauthParams.put("id", "facebookDummyId");
        oauthParams.put("access_token", "facebookDummyAccessToken");
        oauthParams.put("expiration_date", "2016-06-07T01:02:03.004Z");

        NCMBUserService userService = getUserService();
        NCMBUser user = userService.registerByOauth(oauthParams);
        Assert.assertEquals("dummyObjectId", user.getObjectId());
    }

    /**
     * - 内容：registerByOauthInBackground が Facebook で成功する事を確認する
     * - 結果：NCMBUserオブジェクトが正しく作成されること
     */
    @Test
    public void registerByOauthFacebookInBackground() throws Exception {
        JSONObject oauthParams = new JSONObject();
        oauthParams.put("type", "facebook");
        oauthParams.put("id", "facebookDummyId");
        oauthParams.put("access_token", "facebookDummyAccessToken");
        oauthParams.put("expiration_date", "2016-06-07T01:02:03.004Z");

        NCMBUserService userService = getUserService();
        userService.registerByOauthInBackground(oauthParams, new LoginCallback() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                Assert.assertEquals(e, null);
                Assert.assertEquals("dummyObjectId", user.getObjectId());
            }
        });
    }

    /**
     * - 内容：registerByOauth が Twitter で成功する事を確認する
     * - 結果：NCMBUserオブジェクトが正しく作成されること
     */
    @Test
    public void registerByOauthTwitter() throws Exception {
        JSONObject oauthParams = new JSONObject();
        oauthParams.put("type", "twitter");
        oauthParams.put("id", "twitterDummyId");
        oauthParams.put("screen_name", "twitterDummyScreenName");
        oauthParams.put("oauth_consumer_key", "twitterDummyConsumerKey");
        oauthParams.put("consumer_secret", "twitterDummyConsumerSecret");
        oauthParams.put("oauth_token", "twitterDummyOauthToken");
        oauthParams.put("oauth_token_secret", "twitterDummyOauthSecret");

        NCMBUserService userService = getUserService();
        NCMBUser user = userService.registerByOauth(oauthParams);
        Assert.assertEquals("dummyObjectId", user.getObjectId());
    }

    /**
     * - 内容：registerByOauthInBackground が Twitter で成功する事を確認する
     * - 結果：NCMBUserオブジェクトが正しく作成されること
     */
    @Test
    public void registerByOauthTwitterInBackground() throws Exception {
        JSONObject oauthParams = new JSONObject();
        oauthParams.put("type", "twitter");
        oauthParams.put("id", "twitterDummyId");
        oauthParams.put("screen_name", "twitterDummyScreenName");
        oauthParams.put("oauth_consumer_key", "twitterDummyConsumerKey");
        oauthParams.put("consumer_secret", "twitterDummyConsumerSecret");
        oauthParams.put("oauth_token", "twitterDummyOauthToken");
        oauthParams.put("oauth_token_secret", "twitterDummyOauthSecret");

        NCMBUserService userService = getUserService();
        userService.registerByOauthInBackground(oauthParams, new LoginCallback() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                Assert.assertEquals(e, null);
                Assert.assertEquals("dummyObjectId", user.getObjectId());
            }
        });
    }

    /**
     * - 内容：registerByOauth が Google で成功する事を確認する
     * - 結果：NCMBUserオブジェクトが正しく作成されること
     */
    @Test
    public void registerByOauthGoogle() throws Exception {
        JSONObject oauthParams = new JSONObject();
        oauthParams.put("type", "google");
        oauthParams.put("id", "googleDummyId");
        oauthParams.put("access_token", "googleDummyAccessToken");

        NCMBUserService userService = getUserService();
        NCMBUser user = userService.registerByOauth(oauthParams);
        Assert.assertEquals("dummyObjectId", user.getObjectId());
    }

    /**
     * - 内容：registerByOauthInBackground が Google で成功する事を確認する
     * - 結果：NCMBUserオブジェクトが正しく作成されること
     */
    @Test
    public void registerByOauthGoogleInBackground() throws Exception {
        JSONObject oauthParams = new JSONObject();
        oauthParams.put("type", "google");
        oauthParams.put("id", "googleDummyId");
        oauthParams.put("access_token", "googleDummyAccessToken");

        NCMBUserService userService = getUserService();
        userService.registerByOauthInBackground(oauthParams, new LoginCallback() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                Assert.assertEquals(e, null);
                Assert.assertEquals("dummyObjectId", user.getObjectId());
            }
        });
    }

    /**
     * - 内容：registerByOauth が Anonymous で成功する事を確認する
     * - 結果：NCMBUserオブジェクトが正しく作成されること
     */
    @Test
    public void registerByOauthAnonymous() throws Exception {
        JSONObject oauthParams = new JSONObject();
        oauthParams.put("type", "anonymous");
        oauthParams.put("id", "anonymousDummyId");

        NCMBUserService userService = getUserService();
        NCMBUser user = userService.registerByOauth(oauthParams);
        Assert.assertEquals("dummyObjectId", user.getObjectId());
    }

    /**
     * - 内容：registerByOauthInBackground が Anonymous で成功する事を確認する
     * - 結果：NCMBUserオブジェクトが正しく作成されること
     */
    @Test
    public void registerByOauthAnonymousInBackground() throws Exception {
        JSONObject oauthParams = new JSONObject();
        oauthParams.put("type", "anonymous");
        oauthParams.put("id", "anonymousDummyId");

        NCMBUserService userService = getUserService();
        userService.registerByOauthInBackground(oauthParams, new LoginCallback() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                Assert.assertEquals(e, null);
                Assert.assertEquals("dummyObjectId", user.getObjectId());
            }
        });
    }

    /**
     * - 内容：inviteByMail が成功する事を確認する
     * - 結果：NCMBException が発生しないこと
     */
    @Test
    public void inviteByMail() throws Exception {
        String mailAddress = "sample@example.com";

        NCMBUserService userService = getUserService();
        try {
            userService.inviteByMail(mailAddress);
        } catch (NCMBException e) {
            Assert.assertTrue("Exception throwed", false);
        }
    }

    /**
     * - 内容：inviteByMailInBackground が成功する事を確認する
     * - 結果：DoneCallback に例外が返らないこと
     */
    @Test
    public void inviteByMailInBackground() throws Exception {
        String mailAddress = "sample@example.com";

        NCMBUserService userService = getUserService();
        userService.inviteByMailInBackground(mailAddress, new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                Assert.assertEquals(e, null);
            }
        });
    }

    /**
     * - 内容：getUser が成功する事を確認する
     * - 結果：NCMBUser オブジェクトが正しく生成されること
     */
    @Test
    public void getUser() throws Exception {
        String userId = "dummyUserId";

        NCMBUserService userService = getUserService();
        NCMBUser user = userService.getUser(userId);
    }

    /**
     * - 内容：getUserInBackground が成功する事を確認する
     * - 結果：NCMBUser オブジェクトが正しく生成されること
     */
    @Test
    public void getUserInBackground() throws Exception {
        String userId = "dummyUserId";

        NCMBUserService userService = getUserService();
        userService.getUserInBackground(userId, new UserCallback() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                Assert.assertEquals(e, null);
                Assert.assertEquals("dummyObjectId", user.getObjectId());
            }
        });
    }

    /**
     * - 内容：updateUser が成功する事を確認する
     * - 結果：NCMBException が発生しないこと
     */
    @Test
    public void updateUser() throws Exception {
        String userId = "dummyUserId";
        JSONObject params = new JSONObject();
        params.put("userName", "NCMB Tarou");
        params.put("mailAddress", "mobilebackend@example.com");

        NCMBUserService userService = getUserService();
        try {
            userService.updateUser(userId, params);
        } catch (NCMBException e) {
            Assert.assertTrue("Exception throwed", false);
        }
    }

    /**
     * - 内容：updateUserInBackground が成功する事を確認する
     * - 結果：DoneCallback に例外が返らないこと
     */
    @Test
    public void updateUserInBackground() throws Exception {
        String userId = "dummyUserId";
        JSONObject params = new JSONObject();
        params.put("userName", "NCMB Tarou");
        params.put("mailAddress", "mobilebackend@example.com");

        NCMBUserService userService = getUserService();
        userService.updateUserInBackground(userId, params, new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject json, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
                try {
                    JSONAssert.assertEquals("{\"updateDate\":\"2014-06-04T11:28:30.348Z\"}", json.toString(), true);
                } catch (JSONException e1) {
                    Assert.fail(e.getMessage());
                }
            }
        });
    }

    /**
     * - 内容：loginByName が成功する事を確認する
     * - 結果：NCMBUser オブジェクトが正しく生成されること
     */
    @Test
    public void loginByName() throws Exception {
        String userName = "Nifty Tarou";
        String password = "dummyPassword";

        NCMBUserService userService = getUserService();

        NCMBUser user = userService.loginByName(userName, password);
        Assert.assertEquals("dummyObjectId", user.getObjectId());
        Assert.assertEquals(userName, user.getUserName());
    }

    /**
     * - 内容：loginByNameInBackground が成功する事を確認する
     * - 結果：DoneCallback に例外が返らないこと
     */
    @Test
    public void loginByNameInBackground() throws Exception {
        final String userName = "Nifty Tarou";
        final String password = "dummyPassword";

        NCMBUserService userService = getUserService();
        userService.loginByNameInBackground(userName, password, new LoginCallback() {
            @Override
            public void done(NCMBUser user, NCMBException e) {
                Assert.assertEquals(e, null);
                Assert.assertEquals("dummyObjectId", user.getObjectId());
                Assert.assertEquals(userName, user.getUserName());
            }
        });
    }

    /**
     * - 内容：logout が成功する事を確認する
     * - 結果：NCMBException が発生しないこと
     */
    @Test
    public void logout() throws Exception {
        NCMBUserService userService = getUserService();

        userService.mContext.sessionToken = "testSessionToken";
        userService.mContext.userId = "testUserId";

        try {
            userService.logout();
        } catch (NCMBException e) {
            Assert.assertTrue("Exception throwed", false);
        }

        Assert.assertNull(userService.mContext.sessionToken);
        Assert.assertNull(userService.mContext.userId);
    }

    /**
     * - 内容：logoutInBackground が成功する事を確認する
     * - 結果：callback に null が渡されること
     */
    @Test
    public void logoutInBackground() throws Exception {
        NCMBUserService userService = getUserService();

        userService.mContext.sessionToken = "testSessionToken";
        userService.mContext.userId = "testUserId";

        userService.logoutInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                Assert.assertEquals(e, null);
            }
        });

        Assert.assertNull(userService.mContext.sessionToken);
        Assert.assertNull(userService.mContext.userId);
    }

    /**
     * - 内容：searchUser が全件取得で成功する事を確認する
     * - 結果：result に NCMBUser が正しく格納されていること
     */
    @Test
    public void searchUserAll() throws Exception {
        NCMBUserService userService = getUserService();

        ArrayList<NCMBUser> result = userService.searchUser(null);
        Assert.assertEquals(result.size(), 2);

        NCMBUser user1 = result.get(0);
        Assert.assertEquals(user1.getObjectId(), "dummyObjectId01");
        Assert.assertEquals(user1.getUserName(), "Nifty Tarou");

        NCMBUser user2 = result.get(1);
        Assert.assertEquals(user2.getObjectId(), "dummyObjectId02");
        Assert.assertEquals(user2.getUserName(), "Nifty Jirou");
    }

    /**
     * - 内容：searchUserInBackground が全件取得で成功する事を確認する
     * - 結果：result に NCMBUser が正しく格納されていること
     */
    @Test
    public void searchUserAllInBackground() throws Exception {
        NCMBUserService userService = getUserService();
        userService.searchUserInBackground(null, new SearchUserCallback() {
            @Override
            public void done(ArrayList<NCMBUser> result, NCMBException e) {
                Assert.assertEquals(e, null);
                Assert.assertEquals(result.size(), 2);

                NCMBUser user1 = result.get(0);
                Assert.assertEquals(user1.getObjectId(), "dummyObjectId01");
                Assert.assertEquals(user1.getUserName(), "Nifty Tarou");

                NCMBUser user2 = result.get(1);
                Assert.assertEquals(user2.getObjectId(), "dummyObjectId02");
                Assert.assertEquals(user2.getUserName(), "Nifty Jirou");
            }
        });
    }

    /**
     * - 内容：searchUser が成功する事を確認する
     * - 結果：result に NCMBUser が正しく格納されていること
     */
    @Test
    public void searchUser() throws Exception {
        JSONObject conditions = new JSONObject();
        conditions.put("userName", "Nifty Tarou");

        NCMBUserService userService = getUserService();

        ArrayList<NCMBUser> result = userService.searchUser(conditions);
        Assert.assertEquals(result.size(), 1);

        NCMBUser user1 = result.get(0);
        Assert.assertEquals(user1.getObjectId(), "dummyObjectId01");
        Assert.assertEquals(user1.getUserName(), "Nifty Tarou");
    }

    /**
     * - 内容：searchUserInBackground が全件取得で成功する事を確認する
     * - 結果：result に NCMBUser が正しく格納されていること
     */
    @Test
    public void searchUserInBackground() throws Exception {
        JSONObject conditions = new JSONObject();
        conditions.put("userName", "Nifty Tarou");

        NCMBUserService userService = getUserService();
        userService.searchUserInBackground(conditions, new SearchUserCallback() {
            @Override
            public void done(ArrayList<NCMBUser> result, NCMBException e) {
                Assert.assertEquals(e, null);
                Assert.assertEquals(result.size(), 1);

                NCMBUser user1 = result.get(0);
                Assert.assertEquals(user1.getObjectId(), "dummyObjectId01");
                Assert.assertEquals(user1.getUserName(), "Nifty Tarou");
            }
        });
    }


    /**
     * - 内容：currentUserのファイルが生成されている事を確認する
     * - 結果：objectId,key,createDateの値を含むファイルが作成されている事
     */
    @Test
    public void currentUser_newLocalFile() throws Exception {
        //create currentUser
        NCMBUserService userService = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        NCMBUser user = userService.registerByName("Nifty Tarou", "niftytarou");
        Assert.assertEquals("dummyObjectId", user.getObjectId());
        DateFormat format = NCMBDateFormat.getIso8601();
        Date resultDate = format.parse("2015-01-01T00:00:00.000Z");
        Assert.assertEquals(resultDate, user.getCreateDate());

        //check new create localFile
        File localFile = new File(NCMB.sCurrentContext.context.getDir("NCMB", Context.MODE_PRIVATE), "currentUser");
        if (!localFile.exists()){
            Assert.fail("currentUserFile is not created.");
        }

        //check localFile data
        JSONObject localData = new JSONObject();
        try {
            BufferedReader br = new BufferedReader(new FileReader(localFile));
            String information = br.readLine();
            br.close();
            localData = new JSONObject(information);
        } catch (IOException | JSONException e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals("dummyObjectId", localData.getString("objectId"));
        Assert.assertEquals("dummySessionToken", localData.getString("sessionToken"));
        Assert.assertEquals("dummySessionToken", NCMB.sCurrentContext.sessionToken);
        Assert.assertEquals("Nifty Tarou", localData.getString("userName"));
        //パスワードをローカルに持たない
        //Assert.assertEquals("dummyPassword", localData.getString("password"));
        Assert.assertEquals("2015-01-01T00:00:00.000Z", localData.getString("createDate"));
    }

    /**
     * - 内容：v1時のパスで生成されたCurrentUser情報がv2で正しく取得されている事を確認する
     * - 結果：objectId,phone,createDate,userNameの値を含むcurrentUserが取得されている事
     */
    @Test
    public void currentUser_v1_From_v2() throws Exception {
        //create currentUser data
        JSONObject localFileData = new JSONObject();
        localFileData.put("sessionToken","dummySessionToken");
        localFileData.put("phone","000-000-0000");
        localFileData.put("objectId","dummyUserId");
        localFileData.put("mailAddress","email@example.com");
        localFileData.put("classname","user");
        localFileData.put("userName","dummyUser");
        localFileData.put("password","dummyPassword");
        localFileData.put("createDate","2015-09-10T02:24:03.597Z");
        localFileData.put("updateDate","2015-09-11T02:24:03.597Z");

        //create currentUser from v1 path
        File localFile = new File(NCMB.sCurrentContext.context.getDir("NCMB", Context.MODE_PRIVATE), "currentUser");
        try {
            FileOutputStream out = new FileOutputStream(localFile);
            out.write(localFileData.toString().getBytes("UTF-8"));
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //check currentUser
        NCMBUser currentUser = NCMBUser.getCurrentUser();
        Assert.assertEquals("dummySessionToken", currentUser.getString("sessionToken"));
        Assert.assertEquals("dummySessionToken", NCMB.sCurrentContext.sessionToken);
        Assert.assertEquals("000-000-0000", currentUser.getString("phone"));
        Assert.assertEquals("dummyUserId", currentUser.getObjectId());
        Assert.assertEquals("email@example.com", currentUser.getMailAddress());
        Assert.assertEquals("dummyUser", currentUser.getUserName());
        //パスワードをローカルに持たない
        //Assert.assertEquals("dummyPassword", currentUser.getValue("password"));
        DateFormat format = NCMBDateFormat.getIso8601();
        Date resultCreateDate = format.parse("2015-09-10T02:24:03.597Z");
        Assert.assertEquals(resultCreateDate, currentUser.getCreateDate());
        Date resultUpdateDate = format.parse("2015-09-11T02:24:03.597Z");
        Assert.assertEquals(resultUpdateDate, currentUser.getUpdateDate());

        //check upDate currentUser
        NCMBUserService userService = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        try {
            JSONObject update = new JSONObject("{key:value}");
            userService.updateUser(currentUser.getObjectId(), update);
        }catch (NCMBException error){
            Assert.fail(error.getMessage());
        }
    }

    /**
     * - 内容：currentUserの作成が成功する事を確認する
     * - 結果：currentUserにobjectId,sessionToken,createDateの値が含まれている事
     */
    @Test
    public void currentUser_POST() throws Exception {
        //connect post
        NCMBUserService userService = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        NCMBUser user = userService.registerByName("Nifty Tarou", "niftytarou");
        Assert.assertEquals("dummyObjectId", user.getObjectId());
        DateFormat format = NCMBDateFormat.getIso8601();
        Date resultDate = format.parse("2015-01-01T00:00:00.000Z");
        Assert.assertEquals(resultDate, user.getCreateDate());

        //check currentUser
        NCMBUser currentUser = NCMBUser.getCurrentUser();
        Assert.assertEquals("dummyObjectId", currentUser.getObjectId());
        Assert.assertEquals("Nifty Tarou", currentUser.getUserName());
        Assert.assertEquals("dummySessionToken", currentUser.getString("sessionToken"));
        Assert.assertEquals("dummySessionToken", NCMB.sCurrentContext.sessionToken);
        Assert.assertEquals("Nifty Tarou", currentUser.getUserName());
        //パスワードをローカルに持たない
        //Assert.assertEquals("dummyPassword", currentUser.getValue("password"));
        Assert.assertEquals(resultDate, currentUser.getCreateDate());
    }

    /**
     * - 内容：currentUserの更新が成功する事を確認する
     * - 結果：currentUserのkeyの値が更新され、updateDateの値が含まれている事
     */
    @Test
    public void currentUser_PUT() throws Exception {
        //connect post
        NCMBUserService userService = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        JSONObject params = new JSONObject();
        params.put("key", "value");
        userService.updateUser("dummyUserId", params);

        //check currentUser
        NCMBUser currentUser = NCMBUser.getCurrentUser();
        Assert.assertEquals("dummyUserId", currentUser.getObjectId());
        Assert.assertEquals("value", currentUser.getString("key"));

        //connect put
        JSONObject update = new JSONObject();
        update.put("key", "value2");
        userService.updateUser("dummyUserId", update);

        //check currentUser
        currentUser = NCMBUser.getCurrentUser();
        Assert.assertEquals("dummyUserId", currentUser.getObjectId());
        Assert.assertEquals("value2", currentUser.getString("key"));
        DateFormat format = NCMBDateFormat.getIso8601();
        Date resultDate = format.parse("2014-06-04T11:28:30.348Z");
        Assert.assertEquals(resultDate, currentUser.getUpdateDate());
    }

    /**
     * - 内容：currentUserの削除(DELETE)が成功する事を確認する
     * - 結果：currentUserの値が削除されている事
     */
    @Test
    public void currentUser_DELETE() throws Exception {
        //connect post
        NCMBUserService userService = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        NCMBUser user = userService.registerByName("Nifty Tarou", "niftytarou");
        Assert.assertEquals("dummyObjectId", user.getObjectId());
        DateFormat format = NCMBDateFormat.getIso8601();
        Date resultDate = format.parse("2015-01-01T00:00:00.000Z");
        Assert.assertEquals(resultDate, user.getCreateDate());

        //check currentUser
        NCMBUser currentUser = NCMBUser.getCurrentUser();
        Assert.assertEquals("dummyObjectId", currentUser.getObjectId());

        //connect delete
        userService.deleteUser(currentUser.getObjectId());

        //check currentUser
        currentUser = NCMBUser.getCurrentUser();
        Assert.assertNull(NCMB.sCurrentContext.userId);
        Assert.assertNull(NCMB.sCurrentContext.sessionToken);
        Assert.assertNull(currentUser.getObjectId());
    }

    @Test
    public void currentUser_DELETE_asynchronously () throws Exception {
        //connect post
        NCMBUserService userService = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        NCMBUser user = userService.registerByName("Nifty Tarou", "niftytarou");
        Assert.assertEquals("dummyObjectId", user.getObjectId());
        DateFormat format = NCMBDateFormat.getIso8601();
        Date resultDate = format.parse("2015-01-01T00:00:00.000Z");
        Assert.assertEquals(resultDate, user.getCreateDate());

        //check currentUser
        NCMBUser currentUser = NCMBUser.getCurrentUser();
        Assert.assertEquals("dummyObjectId", currentUser.getObjectId());

        userService.deleteUserInBackground(currentUser.getObjectId(), new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject json, NCMBException e) {
                Assert.assertNull(e);
                Assert.assertNull(NCMB.sCurrentContext.userId);
                Assert.assertNull(NCMB.sCurrentContext.sessionToken);
                Assert.assertNull(NCMBUser.getCurrentUser().getObjectId());
            }
        });
    }

    /**
     * - 内容：currentUserの作成(login)が成功する事を確認する
     * - 結果：currentUserにobjectId,sessionToken,createDateの値が含まれている事
     */
    @Test
    public void currentUser_login() throws Exception {
        //connect post
        NCMBUserService userService = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        NCMBUser user = userService.loginByName("Nifty Tarou", "dummyPassword");
        Assert.assertEquals("Nifty Tarou", user.getUserName());

        //check currentUser
        NCMBUser currentUser = NCMBUser.getCurrentUser();
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc",currentUser.getString("sessionToken"));
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.sCurrentContext.sessionToken);
        Assert.assertEquals("Nifty Tarou", user.getUserName());
    }

    /**
     * - 内容：currentUserの削除(logout)が成功する事を確認する
     * - 結果：currentUserの値が削除されている事
     */
    @Test
    public void currentUser_logout() throws Exception {
        //connect post
        NCMBUserService userService = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        userService.logout();

        //check currentUser
        NCMBUser currentUser = NCMBUser.getCurrentUser();
        Assert.assertNull(NCMB.sCurrentContext.sessionToken);
        Assert.assertNull(currentUser.getObjectId());
    }

    /**
     * - 内容：PUTでcurrentUserの自動ログアウトが成功する事を確認する
     * - 結果：currentUserの値が削除されている事
     */
    @Test
    public void currentUser_AutoLogout_PUT() throws Exception {
        //connect post
        NCMBUserService userService = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        NCMBUser user = userService.registerByName("Nifty Tarou", "niftytarou");
        Assert.assertEquals("dummyObjectId", user.getObjectId());

        //check currentUser
        NCMBUser currentUser = NCMBUser.getCurrentUser();
        Assert.assertEquals("dummyObjectId", currentUser.getObjectId());
        Assert.assertEquals("dummySessionToken", currentUser.getString("sessionToken"));
        Assert.assertEquals("dummySessionToken", NCMB.sCurrentContext.sessionToken);

        //connect auto logout
        NCMBException error = null;
        try {
            JSONObject update = new JSONObject("{error:test}");
            userService.updateUser(currentUser.getObjectId(), update);
        } catch (NCMBException e) {
            error = e;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(NCMBException.INVALID_AUTH_HEADER, error.getCode());
        Assert.assertEquals("Authentication error by header incorrect.", error.getMessage());

        //check currentUser
        currentUser = NCMBUser.getCurrentUser();
        Assert.assertNull(currentUser.getObjectId());
        Assert.assertNull(NCMB.sCurrentContext.sessionToken);
        Assert.assertNull(NCMB.sCurrentContext.userId);
    }

    /**
     * - 内容：PUTでcurrentUserの自動ログアウトが成功する事を確認する（非同期通信）
     * - 結果：currentUserの値が削除されている事
     */
    @Test
    public void currentUser_AutoLogout_PUT_asynchronously() throws Exception {
        NCMBUserService userService = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        NCMBUser user = userService.registerByName("Nifty Tarou", "niftytarou");
        Assert.assertEquals("dummyObjectId", user.getObjectId());

        //check currentUser
        NCMBUser currentUser = NCMBUser.getCurrentUser();
        Assert.assertEquals("dummyObjectId", currentUser.getObjectId());
        Assert.assertEquals("dummySessionToken", currentUser.getString("sessionToken"));
        Assert.assertEquals("dummySessionToken", NCMB.sCurrentContext.sessionToken);

        JSONObject update = new JSONObject("{error:test}");
        userService.updateUserInBackground(user.getObjectId(), update, new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject json, NCMBException e) {
                if (e == null) {
                    Assert.fail("this callback must be raise exception");
                } else {
                    Assert.assertEquals(NCMBException.INVALID_AUTH_HEADER, e.getCode());
                    Assert.assertNull(NCMBUser.getCurrentUser().getObjectId());
                    Assert.assertNull(NCMB.sCurrentContext.sessionToken);
                    Assert.assertNull(NCMB.sCurrentContext.userId);
                }
            }
        });
    }

    /**
     * - 内容：staticのsessionTokenが破棄されてもlocalFileのsessionTokenが反映される事を確認する
     * - 結果：sessionTokenの値が設定されている事
     */
    @Test
    public void currentUser_sessionToken() throws Exception {
        //connect post
        NCMBUserService userService = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
        NCMBUser user = userService.loginByName("Nifty Tarou", "dummyPassword");
        Assert.assertEquals("Nifty Tarou", user.getUserName());

        //check currentUser
        NCMBUser currentUser = NCMBUser.getCurrentUser();
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.sCurrentContext.sessionToken);
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc",currentUser.getString("sessionToken"));
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc",NCMBUser.getSessionToken());

        //clear currentUser
        NCMB.sCurrentContext.sessionToken = null;
        NCMBUser.currentUser = null;

        //check newCurrentUser
        NCMBUser newCurrentUser = NCMBUser.getCurrentUser();
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.sCurrentContext.sessionToken);
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc",newCurrentUser.getString("sessionToken"));
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc",NCMBUser.getSessionToken());
    }
}
