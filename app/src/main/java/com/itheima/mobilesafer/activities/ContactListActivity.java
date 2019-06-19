package com.itheima.mobilesafer.activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itheima.mobilesafer.bean.Contact;
import com.itheima.mobilesafer.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {

    public static final String TAG = "ContactListActivity";

    /**
     * 检测权限READ_CONTACTS的requestCode
     */
    public static final int PERMISSION_REQUEST_READ_CONTACTS = 1;

    private RecyclerView rv_contact;
    private List<Contact> mContactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        initUI();
        initData();
    }

    private void initUI() {
        rv_contact = (RecyclerView) findViewById(R.id.rv_contact);
    }

    private void initData() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
        } else {
            mContactList = getAllContacts(this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            //为避免错误，建议xml和代码都统一设置一致的orientation(因为这个花费了大量时间Debug)
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            rv_contact.setLayoutManager(layoutManager);
            rv_contact.setAdapter(new ContactListAdapter(mContactList));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_CONTACTS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mContactList = getAllContacts(this);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                    //为避免错误，建议xml和代码都统一设置一致的orientation(因为这个花费了大量时间Debug)
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    rv_contact.setLayoutManager(layoutManager);
                    rv_contact.setAdapter(new ContactListAdapter(mContactList));
                } else {
                    ToastUtil.show(getApplicationContext(), "授权失败，无法获取通讯录");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取所有的联系人数据 <uses-permission
     * android:name="android.permission.READ_CONTACTS" />
     *
     * @return
     */
    public static ArrayList<Contact> getAllContacts(Context context) {
        ArrayList<Contact> contactsList = new ArrayList<Contact>();// 所有的数据
        // 获取内容解析者
        ContentResolver contentResolver = context.getContentResolver();
        // Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        // 要查询的字段
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,// 名字
                ContactsContract.CommonDataKinds.Phone.NUMBER, // 电话
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID};// 联系人id


        // 参1 查询的uri 参2 要查询的字段 参3 查询的条件 参4 条件里?对应的值 参5 排序
        Cursor cursor = contentResolver
                .query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                String number = cursor.getString(1);
                int contactsId = cursor.getInt(2);
                Contact contact = new Contact(name, number);
                contactsList.add(contact);
            }
            cursor.close();
        }
        return contactsList;
    }

    class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {
        private List<Contact> contactsList;

        public ContactListAdapter(List<Contact> contactsList) {
            this.contactsList = contactsList;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private View view_contact;
            private TextView tv_name;
            private TextView tv_phone;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                view_contact = itemView;
                tv_name = (TextView) itemView.findViewById(R.id.tv_name);
                tv_phone = (TextView) itemView.findViewById(R.id.tv_phone);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recyclerview_contact_item, viewGroup, false);
            final ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.view_contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("name", viewHolder.tv_name.getText().toString());
                    intent.putExtra("phone", viewHolder.tv_phone.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
            Contact contact = contactsList.get(position);
            viewHolder.tv_name.setText(contact.getName());
            viewHolder.tv_phone.setText(contact.getPhone());
            Log.d(TAG, "onBindViewHolder: " + viewHolder.tv_phone.getText().toString());
        }

        @Override
        public int getItemCount() {
            return contactsList.size();
        }
    }
}
