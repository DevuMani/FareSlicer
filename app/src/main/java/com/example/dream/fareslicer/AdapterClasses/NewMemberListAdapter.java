package com.example.dream.fareslicer.AdapterClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dream.fareslicer.BeanClasses.MemberData;
import com.example.dream.fareslicer.R;
import com.example.dream.fareslicer.RetrofitClientAndInterface.RetrofitClient;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.QueryValue;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.MemberDeletion.MemberDeletionInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.MemberDeletion.MemberDeletionOutput;

import java.io.IOException;
import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by DevuMani on 10/5/2018.
 */

public class NewMemberListAdapter extends RecyclerView.Adapter<NewMemberListAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<MemberData> memberList;
    private String group_id="";
    private int flag=0;
    private String user_id="";

    public NewMemberListAdapter(Context context, ArrayList<MemberData> member_list) //new group
    {
        mContext=context;
        memberList=member_list;

    }

    public NewMemberListAdapter(Context context, ArrayList<MemberData> member_list,String group_id) //edit group
    {
        mContext=context;
        memberList=member_list;
        this.group_id=group_id;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.cust_row_member_list, parent,false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        MemberData m_data= memberList.get(position);

        holder.name.setText(m_data.getSelected_name());

        if(m_data.getSelected_user_id().equalsIgnoreCase(user_id))
        {
            holder.name.setText("You"); //To show this user is ourself
            holder.delete.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
//        Toast.makeText(mContext, "Size"+iconList.size(), Toast.LENGTH_SHORT).show();
        return memberList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private static final String TAG = "MemberList Adapter" ;
        public TextView name;
//        public ImageView icon;
        public ImageView delete;


        public MyViewHolder(View itemView) {
            super(itemView);

            SharedPreferences sp=mContext.getSharedPreferences("User",Context.MODE_PRIVATE);
            user_id=sp.getString("user_id","");

            name = itemView.findViewById(R.id.member_name);
            delete=itemView.findViewById(R.id.member_delete);

            if(group_id.equalsIgnoreCase(""))
            {
                delete.setVisibility(View.GONE);
            }
            else
            {
                delete.setVisibility(View.VISIBLE);
            }
            name.setOnClickListener(this);
            delete.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {

            if (view == name) {

                MemberData m_data = memberList.get(getAdapterPosition());
//                Toast.makeText(mContext, "Id : " +m_data.getSelected_user_id(), Toast.LENGTH_SHORT).show();

            }
            else if (view==delete) {

                MemberData m_data = memberList.get(getAdapterPosition());
//                Toast.makeText(mContext, "Id : " +m_data.getSelected_user_id(), Toast.LENGTH_SHORT).show();

                memberDeleteCall(group_id,m_data.getSelected_user_id(),m_data.getSelected_name());




            }
        }

        private void memberDeleteCall(String group_id, String selected_user_id, final String selected_name) {
// remove
////            String query="select group_id from tb_group where group_name=? and user_id=?";
//            ArrayList<String> value=new ArrayList();
//            value.add(group_id);
//            value.add(selected_user_id);
//
//            QueryValue queryValue=new QueryValue();
////            queryValue.setQuery(query);
//            queryValue.setValue(value);

            SharedPreferences sp= mContext.getSharedPreferences("User",Context.MODE_PRIVATE);
            String user_id=sp.getString("user_id","");

            MemberDeletionInput input=new MemberDeletionInput();
            input.setGroupId(group_id);
            input.setUserId(selected_user_id);
            input.setCuruserId(user_id);

            Call<MemberDeletionOutput> call=RetrofitClient.getInstance().getApi().memberDeletion(input);

            call.enqueue(new Callback<MemberDeletionOutput>() {
                @Override
                public void onResponse(Call<MemberDeletionOutput> call, Response<MemberDeletionOutput> response) {

                    if(response.code()==200) {

                        MemberDeletionOutput selectResult = response.body();

                        String success = "";
                        if (selectResult != null) {
                            success = selectResult.getStatus();

                            if (success.equalsIgnoreCase("true")) {

                                Toast.makeText(mContext, "Deleted member "+selected_name, Toast.LENGTH_SHORT).show();


                            }
                            else
                            {
                                Log.e(TAG,success);
                            Toast.makeText(mContext, "Couldn't delete member "+selected_name+"..Try Again!! ", Toast.LENGTH_SHORT).show();

                            }
                        }
                        else
                        {
                            Log.e(TAG,selectResult.toString());
//                        Snackbar.make(linearLayout,"Selection Failed",Snackbar.LENGTH_LONG).show();

                        }
                    }
                    else
                    {

                        String s="";
                        try {
                            if (response.errorBody() != null) {
                                s=response.errorBody().string();
                                Log.e(TAG,"Error body is "+s);
                            }
                            else
                            {
                                Log.e(TAG,"Error body is null");
                            }
                        } catch (IOException e) {
                            Log.e(TAG,e.getMessage());

                        }
                    }


                }

                @Override
                public void onFailure(Call<MemberDeletionOutput> call, Throwable t) {
//                Toast.makeText(NewGroup.this, "Insertion Call failed", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, t.getMessage());
                }
            });



        }
    }
}
