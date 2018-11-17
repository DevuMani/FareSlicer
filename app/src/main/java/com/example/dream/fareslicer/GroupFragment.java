package com.example.dream.fareslicer;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.dream.fareslicer.AdapterClasses.ContactListAdapter;
import com.example.dream.fareslicer.AdapterClasses.GroupListAdapter;
import com.example.dream.fareslicer.BeanClasses.GroupData;
import com.example.dream.fareslicer.RetrofitClientAndInterface.RetrofitClient;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CallOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CallResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.QueryValue;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {

    ProgressBar progressBar;
    RecyclerView recyclerView;
    LinearLayout no_data_layout,data_layout;

    String count_parameter="";

    String cLeaves="";
    String aLeaves="";

    FloatingActionButton fab;
    TextView owe_amount,amount_own;
    LinearLayout linearHead;

    private String userID;
    private ArrayList<GroupData> groupList;
    private LinearLayoutManager linearLayoutManager;
    private GroupListAdapter adapter;
    private GroupData groupData;


    public GroupFragment() {
        // Required empty public constructor

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("User",MODE_PRIVATE);
        this.userID=sharedPreferences.getString("user_phone","");
        userID="0";

    }

    @Override
    public void onResume() {
        super.onResume();

//        SharedPreferences sp=getActivity().getSharedPreferences("UserLogin",MODE_PRIVATE);
//        userID=sp.getString("userID","");
        if(!userID.equals(""))
        {
//            leaveCall();
//            countCall();
        }
        else
        {
            Toast.makeText(getActivity(), "Connection time out", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(getActivity(),RegistrationActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initialize(view);

//         Inflate the layout for this fragment
        return view;

    }

    private void initialize(View rootView) {

        progressBar=rootView.findViewById(R.id.home_progressBar);
        recyclerView=rootView.findViewById(R.id.home_recyclerView);

        fab=rootView.findViewById(R.id.home_fab);
        owe_amount=rootView.findViewById(R.id.owe_amount);
        amount_own=rootView.findViewById(R.id.amount_own);

        no_data_layout=rootView.findViewById(R.id.home_no_transaction_layout);
        data_layout=rootView.findViewById(R.id.home_transaction_layout);

        linearHead=rootView.findViewById(R.id.linear_head_text);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setGroupList();
        setRecyclerViewData();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getActivity(),NewGroup.class);
                startActivity(intent);
            }
        });



    }



    private void setRecyclerViewData() {

        linearLayoutManager=new LinearLayoutManager(getContext());
        adapter = new GroupListAdapter(getContext(), groupList);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);

    }

    private void setGroupList() {

        SharedPreferences sp=getContext().getSharedPreferences("User",MODE_PRIVATE);
        String user_id=sp.getString("user_id","");

        String query="select t1.group_id,t1.group_name from tb_group t1,tb_member t2 where t1.group_id = t2.group_id and t2.user_id =?";
        ArrayList<String> value=new ArrayList();
        value.add(user_id);

        QueryValue queryValue=new QueryValue();
        queryValue.setQuery(query);
        queryValue.setValue(value);


        groupList= new ArrayList<>();

        Call<CallResult> call= RetrofitClient.getInstance().getApi().select(queryValue);

        call.enqueue(new Callback<CallResult>() {
            @Override
            public void onResponse(Call<CallResult> call, Response<CallResult> response) {

                if(response.code()==200) {

                    CallResult selectResult=response.body();

                    String success= "";
                    if (selectResult != null) {
                        success = selectResult.getStatus();

                        if (success.equalsIgnoreCase("true")) {

                            List<CallOutput> output = selectResult.getOutput();
                            for(int i=0;i<output.size();i++) {
                                CallOutput item = output.get(i);
                                List<String> list = item.getValue();
                                String id = list.get(0);
                                String name=list.get(1);
                                groupData=new GroupData(id,name);

                                groupList.add(groupData);


                            }

                            adapter.notifyDataSetChanged();
                            if(groupList.isEmpty())
                            {
                                no_data_layout.setVisibility(View.VISIBLE);
                                data_layout.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);


                            }
                            else
                            {
                                no_data_layout.setVisibility(View.GONE);
                                data_layout.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);

                            }

                        } else {
                            Log.e("Selection", "No data found");

                            no_data_layout.setVisibility(View.VISIBLE);
                            data_layout.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            fab.show();
                        }
                    }
                    else
                    {
                        Log.e("Selection", "Success is null ");
                    }

                }
                else {
                    String s="";
                    try {
                        if (response.errorBody() != null) {
                            s=response.errorBody().string();
                        }
                        else
                        {
                            Log.e("Selection","Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e("Insertion",e.getMessage());

                    }
                }
            }

            @Override
            public void onFailure(Call<CallResult> call, Throwable t) {

                Toast.makeText(getContext(), "Selection Call failed", Toast.LENGTH_SHORT).show();
                Log.e("Selction", t.getMessage());
            }

        });

    }

}
