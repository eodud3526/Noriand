package com.noriand.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.noriand.R;
import com.noriand.common.CommonPreferences;
import com.noriand.network.ApiController;
import com.noriand.util.StringUtil;
import com.noriand.view.dialog.CommonDialog;
import com.noriand.vo.request.RequestLoginVO;
import com.noriand.vo.response.ResponseLoginVO;


public class LoginActivity extends BaseActivity {
// --------------------------------------------------
    // Common


    // --------------------------------------------------
    // View
    private EditText metEmail = null;
    private EditText metPassword = null;
    private Button mbtLogin = null;

    private TextView mtvHere = null;

    // --------------------------------------------------
    // Data

    // --------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setStatusBar(Color.WHITE);

        setBase();
        setLayout();
        setListener();
    }

    private void onBack() {
        finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            onBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setBase() {

    }

    private void setLayout() {
        metEmail = (EditText)findViewById(R.id.et_login_email);
        metPassword = (EditText)findViewById(R.id.et_login_password);
        mbtLogin = (Button)findViewById(R.id.btn_login);

        mtvHere = (TextView)findViewById(R.id.tv_login_here);
    }

    private void setListener() {
        metPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE) {
                    checkAll();
                    return true;
                }
                return false;
            }
        });
        mbtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAll();
            }
        });

        mtvHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveJoinActivity();
            }
        });
    }

    private void checkAll() {
        String email = metEmail.getText().toString();
        if(StringUtil.isEmpty(email)) {
            showDialogOneButton("이메일을 입력해 주세요.", new CommonDialog.DialogConfirmListener() {
                @Override
                public void onConfirm() {
                    metEmail.requestFocus();
                }
                @Override
                public void onCancel() {
                }
            });
            return;
        }
        if(!StringUtil.isValidEmail(email)) {
            showDialogOneButton("올바른 이메일을 입력해 주세요.", new CommonDialog.DialogConfirmListener() {
                @Override
                public void onConfirm() {
                    metEmail.requestFocus();
                }
                @Override
                public void onCancel() {
                }
            });
            return;
        }

        String password = metPassword.getText().toString();
        if(StringUtil.isEmpty(password)) {
            showDialogOneButton("비밀번호를 입력해 주세요.", new CommonDialog.DialogConfirmListener() {
                @Override
                public void onConfirm() {
                    metPassword.requestFocus();
                }
                @Override
                public void onCancel() {
                }
            });
            return;
        }
        if(password.length() < 4) {
            showDialogOneButton("비밀번호를 4자리 이상 입력해 주세요.", new CommonDialog.DialogConfirmListener() {
                @Override
                public void onConfirm() {
                    metPassword.requestFocus();
                }
                @Override
                public void onCancel() {
                }
            });
            return;
        }

        RequestLoginVO requestItem = new RequestLoginVO(mActivity);
        requestItem.setLogin(email, password);
        networkLogin(requestItem);
    }

    private void networkLogin(final RequestLoginVO requestItem) {
        mApiController.login(mActivity, requestItem, new ApiController.ApiLoginListener() {
            @Override
            public void onSuccess(ResponseLoginVO item) {
                if(item == null) {
                    showDialogOneButton(getResources().getString(R.string.please_retry_network));
                    return;
                }

                if(!item.isConfirm || item.userNo == 0) {
                    CommonPreferences.resetPreferences(getApplicationContext());
                    showDialogOneButton("등록되지 않은 이메일이거나,\n비밀번호를 잘못 입력 하셨습니다.", new CommonDialog.DialogConfirmListener() {
                        @Override
                        public void onConfirm() {
                            StringUtil.selectionLast(metPassword);
                        }
                        @Override
                        public void onCancel() {

                        }
                    });
                    return;
                }

                Context context = getApplicationContext();
                CommonPreferences.putInt(context, CommonPreferences.TAG_USER_NO, item.userNo);
                CommonPreferences.putString(context, CommonPreferences.TAG_EMAIL, requestItem.email);
                CommonPreferences.putString(context, CommonPreferences.TAG_PHONE_NUMBER, item.phoneNumber);
                CommonPreferences.putString(context, CommonPreferences.TAG_JOIN_TIME, item.joinTime);
                CommonPreferences.putString(context, CommonPreferences.TAG_LAST_LOGIN_TIME, item.lastLoginTime);

                Toast.makeText(context, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                moveDeviceSelectActivity();
            }

            @Override
            public void onFail() {
                showRetryDialogTwoButton(new CommonDialog.DialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        networkLogin(requestItem);
                    }

                    @Override
                    public void onCancel() {
                    }
                });
            }
        });
    }

    public void moveDeviceSelectActivity() {
        Intent intent = new Intent(mActivity, DeviceSelectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        finish();
    }
    public void moveJoinActivity() {
        Intent intent = new Intent(mActivity, JoinActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        finish();
    }
}