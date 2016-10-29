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
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
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
    private Observable<String> stringObservable;
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
        stringObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String name = Thread.currentThread().getName();
                subscriber.onNext(name);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
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
                            .subscribe(new Action1<String>() {
                                @Override
                                public void call(String s) {
                                    tvResult.setText(s);
                                    Toast.makeText(MainActivity.this, "" + Thread.currentThread().getName(), Toast.LENGTH_SHORT).show();
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Toast.makeText(MainActivity.this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }, new Action0() {
                                @Override
                                public void call() {
                                    Toast.makeText(MainActivity.this, "Completed!", Toast.LENGTH_SHORT).show();
                                }
                            });
                    break;
            }

        } else {
            Toast.makeText(this, "名称不得为空!", Toast.LENGTH_SHORT).show();
        }
    }
}
