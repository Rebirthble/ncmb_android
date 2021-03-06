package com.nifty.cloud.usersample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBUser;
import com.nifty.cloud.mb.core.NCMBUserService;

public class MainActivity extends AppCompatActivity {

    Intent intent;
    static final String INTENT_RESULT = "result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初期化
        NCMB.initialize(
                this,
                "applicationKey",
                "clientKey");

        intent = new Intent(this, ResultActivity.class);
    }

    /**
     * on signUp button clicked
     *
     * @param v view
     */
    public void onSignUpClicked(View v) {
        String result;
        try {
            NCMBUserService userService = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
            NCMBUser user = userService.registerByName("TestUser", "TestPassword");
            result = createSuccessString(user);
            Toast.makeText(this, "新規登録成功", Toast.LENGTH_SHORT).show();
        } catch (NCMBException error) {
            Toast.makeText(this, "新規登録失敗", Toast.LENGTH_SHORT).show();
            result = createFailedString(error);
        }

        intent.putExtra(INTENT_RESULT, result);
        startActivityForResult(intent, 0);
    }

    /**
     * on login button clicked
     *
     * @param v view
     */
    public void onLoginClicked(View v) {
        String result;
        try {
            NCMBUserService userService = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
            NCMBUser user = userService.loginByName("TestUser", "TestPassword");
            result = createSuccessString(user);
            Toast.makeText(this, "ログイン成功", Toast.LENGTH_SHORT).show();
        } catch (NCMBException error) {
            Toast.makeText(this, "ログイン失敗", Toast.LENGTH_SHORT).show();
            result = createFailedString(error);
        }

        intent.putExtra(INTENT_RESULT, result);
        startActivityForResult(intent, 0);
    }

    /**
     * on logout button clicked
     *
     * @param v view
     */
    public void onLogoutClicked(View v) {
        String result;
        try {
            NCMBUserService userService = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
            userService.logout();
            NCMBUser user = NCMBUser.getCurrentUser();
            result = createSuccessString(user);
            Toast.makeText(this, "ログアウト成功", Toast.LENGTH_SHORT).show();
        } catch (NCMBException error) {
            Toast.makeText(this, "ログアウト失敗", Toast.LENGTH_SHORT).show();
            result = createFailedString(error);
        }

        intent.putExtra(INTENT_RESULT, result);
        startActivityForResult(intent, 0);
    }


    /**
     * on currentUser button clicked
     *
     * @param v view
     */
    public void onCurrentUserClicked(View v) {
        String result;
        try {
            NCMBUser user = NCMBUser.getCurrentUser();
            result = createSuccessString(user);
            Toast.makeText(this, "ログインユーザー : " + user.getUserName(), Toast.LENGTH_SHORT).show();
        } catch (NCMBException e) {
            Toast.makeText(this, "ログインユーザーの取得に失敗", Toast.LENGTH_SHORT).show();
            result = createFailedString(e);
        }
        intent.putExtra(INTENT_RESULT, result);
        startActivityForResult(intent, 0);
    }

    /**
     * on user delete button clicked
     *
     * @param v view
     */
    public void onUserDeleteClicked(View v) {
        String result;
        try {
            NCMBUserService userService = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
            userService.deleteUser(NCMBUser.getCurrentUser().getUserId());
            NCMBUser user = NCMBUser.getCurrentUser();
            result = createSuccessString(user);
            Toast.makeText(this, "削除成功", Toast.LENGTH_SHORT).show();
        } catch (NCMBException error) {
            Toast.makeText(this, "削除失敗", Toast.LENGTH_SHORT).show();
            result = createFailedString(error);
        }

        intent.putExtra(INTENT_RESULT, result);
        startActivityForResult(intent, 0);
    }

    /**
     * on mail signUp button clicked
     *
     * @param v view
     */
    public void onMailSignUpClicked(View v) {
        String result;
        try {
            NCMBUserService userService = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
            userService.inviteByMail("mailAddress");
            result = "指定したアドレスに招待メールを送信しました。\n受信したメールから会員登録を行ってください。";
            Toast.makeText(this, "登録メール送信成功", Toast.LENGTH_SHORT).show();
        } catch (NCMBException error) {
            Toast.makeText(this, "登録メール送信失敗", Toast.LENGTH_SHORT).show();
            result = createFailedString(error);
        }

        intent.putExtra(INTENT_RESULT, result);
        startActivityForResult(intent, 0);
    }

    /**
     * on mail signUp button clicked
     *
     * @param v view
     */
    public void onMailLoginUpClicked(View v) {
        String result;
        try {
            NCMBUserService userService = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
            NCMBUser user = userService.loginByMail("mailAddress", "TestPassword");
            result = createSuccessString(user);
            Toast.makeText(this, "メールログイン成功", Toast.LENGTH_SHORT).show();
        } catch (NCMBException error) {
            Toast.makeText(this, "メールログイン失敗", Toast.LENGTH_SHORT).show();
            result = createFailedString(error);
        }

        intent.putExtra(INTENT_RESULT, result);
        startActivityForResult(intent, 0);
    }

    String createSuccessString(NCMBUser user) throws NCMBException {
        String successString = null;
        try{
            successString = "【Success】\n";
            successString += "ID : " + user.getUserId() + "\n";
            successString += "UserName : " + user.getUserName() + "\n";
            successString += "MailAddress : " + user.getMailAddress() + "\n";
            successString += "SessionToken : " + user.getValue("sessionToken") + "\n";
        }catch (NCMBException error){
            successString += "SessionToken : " + null;
        }
        return successString;
    }

    String createFailedString(NCMBException error) {
        return "【Failed】\n" +
                "StatusCode : " + error.getCode() + "\n" +
                "Message : " + error.getMessage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
