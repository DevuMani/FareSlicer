package com.example.dream.fareslicer.FragmentClasses;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.dream.fareslicer.Activities.NewGroup;
import com.example.dream.fareslicer.Activities.RegistrationActivity;
import com.example.dream.fareslicer.AdapterClasses.GroupListAdapter;
import com.example.dream.fareslicer.BeanClasses.GroupData;
import com.example.dream.fareslicer.R;
import com.example.dream.fareslicer.RetrofitClientAndInterface.RetrofitClient;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.QueryValue;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupListFetch.GroupListFetchInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupListFetch.GroupListFetchOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.GroupListFetch.GroupListFetchResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.ShowAmountHome.ShowAmountInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.ShowAmountHome.ShowAmountOutput;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import ch.halcyon.squareprogressbar.SquareProgressBar;
import ch.halcyon.squareprogressbar.utils.PercentStyle;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {

    private static final String TAG = "Group Fragment" ;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    LinearLayout no_data_layout,data_layout,heading_layout;

    private FloatingActionButton fab;
    private TextView total_currency,total_text,home_total_amount,total_amount,own_currency,owe_currency,owe_amount,own_amount;
    private LinearLayout linearHead,total_text_layout,all_settled_up,layout_you_owe,layout_you_own;
    private RelativeLayout fragment_totalLayout,main_Layout;
    SwipeRefreshLayout swipeRefreshLayout;
    SquareProgressBar squareProgressBar;//square progressbar

    private String user_id; //To store shared preference value
    private String currency="";

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
        this.user_id =sharedPreferences.getString("user_id","");
//        this.currency=getResources().getString(R.string.currency);//remove currency
        this.currency=sharedPreferences.getString("user_currency","");
    }

    @Override
    public void onResume() {
        super.onResume();

        if(user_id.equals(""))
        {
            Toast.makeText(getActivity(), "Connection time out", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(getActivity(),RegistrationActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initialize(view);

        return view;

    }

    private void initialize(View rootView) {

        swipeRefreshLayout=rootView.findViewById(R.id.home_swiperefresh);

        squareProgressBar=rootView.findViewById(R.id.sprogressbar);//square progressbar
        progressBar=rootView.findViewById(R.id.home_progressBar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView=rootView.findViewById(R.id.home_recyclerView);
        main_Layout=rootView.findViewById(R.id.main_Layout);
        main_Layout.setVisibility(View.GONE);

        fab=rootView.findViewById(R.id.home_fab);
        fragment_totalLayout=rootView.findViewById(R.id.fragment_totalLayout);

        all_settled_up=rootView.findViewById(R.id.all_settled_up);
        layout_you_owe=rootView.findViewById(R.id.layout_you_owe);
        layout_you_own=rootView.findViewById(R.id.layout_you_own);

        total_text_layout=rootView.findViewById(R.id.total_text_layout);
        total_text=rootView.findViewById(R.id.total_text);

        total_currency=rootView.findViewById(R.id.total_currency);
        total_amount=rootView.findViewById(R.id.total_amount);
        home_total_amount=rootView.findViewById(R.id.home_total_amount);

        owe_currency=rootView.findViewById(R.id.home_heading_owe_currency);
        owe_amount=rootView.findViewById(R.id.owe_amount);
        own_currency=rootView.findViewById(R.id.home_heading_own_currency);
        own_amount=rootView.findViewById(R.id.own_amount);

        heading_layout=rootView.findViewById(R.id.home_transaction_heading_layout);
        no_data_layout=rootView.findViewById(R.id.home_no_transaction_layout);
        no_data_layout.setVisibility(View.GONE);
        data_layout=rootView.findViewById(R.id.home_transaction_layout);
        data_layout.setVisibility(View.GONE);
        linearHead=rootView.findViewById(R.id.linear_head_text);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showAmountHome();

//        setGroupList1();
        setGroupList();

        setRecyclerViewData();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(getActivity(),NewGroup.class);
                intent.putExtra("g_id","");
                startActivity(intent);
            }
        });

        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG, "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        myUpdateOperation();
                    }
                }
        );
    }

    private void myUpdateOperation() {

        showAmountHome();
        setGroupList();
        setRecyclerViewData();

    }

    private void setGroupList() {

        GroupListFetchInput input=new GroupListFetchInput();
        input.setUserId(user_id);

        groupList= new ArrayList<>();

        Call<GroupListFetchResult> call=RetrofitClient.getInstance().getApi().groupListFetch(input);

        call.enqueue(new Callback<GroupListFetchResult>() {
            @Override
            public void onResponse(Call<GroupListFetchResult> call, Response<GroupListFetchResult> response) {


                if (response.code()==200) {


                    GroupListFetchResult groupListFetchResult = response.body();

                    List<GroupListFetchOutput> groupListFetchOutputs = groupListFetchResult.getOutput();

                    for (int i = 0; i < groupListFetchOutputs.size(); i++) {

                        GroupListFetchOutput output = groupListFetchOutputs.get(i);

                        groupData = new GroupData(output.getGroupId(), output.getGroupName(), output.getAmount(), output.getOwe(), output.getOwn());

                        groupList.add(groupData);
                    }


                    if (groupList.isEmpty()) {

                        no_data_layout.setVisibility(View.VISIBLE);
                        heading_layout.setVisibility(View.GONE);
                        data_layout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);


                    } else {
                        no_data_layout.setVisibility(View.GONE);
                        heading_layout.setVisibility(View.VISIBLE);
                        data_layout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();

                    }

                }
                else
                {
                    String s="";
                    try {
                        if (response.errorBody() != null) {
                            s=response.errorBody().string();
                            Log.e("Selection","Error body is "+s);
                        }
                        else
                        {
                            Log.e("Selection","Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e("Selection",e.getMessage());

                    }
                }
            }

            @Override
            public void onFailure(Call<GroupListFetchResult> call, Throwable t) {

                Toast.makeText(getContext(), "Selection Call failed", Toast.LENGTH_SHORT).show();
                Log.e("Selection", t.getMessage());
            }
        });
    }

    private void showAmountHome() {

        ShowAmountInput showAmountInput =new ShowAmountInput();
        showAmountInput.setUserId(user_id);

        Call<ShowAmountOutput> call=RetrofitClient.getInstance().getApi().showAmountHome(showAmountInput);

        call.enqueue(new Callback<ShowAmountOutput>() {
            @Override
            public void onResponse(Call<ShowAmountOutput> call, Response<ShowAmountOutput> response) {

                main_Layout.setVisibility(View.VISIBLE); //To make the main layout visible in the response of any one of the api calls
                swipeRefreshLayout.setRefreshing(false);
                if (response.code() == 200) {

                    ShowAmountOutput showAmountOutput = response.body();

                    String amount=showAmountOutput.getAmount();
                    amount=String.format("%.2f", Double.parseDouble(amount));

                    Double owed = Math.abs(Double.parseDouble(showAmountOutput.getOwe()));
                    String owe=String.format("%.2f", owed);

                    String own=showAmountOutput.getOwn();
                    own=String.format("%.2f", Double.parseDouble(own));

                    if(amount.equals("0"))
                    {
                        total_text_layout.setVisibility(View.GONE);
                        all_settled_up.setVisibility(View.VISIBLE);
                    }
                    else
                    {

                        all_settled_up.setVisibility(View.GONE);
                        total_text_layout.setVisibility(View.VISIBLE);
                        total_currency.setText(" "+ currency+" ");


                        if (Double.parseDouble(amount)<0)//Amount you owe
                        {
                            //To remove -ve sign
                            Double x = Math.abs(Double.parseDouble(amount));
                            amount=""+x;

                            total_amount.setText(amount);//remove
                            total_amount.setTextColor(getResources().getColor(R.color.warning_red));//remove

                            home_total_amount.setText(amount);
                            home_total_amount.setTextColor(getResources().getColor(R.color.warning_red));

                            //square progressbar
                            squareProgressBar.showProgress(true);
                            PercentStyle percentStyle = new PercentStyle(Paint.Align.CENTER, 20, true);
                            percentStyle.setCustomText(amount);
                            Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(R.drawable.add_person));
                            squareProgressBar.setImageBitmap(bitmap);
                            squareProgressBar.setPercentStyle(percentStyle);

                            squareProgressBar.setColor("#00BD98");
                            squareProgressBar.setHoloColor(R.color.warning_red);
                            percentStyle.setTextColor(getResources().getColor(R.color.caribbean_green));


                            total_text.setTextColor(getResources().getColor(R.color.warning_red));
                            total_currency.setTextColor(getResources().getColor(R.color.warning_red));

                        }
                        else if (Double.parseDouble(amount)>=0)//Amount you own
                        {
                            total_amount.setText(amount);//remove
                            total_amount.setTextColor(getResources().getColor(R.color.caribbean_green));//remove

                            home_total_amount.setText(amount);
                            home_total_amount.setTextColor(getResources().getColor(R.color.caribbean_green));

                            //square progressbar
                            squareProgressBar.showProgress(true);
                            PercentStyle percentStyle = new PercentStyle(Paint.Align.CENTER, 20, true);
                            percentStyle.setCustomText(amount);
                            Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(R.drawable.add_person));
                            squareProgressBar.setImageBitmap(bitmap);
                            squareProgressBar.setPercentStyle(percentStyle);

                            squareProgressBar.setColor("#00BD98");
                            squareProgressBar.setHoloColor(R.color.warning_red);
                            percentStyle.setTextColor(getResources().getColor(R.color.caribbean_green));



                            total_text.setTextColor(getResources().getColor(R.color.caribbean_green));
                            total_currency.setTextColor(getResources().getColor(R.color.caribbean_green));

                        }
                    }
//remove

//                    if(owe.equals("0"))
//                    {
//                        layout_you_owe.setVisibility(View.GONE);
//                    }
//                    else
//                    {
                        layout_you_owe.setVisibility(View.VISIBLE);
                        owe_currency.setText(" "+ currency+" ");//hardcoding

                        //To remove -ve sign
                        Double x = Math.abs(Double.parseDouble(owe));
                        owe=""+x;

                        owe_amount.setText(owe);
                        owe_currency.setTextColor(getResources().getColor(R.color.warning_red));
                        owe_amount.setTextColor(getResources().getColor(R.color.warning_red));



//remove

//                    }
//

//                    if (own.equals("0"))
//                    {
//                        layout_you_own.setVisibility(View.GONE);
//                    }
//                    else
//                    {
                        layout_you_own.setVisibility(View.VISIBLE);
                        own_currency.setText(" "+ currency+" ");

                        own_amount.setText(own);
                        own_currency.setTextColor(getResources().getColor(R.color.caribbean_green));
                        own_amount.setTextColor(getResources().getColor(R.color.caribbean_green));

//remove

//                    }


//                    if (amount.equals("0"))
//                    {
//                        total_text.setVisibility(View.GONE);
//                        all_settled_up.setVisibility(View.VISIBLE);
//
//                        if(owe.equals("0")){
//                            layout_you_owe.setVisibility(View.GONE);
//                        }
//                        else
//                        {
//                            layout_you_owe.setVisibility(View.VISIBLE);
//                            owe_currency.setText("$");//Just harcoding
//                            owe_amount.setText(amount);
//                            owe_currency.setTextColor(Color.RED);
//                            owe_amount.setTextColor(Color.RED);
//                        }
//
//                        if (own.equals("0")){
//                            layout_you_own.setVisibility(View.GONE);
//                        }
//                        else
//                        {
//                            layout_you_own.setVisibility(View.VISIBLE);
//                            own_currency.setText("$");//Just harcoding
//                            own_amount.setText(amount);
//                            own_currency.setTextColor(getResources().getColor(R.color.caribbean_green));
//                            own_amount.setTextColor(getResources().getColor(R.color.caribbean_green));
//                        }
//
//                    }
//                    else if (Double.parseDouble(amount)<0)//Amount you owe
//                    {
//                        total_text.setVisibility(View.GONE);
//                        all_settled_up.setVisibility(View.GONE);
//
//                        layout_you_own.setVisibility(View.GONE);
//
//                        layout_you_owe.setVisibility(View.VISIBLE);
//                        owe_currency.setText("$");//Just harcoding
//                        owe_amount.setText(amount);
//                        owe_currency.setTextColor(Color.RED);
//                        owe_amount.setTextColor(Color.RED);
//
//                    }
//                    else if (Double.parseDouble(amount)>0)
//                    {
//                        all_settled_up.setVisibility(View.GONE);
//                        layout_you_owe.setVisibility(View.GONE);
//
//                        layout_you_own.setVisibility(View.VISIBLE);
//                        own_currency.setText("$");//Just harcoding
//                        own_amount.setText(amount);
//                        own_currency.setTextColor(Color.GREEN);
//                        own_amount.setTextColor(Color.GREEN);
//                    }
                }
                else {

                    String s="";
                    try {
                        if (response.errorBody() != null) {
                            s=response.errorBody().string();
                            Log.e("Selection","Error body is "+s);
                        }
                        else
                        {
                            Log.e("Selection","Error body is null");
                        }
                    } catch (IOException e) {
                        Log.e("Selection",e.getMessage());

                    }
                }


            }
            @Override
            public void onFailure(Call<ShowAmountOutput> call, Throwable t) {
                Log.e("Selection",t.getMessage());

                swipeRefreshLayout.setRefreshing(false);
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

    //Remove
//    private void setGroupList1() {
//
//        SharedPreferences sp=getContext().getSharedPreferences("User",MODE_PRIVATE);
//        String user_id=sp.getString("user_id","");
//
//        String query="select DISTINCT t1.group_id,t1.group_name,t1.group_currency from tb_group t1,tb_member t2 where t1.group_id = t2.group_id and t2.user_id =?";
//        ArrayList<String> value=new ArrayList();
//        value.add(user_id);
//
//        QueryValue queryValue=new QueryValue();
//        queryValue.setQuery(query);
//        queryValue.setValue(value);
//
//
//        groupList= new ArrayList<>();
//
//        Call<CallResult> call= RetrofitClient.getInstance().getApi().select(queryValue);
//
//        call.enqueue(new Callback<CallResult>() {
//            @Override
//            public void onResponse(Call<CallResult> call, Response<CallResult> response) {
//
//                if(response.code()==200) {
//
//                    CallResult selectResult=response.body();
//
//                    String success= "";
//                    if (selectResult != null) {
//                        success = selectResult.getStatus();
//
//                        if (success.equalsIgnoreCase("true")) {
//
//                            List<CallOutput> output = selectResult.getOutput();
//                            for(int i=0;i<output.size();i++) {
//                                CallOutput item = output.get(i);
//                                List<String> list = item.getValue();
//                                String id = list.get(0);
//                                String name=list.get(1);
//                                String currency=list.get(2);
//                                groupData=new GroupData(id,name,currency);
//
//                                groupList.add(groupData);
//
//
//                            }
//
//                            adapter.notifyDataSetChanged();
//                            if(groupList.isEmpty())
//                            {
//
//                                no_data_layout.setVisibility(View.VISIBLE);
//                                heading_layout.setVisibility(View.GONE);
//                                data_layout.setVisibility(View.GONE);
//                                progressBar.setVisibility(View.GONE);
//
//
//                            }
//                            else
//                            {
//                                no_data_layout.setVisibility(View.GONE);
//                                heading_layout.setVisibility(View.VISIBLE);
//                                data_layout.setVisibility(View.VISIBLE);
//                                progressBar.setVisibility(View.GONE);
//
//                            }
//
//                        } else {
//                            Log.e("Selection", "No data found");
//
//                            no_data_layout.setVisibility(View.VISIBLE);
//                            data_layout.setVisibility(View.GONE);
//                            progressBar.setVisibility(View.GONE);
//                            fab.show();
//                        }
//                    }
//                    else
//                    {
//                        Log.e("Selection", "Success is null ");
//                    }
//
//                }
//                else {
//                    String s="";
//                    try {
//                        if (response.errorBody() != null) {
//                            s=response.errorBody().string();
//                            Log.e("Selection","Error body is "+s);
//                        }
//                        else
//                        {
//                            Log.e("Selection","Error body is null");
//                        }
//                    } catch (IOException e) {
//                        Log.e("Selection",e.getMessage());
//
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CallResult> call, Throwable t) {
//
//                Toast.makeText(getContext(), "Selection Call failed", Toast.LENGTH_SHORT).show();
//                Log.e("Selection", t.getMessage());
//            }
//
//        });
//
//    }

}
