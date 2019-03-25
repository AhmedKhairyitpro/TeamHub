package com.abdelazim.x.teamhub.repository;

import android.content.Context;

import com.abdelazim.x.teamhub.home.HomeContract;
import com.abdelazim.x.teamhub.repository.local.LocalAccount;
import com.abdelazim.x.teamhub.repository.local.LocalDatabase;
import com.abdelazim.x.teamhub.repository.remote.GithubApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Repository {

    private Retrofit retrofit;
    private GithubApi githubApi;
    HomeContract.HomePresenterCallbacks homePresenterCallbacks;
    private LocalDatabase localDatabase;

    public Repository(HomeContract.HomePresenterCallbacks homePresenterCallbacks, Context context) {

        localDatabase = LocalDatabase.getInstance(context);
        this.homePresenterCallbacks = homePresenterCallbacks;

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/users/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        githubApi = retrofit.create(GithubApi.class);
    }

    public void getAccountsFromGitHub(List<String> namesList) {

        for (String name : namesList) {

            Call<Account> accountCall = githubApi.getAccount(name);

            accountCall.enqueue(new Callback<Account>() {
                @Override
                public void onResponse(Call<Account> call, Response<Account> response) {

                    Account account = response.body();
                    homePresenterCallbacks.accountFetched(account);
//                    saveAccountLocally(new LocalAccount(account.getLogin(),account.getAvatar_url(),account.getRepos()));
                }

                @Override
                public void onFailure(Call<Account> call, Throwable t) {

                }
            });
        }
    }

    public void saveAccountLocally(LocalAccount localAccount) {

        localDatabase.localAccountDao().insertLocalAccount(localAccount);
    }
}
