package com.example.lenovo.rxjavademo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.et_sp_name)
    EditText etSpName;
    @Bind(R.id.et_sp_content)
    EditText etSpContent;
    @Bind(R.id.btn_sp_write)
    Button btnSpWrite;
    @Bind(R.id.btn_sp_read)
    Button btnSpRead;
    @Bind(R.id.tv_result)
    TextView tvResult;
    private SharedPreferences sharedPreferences;
    private Observable<Byte> stringObservable;
    private String content;
    private String name;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        btnSpRead.setOnClickListener(this);
        btnSpWrite.setOnClickListener(this);
        sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        String[] strings = {"dskhs", "klhdsewjkhe3", "hjqhggu"};
        stringObservable = Observable.from(strings)
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return s.length() > 3;
                    }
                })
                .flatMap(new Func1<String, Observable<Byte>>() {
                    @Override
                    public Observable<Byte> call(String s) {
                        byte[] bytes = s.getBytes();
                        Byte[] bytes1 = new  Byte[bytes.length];
                        for (int i = 0; i < bytes.length; i++) {
                            bytes1[i] = bytes[i];
                        }
                        return Observable.from(bytes1);
                    }
                })
//                .throttleFirst(15, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public void onClick(View view) {
        name = etSpName.getText().toString().trim();
        content = etSpContent.getText().toString().trim();
        if (!TextUtils.isEmpty(name)) {
            switch (view.getId()) {
                case R.id.btn_sp_read:

                    break;
                case R.id.btn_sp_write:
                    subscription = stringObservable.observeOn(AndroidSchedulers.mainThread())
//                            .throttleFirst(15,TimeUnit.MILLISECONDS)
                            .subscribe(new Subscriber<Byte>() {
                                @Override
                                public void onCompleted() {
                                    Utils.showLog("onCompleted " + Thread.currentThread().getName());
                                }

                                @Override
                                public void onError(Throwable throwable) {

                                }

                                @Override
                                public void onNext(Byte s) {
                                    tvResult.setText(Thread.currentThread().getName());
                                    Utils.showLog(s.toString());
                                }

                                @Override
                                public void onStart() {
                                    super.onStart();
                                    String name = Thread.currentThread().getName();
                                    Utils.showLog(name);
                                    Toast.makeText(MainActivity.this, "onStart " + name, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void setProducer(Producer p) {
                                    super.setProducer(p);
                                    Utils.showLog("setProducer " + Thread.currentThread().getName());
                                }
                            });
                    break;
            }

        } else {
            Toast.makeText(this, "名称不得为空!", Toast.LENGTH_SHORT).show();
        }
    }
}
