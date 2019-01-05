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
import com.example.dream.fareslicer.AdapterClasses.TransactionListAdapter;
import com.example.dream.fareslicer.BeanClasses.TransactionData;
import com.example.dream.fareslicer.BeanClasses.TransactionFragmentData;
import com.example.dream.fareslicer.R;
import com.example.dream.fareslicer.RetrofitClientAndInterface.RetrofitClient;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.CallResult;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.CommonInputOutputClasses.QueryValue;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.ShowAmountHome.ShowAmountInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.ShowAmountHome.ShowAmountOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.TransactionMembersList.TransactionMembersListInput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.TransactionMembersList.TransactionMembersListOutput;
import com.example.dream.fareslicer.RetrofitInputOutputClasses.TransactionMembersList.TransactionMembersListResult;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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

public class TransactionFragment extends Fragment {

    private final static String TAG="Transaction Fragment";

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private LinearLayout no_data_layout, data_layout;
    private CardView all_settled_up;

    private int tab_position = 0;

    private FloatingActionButton fab;
//    private TextView total_currency, total_text,total_amount;remove
    private TextView home_total_amount,own_currency, owe_currency, owe_amount, own_amount;
//    private LinearLayout  total_text_layout;//remove
    private LinearLayout heading_layout,  layout_you_owe, layout_you_own;
    private RelativeLayout fragment_totalLayout,main_Layout;
    private SwipeRefreshLayout swipeRefreshLayout;
//    SquareProgressBar squareProgressBar;//square progressbar remove

    private String userID,currency;
    private ArrayList<TransactionFragmentData> transactionList;
    private LinearLayoutManager linearLayoutManager;
    private TransactionListAdapter adapter;
    private TransactionData transactionData;

    public TransactionFragment() {
        // Required empty public constructor

    }

    //no need remove
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.tab_position = getArguments().getInt("tab");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("User", MODE_PRIVATE);
        this.userID = sharedPreferences.getString("user_id", "");
//        this.currency=getResources().getString(R.string.currency); remove currency
        this.currency=sharedPreferences.getString("user_currency","");

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!userID.equals("")) {
        } else {
            Toast.makeText(getActivity(), "Connection time out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), RegistrationActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initialize(rootView);

//         Inflate the layout for this fragment
        return rootView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar.setVisibility(View.VISIBLE);

        showAmountHome();

//        setRecyclerViewData();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), NewGroup.class);
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

    private void initialize(View rootView) {

        swipeRefreshLayout=rootView.findViewById(R.id.home_swiperefresh);
//        squareProgressBar=rootView.findViewById(R.id.sprogressbar);//square progressbar remove

        progressBar = rootView.findViewById(R.id.home_progressBar);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = rootView.findViewById(R.id.home_recyclerView);
        main_Layout=rootView.findViewById(R.id.main_Layout);
        main_Layout.setVisibility(View.GONE);

        fab = rootView.findViewById(R.id.home_fab);
        fab.hide();

        all_settled_up = rootView.findViewById(R.id.all_settled_up);
        layout_you_owe = rootView.findViewById(R.id.layout_you_owe);
        layout_you_own = rootView.findViewById(R.id.layout_you_own);

        home_total_amount=rootView.findViewById(R.id.home_total_amount);

        owe_currency = rootView.findViewById(R.id.home_heading_owe_currency);
        owe_amount = rootView.findViewById(R.id.owe_amount);
        own_currency = rootView.findViewById(R.id.home_heading_own_currency);
        own_amount = rootView.findViewById(R.id.own_amount);

        heading_layout=rootView.findViewById(R.id.home_transaction_heading_layout); //Main Layout for showing total amount details
        heading_layout.setVisibility(View.GONE);
//        lv_color_for_CardView=rootView.findViewById(R.id.home_color_cardview);

        no_data_layout=rootView.findViewById(R.id.home_no_transaction_layout);
        no_data_layout.setVisibility(View.GONE);
        data_layout=rootView.findViewById(R.id.home_transaction_layout);
        data_layout.setVisibility(View.GONE);
//        linearHead=rootView.findViewById(R.id.linear_head_text);

    }

    private void setGroupList() {

        TransactionMembersListInput input=new TransactionMembersListInput();
        input.setUserId(userID);

        transactionList=new ArrayList<>();

        Call<TransactionMembersListResult> call=RetrofitClient.getInstance().getApi().transactionMembersList(input);

        call.enqueue(new Callback<TransactionMembersListResult>() {
            @Override
            public void onResponse(Call<TransactionMembersListResult> call, Response<TransactionMembersListResult> response) {


                if (response.code()==200) {

                    TransactionMembersListResult result = response.body();


                    try {

                        List<TransactionMembersListOutput> membersList = null;
                        if (result != null) {
                            membersList = result.getOutput();


                            for (int i = 0; i < membersList.size(); i++) {

                                TransactionMembersListOutput membersListData = membersList.get(i);

                                TransactionFragmentData transactionFragmentData = new TransactionFragmentData(membersListData.getUserId(), membersListData.getUserName(), membersListData.getAmount());

                                transactionList.add(transactionFragmentData);

                            }
                        }
                    } catch (NullPointerException n) {
                        Log.e("Transaction Fragment", n.getMessage());
                    }

                    if (transactionList.isEmpty()) {
                        no_data_layout.setVisibility(View.VISIBLE);
                        heading_layout.setVisibility(View.GONE);
                        data_layout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);

                    } else {
                        no_data_layout.setVisibility(View.GONE);
                        heading_layout.setVisibility(View.VISIBLE);
                        data_layout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
//                        adapter.notifyDataSetChanged();
                        setRecyclerViewData();
                    }
                }
                else
                {

                    //Warning snackbar
                    Snackbar.make(main_Layout,"Sorry for your inconvenience.. Please try refreshing page by swiping down..",Snackbar.LENGTH_SHORT).show();

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
            public void onFailure(Call<TransactionMembersListResult> call, Throwable t) {

                //Warning snackbar
                Snackbar.make(main_Layout,"Sorry for your inconvenience.. Please try refreshing page by swiping down..",Snackbar.LENGTH_SHORT).show();

//                Toast.makeText(getContext(), "Selection Call failed", Toast.LENGTH_SHORT).show();
                Log.e("Selection", t.getMessage());
            }
        });

    }


    private void showAmountHome() {

        ShowAmountInput showAmountInput =new ShowAmountInput();
        showAmountInput.setUserId(userID);

        Call<ShowAmountOutput> call=RetrofitClient.getInstance().getApi().showAmountHome(showAmountInput);

        call.enqueue(new Callback<ShowAmountOutput>() {
            @Override
            public void onResponse(Call<ShowAmountOutput> call, Response<ShowAmountOutput> response) {

                main_Layout.setVisibility(View.VISIBLE); //To make the main layout visible in the response of any one of the api calls
                swipeRefreshLayout.setRefreshing(false);

                if (response.code() == 200) {

                    setGroupList();


                    heading_layout.setVisibility(View.VISIBLE); //to make this layout visible only if response is successfull

                    ShowAmountOutput showAmountOutput = response.body();

                    String amount=showAmountOutput.getAmount();
                    amount=String.format("%.2f", Double.parseDouble(amount));

                    Double owed = Math.abs(Double.parseDouble(showAmountOutput.getOwe()));
                    String owe=String.format("%.2f", owed);

                    String own=showAmountOutput.getOwn();
                    own=String.format("%.2f", Double.parseDouble(own));

                    if(amount.equals("0.00"))
                    {
                        all_settled_up.setVisibility(View.VISIBLE);
                        home_total_amount.setText(currency+" "+amount);
                        home_total_amount.setTextColor(getResources().getColor(R.color.caribbean_green));
                    }
                    else
                    {

                        all_settled_up.setVisibility(View.GONE);

                        if (Double.parseDouble(amount)<0)//Amount you owe
                        {
                            //To remove -ve sign
                            Double x = Math.abs(Double.parseDouble(amount));
                            amount=""+x;

                            home_total_amount.setText(currency+" "+amount);
                            home_total_amount.setTextColor(getResources().getColor(R.color.warning_red));
//                            lv_color_for_CardView.setBackground(getResources().getDrawable(R.drawable.cardview_outline_red)); to give border color for caredview

                        }
                        else if (Double.parseDouble(amount)>=0)//Amount you own
                        {
//

                            home_total_amount.setText(currency+" "+amount);
                            home_total_amount.setTextColor(getResources().getColor(R.color.caribbean_green));
//                            lv_color_for_CardView.setBackground(getResources().getDrawable(R.drawable.cardview_outline_green));to give border color for caredview


                        }
                    }


//                    Amount Owe(to give)

                    layout_you_owe.setVisibility(View.VISIBLE);
                    owe_currency.setText(" "+ currency+" ");//hardcoding

                    //To remove -ve sign
                    Double x = Math.abs(Double.parseDouble(owe));
                    owe=""+x;

                    owe_amount.setText(owe);
                    owe_currency.setTextColor(getResources().getColor(R.color.warning_red));
                    owe_amount.setTextColor(getResources().getColor(R.color.warning_red));

//                  Amount own(to get)

                    layout_you_own.setVisibility(View.VISIBLE);
                    own_currency.setText(" "+ currency+" ");

                    own_amount.setText(own);
                    own_currency.setTextColor(getResources().getColor(R.color.caribbean_green));
                    own_amount.setTextColor(getResources().getColor(R.color.caribbean_green));

                }
                else {

                    heading_layout.setVisibility(View.GONE);

                    //Warning snackbar
                    Snackbar.make(main_Layout,"Sorry for your inconvenience.. Please try refreshing page by swiping down..",Snackbar.LENGTH_SHORT).show();


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

                //Warning snackbar
                Snackbar.make(main_Layout,"Sorry for your inconvenience.. Please try refreshing page by swiping down..",Snackbar.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);

            }
        });


    }

    private void myUpdateOperation() {

        showAmountHome();
//        setGroupList();
//        setRecyclerViewData();
    }

    private void setRecyclerViewData() {

        linearLayoutManager = new LinearLayoutManager(getContext());
        adapter = new TransactionListAdapter(getContext(), transactionList);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);

    }

}