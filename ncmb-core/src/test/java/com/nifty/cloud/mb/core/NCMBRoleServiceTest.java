package com.nifty.cloud.mb.core;

import com.squareup.okhttp.mockwebserver.MockWebServer;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.ArrayList;


@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
@RunWith(NCMBTestRunner.class)
public class NCMBRoleServiceTest {

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
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    protected NCMBRoleService getRoleService() {
        return (NCMBRoleService)NCMB.factory(NCMB.ServiceType.ROLE);
    }

    /**
     * - 内容：createRoleが成功する事を確認する
     * - 結果：objectIdが正しく作成されること
     */
    @Test
    public void createRole() throws Exception {
        NCMBRoleService roleService = getRoleService();
        String roleName = "dummyRoleName";

        JSONObject json = roleService.createRole(roleName);
        Assert.assertEquals("dummyObjectId", json.getString("objectId"));
    }

    /**
     * - 内容：createRoleInBackground が成功する事を確認する
     * - 結果：objectIdが正しく作成されること
     */
    @Test
    public void createRoleInBackground() throws Exception {
        NCMBRoleService roleService = getRoleService();
        String roleName = "dummyRoleName";

        roleService.createRoleInBackground(roleName, new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject json, NCMBException e) {
                Assert.assertEquals(e, null);
                try {
                    Assert.assertEquals("dummyObjectId", json.getString("objectId"));
                } catch (JSONException e1) {
                    Assert.fail(e.getMessage());
                }
            }
        });
    }

    /**
     * - 内容：deleteRole が成功する事を確認する
     * - 結果：例外が発生しないこと
     */
    @Test
    public void deleteRole() throws Exception {
        NCMBRoleService roleService = getRoleService();
        String roleId = "dummyRoleId";

        try {
            roleService.deleteRole(roleId);
        } catch (NCMBException e) {
            Assert.assertTrue("Exception throwed", false);
        }
    }

    /**
     * - 内容：deleteRoleInBackground が成功する事を確認する
     * - 結果：callback に例外が返らないこと
     */
    @Test
    public void deleteRoleInBackground() throws Exception {
        NCMBRoleService roleService = getRoleService();
        String roleId = "dummyRoleId";

        roleService.deleteRoleInBackground(roleId, new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                Assert.assertEquals(e, null);
            }
        });
    }

    /**
     * - 内容：getRole が成功する事を確認する
     * - 結果：正しく作成された NCMBRole オブジェクトが返ること
     */
    @Test
    public void getRole() throws Exception {
        NCMBRoleService roleService = getRoleService();
        String roleId = "dummyRoleId";

        NCMBRole role = roleService.getRole(roleId);
        Assert.assertEquals(role.getObjectId(), roleId);
    }

    /**
     * - 内容：getRoleInBackground が成功する事を確認する
     * - 結果：callback に正しく作成された NCMBRole オブジェクトが返ること
     */
    @Test
    public void getRoleInBackground() throws Exception {
        NCMBRoleService roleService = getRoleService();
        final String roleId = "dummyRoleId";

        roleService.getRoleInBackground(roleId, new RoleCallback() {
            @Override
            public void done(NCMBRole role, NCMBException e) {
                Assert.assertEquals(e, null);
                Assert.assertEquals(role.getObjectId(), roleId);
            }
        });
    }

    protected ArrayList<NCMBUser> generateUsers(int count) throws JSONException, NCMBException {
        JSONObject userJson = new JSONObject();
        userJson.put("userName", "dummyUserName");
        userJson.put("createDate", "2015-10-10T00:00:01.000Z");

        ArrayList<NCMBUser> users = new ArrayList<NCMBUser>();
        for (int i = 1; i <= count; ++i) {
            String objectId = "dummyUserObjectId" + String.valueOf(i);
            userJson.put("objectId", objectId);

            NCMBUser user = new NCMBUser(userJson);
            users.add(user);
        }
        return users;
    }

    /**
     * - 内容：addUserRelations が成功する事を確認する
     * - 結果：例外が発生しないこと
     */
    @Test
    public void addUserRelations() throws Exception {

        NCMBRoleService roleService = getRoleService();
        String roleId = "dummyRoleId";

        try {
            int numUsers = 2;
            ArrayList<NCMBUser> users = generateUsers(numUsers);
            roleService.addUserRelations(roleId, users);
        } catch (NCMBException e) {
            Assert.assertTrue("addUserRelations throws excepiton", false);
        }
    }

    /**
     * - 内容：addUserRelationsInBackground が成功する事を確認する
     * - 結果：callback に例外が返らないこと
     */
    @Test
    public void addUserRelationsInBackground() throws Exception {

        NCMBRoleService roleService = getRoleService();
        String roleId = "dummyRoleId";
        int numUsers = 2;
        ArrayList<NCMBUser> users = generateUsers(numUsers);

        roleService.addUserRelationsInBackground(roleId, users, new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject json, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
            }
        });
    }

    protected ArrayList<NCMBRole> generateRoles(int count) throws JSONException, NCMBException {
        JSONObject roleJson = new JSONObject();
        roleJson.put("roleName", "dummyUserName");
        roleJson.put("createDate", "2015-10-10T00:00:01.000Z");

        ArrayList<NCMBRole> roles = new ArrayList<NCMBRole>();
        for (int i = 1; i <= count; ++i) {
            String objectId = "dummyRoleObjectId" + String.valueOf(i);
            roleJson.put("objectId", objectId);

            NCMBRole role = new NCMBRole(roleJson);
            roles.add(role);
        }
        return roles;
    }

    /**
     * - 内容：addRoleRelations が成功する事を確認する
     * - 結果：例外が発生しないこと
     */
    @Test
    public void addRoleRelations() throws Exception {

        NCMBRoleService roleService = getRoleService();
        String roleId = "dummyRoleId";

        try {
            int numRoles = 2;
            ArrayList<NCMBRole> roles = generateRoles(numRoles);
            roleService.addRoleRelations(roleId, roles);
        } catch (NCMBException e) {
            Assert.assertTrue("addRoleRelations throws excepiton", false);
        }
    }

    /**
     * - 内容：addRoleRelationsInBackground が成功する事を確認する
     * - 結果：callback に例外が返らないこと
     */
    @Test
    public void addRoleRelationsInBackground() throws Exception {

        NCMBRoleService roleService = getRoleService();
        String roleId = "dummyRoleId";
        int numRoles = 2;
        ArrayList<NCMBRole> roles = generateRoles(numRoles);

        roleService.addRoleRelationsInBackground(roleId, roles, new ExecuteServiceCallback() {
            @Override
            public void done(JSONObject json, NCMBException e) {
                if (e != null) {
                    Assert.fail(e.getMessage());
                }
            }
        });
    }

    protected NCMBAcl generateAcl() {
        String userId = "dummyRwUserId";
        String roleId = "dummyRwRoleId";

        NCMBAcl acl = new NCMBAcl();
        acl.setReadAccess(userId, true);
        acl.setWriteAccess(userId, true);
        acl.setRoleReadAccess(roleId, true);
        acl.setRoleWriteAccess(roleId, true);

        return acl;
    }

    /**
     * - 内容：setAcl が成功する事を確認する
     * - 結果：例外が発生しないこと
     */
    @Test
    public void setAcl() throws Exception {
        NCMBRoleService roleService = getRoleService();
        String roleId = "dummyRoleId";

        try {
            NCMBAcl acl = generateAcl();
            roleService.setAcl(roleId, acl);
        } catch (NCMBException e) {
            Assert.assertTrue("setAcl throws excepiton", false);
        }
    }

    /**
     * - 内容：setAclInBackground が成功する事を確認する
     * - 結果：callback に例外が返らないこと
     */
    @Test
    public void setAclInBackground() throws Exception {
        NCMBRoleService roleService = getRoleService();
        String roleId = "dummyRoleId";

        NCMBAcl acl = generateAcl();
        roleService.setAclInBackground(roleId, acl, new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                Assert.assertEquals(e, null);
            }
        });
    }
}
