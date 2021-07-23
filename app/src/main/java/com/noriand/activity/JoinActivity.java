package com.noriand.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.noriand.R;
import com.noriand.common.CommonPreferences;
import com.noriand.network.ApiController;
import com.noriand.util.StringUtil;
import com.noriand.view.dialog.CommonDialog;
import com.noriand.vo.request.RequestJoinVO;
import com.noriand.vo.response.ResponseJoinVO;


public class JoinActivity extends BaseActivity {
// --------------------------------------------------
    // Common


    // --------------------------------------------------
    // View
    private Button mbtClose = null;

    private Button mbtJoin = null;

    private EditText metEmail = null;
    private EditText metPassword = null;
    private EditText metRetryPassword = null;
    private EditText metPhoneNumber = null;

    // --------------------------------------------------
    // Data


    // --------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        setStatusBar(Color.WHITE);

        setLayout();
        setListener();
    }

    private void setLayout() {
        mbtClose = (Button)findViewById(R.id.btn_join_close);
        mbtJoin = (Button)findViewById(R.id.btn_join);

        metEmail = (EditText)findViewById(R.id.et_join_email);
        metPassword = (EditText)findViewById(R.id.et_join_password);
        metRetryPassword = (EditText)findViewById(R.id.et_join_repassword);
        metPhoneNumber = (EditText)findViewById(R.id.et_join_phone_number);
    }
    private void setListener() {
        mbtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveLoginActivity();
                onBack();
            }
        });

        mbtJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAll();
            }
        });

        metEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkChange();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        metPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkChange();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        metRetryPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkChange();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        metPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkChange();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void checkChange() {
        String email = metEmail.getText().toString();
        if(StringUtil.isEmpty(email)) {
            mbtJoin.setBackgroundResource(R.drawable.btn_gray);
            return;
        }
        if(!StringUtil.isValidEmail(email)) {
            mbtJoin.setBackgroundResource(R.drawable.btn_gray);
            return;
        }

        String password = metPassword.getText().toString();
        if(StringUtil.isEmpty(password)) {
            mbtJoin.setBackgroundResource(R.drawable.btn_gray);
            return;
        }
        if(password.length() < 4) {
            mbtJoin.setBackgroundResource(R.drawable.btn_gray);
            return;
        }
        String retryPassword = metRetryPassword.getText().toString();
        if(StringUtil.isEmpty(retryPassword)) {
            mbtJoin.setBackgroundResource(R.drawable.btn_gray);
            return;
        }

        if(!password.equals(retryPassword)) {
            mbtJoin.setBackgroundResource(R.drawable.btn_gray);
            return;
        }

        String phoneNumber = metPhoneNumber.getText().toString();
        if(StringUtil.isEmpty(phoneNumber)) {
            mbtJoin.setBackgroundResource(R.drawable.btn_gray);
            return;
        }

        mbtJoin.setBackgroundResource(R.drawable.selector_btn_common_purple);
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
        String retryPassword = metRetryPassword.getText().toString();
        if(StringUtil.isEmpty(retryPassword)) {
            showDialogOneButton("비밀번호를 입력해 주세요.", new CommonDialog.DialogConfirmListener() {
                @Override
                public void onConfirm() {
                    metRetryPassword.requestFocus();
                }
                @Override
                public void onCancel() {
                }
            });
            return;
        }

        if(!password.equals(retryPassword)) {
            showDialogOneButton("비밀번호가 동일하지 않습니다.", new CommonDialog.DialogConfirmListener() {
                @Override
                public void onConfirm() {
                    metRetryPassword.requestFocus();
                }
                @Override
                public void onCancel() {
                }
            });
            return;
        }

        String phoneNumber = metPhoneNumber.getText().toString();
        if(StringUtil.isEmpty(phoneNumber)) {
            showDialogOneButton("전화번호를 입력해 주세요.", new CommonDialog.DialogConfirmListener() {
                @Override
                public void onConfirm() {
                    metPhoneNumber.requestFocus();
                }
                @Override
                public void onCancel() {
                }
            });
            return;
        }

        RequestJoinVO requestItem = new RequestJoinVO(mActivity);
        requestItem.email = email;
        requestItem.password = password;
        requestItem.phoneNumber = phoneNumber;
        networkJoin(requestItem);
    }

    private void networkJoin(final RequestJoinVO requestItem) {
        mApiController.join(mActivity, requestItem, new ApiController.ApiJoinListener() {
            @Override
            public void onSuccess(ResponseJoinVO item) {
                if(item == null) {
                    showDialogOneButton(getResources().getString(R.string.please_retry_network));
                    return;
                }

                if(item.isExistUser) {
                    CommonPreferences.resetPreferences(getApplicationContext());
                    showDialogOneButton("이미 해당 이메일로 가입된 사용자가 있습니다.", new CommonDialog.DialogConfirmListener() {
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

                if(!item.isConfirm) {
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
                CommonPreferences.putString(context, CommonPreferences.TAG_PHONE_NUMBER, requestItem.phoneNumber);
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
                        networkJoin(requestItem);
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
    public void moveLoginActivity() {
        Intent intent = new Intent(mActivity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        finish();
    }
    private void onBack() {
        moveLoginActivity();
        finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            onBack();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}